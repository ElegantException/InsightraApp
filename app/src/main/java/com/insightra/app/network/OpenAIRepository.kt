package com.insightra.app.network

import android.content.ContentResolver
import android.net.Uri
import com.insightra.app.core.AppConstants
import com.insightra.app.data.db.ItemEntity
import com.insightra.app.network.model.ComparisonResultPayload
import com.insightra.app.network.model.VisionExtractResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class OpenAIRepository(
    private val service: OpenAIService,
    private val contentResolver: ContentResolver,
    private val cacheDirProvider: () -> File
) {
    suspend fun verifyKey(apiKey: String): Boolean {
        return try {
            val resp = service.listModels("Bearer $apiKey")
            resp.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    suspend fun extractFromImage(apiKey: String, imageUri: Uri): VisionExtractResult {
        return withContext(Dispatchers.IO) {
            val tmp = copyUriToTemp(imageUri)
            val rb = tmp.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", tmp.name, rb)
            val model = AppConstants.MODEL_VISION.toRequestBody("text/plain".toMediaTypeOrNull())
            val inputJson = """{"task":"extract_brand_name_size","output":"json"}"""
                .toRequestBody("application/json".toMediaTypeOrNull())
            val resp: Response<Unit> = service.uploadImageAndExtract(
                auth = "Bearer $apiKey",
                file = part,
                model = model,
                input = inputJson
            )
            tmp.delete()
            VisionExtractResult(brand = "", name = "", size = "1 unit")
        }
    }

    suspend fun generateComparison(apiKey: String, items: List<ItemEntity>): ComparisonResultPayload {
        val prompt = buildRequestPayload(items)
        val body: RequestBody = prompt.toRequestBody("application/json".toMediaTypeOrNull())
        return try {
            val resp = service.generateComparison("Bearer $apiKey", body)
            ComparisonResultPayload(
                table = emptyList(),
                ratings = emptyList(),
                prosCons = emptyList(),
                recommendations = emptyList()
            )
        } catch (_: Exception) {
            ComparisonResultPayload(
                table = emptyList(),
                ratings = emptyList(),
                prosCons = emptyList(),
                recommendations = emptyList()
            )
        }
    }

    private fun buildRequestPayload(items: List<ItemEntity>): String {
        val itemsJson = items.joinToString(prefix = "[", postfix = "]") {
            """{"brand":"${it.brand}","name":"${it.name}","size":"${it.size}"}"""
        }
        return """
        {
          "model": "${AppConstants.MODEL_TEXT}",
          "input": [
            {
              "role": "system",
              "content": "You assist with product comparisons. Use public sources to synthesize ratings and review counts."
            },
            {
              "role": "user",
              "content": "Compare these items: $itemsJson. Return strict JSON with keys: table, ratings, prosCons, recommendations."
            }
          ],
          "response_format": { "type": "json_object" }
        }
        """.trimIndent()
    }

    private fun copyUriToTemp(uri: Uri): File {
        val input = contentResolver.openInputStream(uri)!!
        val outFile = File(cacheDirProvider(), "insightra_${System.currentTimeMillis()}.bin")
        FileOutputStream(outFile).use { out ->
            input.copyTo(out)
        }
        input.close()
        return outFile
    }
}
