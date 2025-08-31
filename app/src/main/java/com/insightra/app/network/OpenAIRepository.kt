package com.insightra.app.network

import android.content.ContentResolver
import android.net.Uri
import com.insightra.app.core.AppConstants
import com.insightra.app.data.db.ItemEntity
import com.insightra.app.network.model.ComparisonResultPayload
import com.insightra.app.network.model.ResponsesCreateResult
import com.insightra.app.network.model.VisionExtractResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class OpenAIRepository(
    private val service: OpenAIService,
    private val contentResolver: ContentResolver,
    private val cacheDirProvider: () -> File
) {
    private val json = Json { ignoreUnknownKeys = true }

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
            try {
                val rb = tmp.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", tmp.name, rb)
                val model = AppConstants.MODEL_VISION.toRequestBody("text/plain".toMediaTypeOrNull())
                val inputJson = """
                {
                  "input": [
                    {
                      "role": "user",
                      "content": [
                        {"type":"input_text","text":"Extract Brand, Name, and Size from the product label. If size is absent, default to '1 unit'. Return strict JSON: {\"brand\":\"\",\"name\":\"\",\"size\":\"\"} with no extra keys."},
                        {"type":"input_image","image_file":{"file_id":"file"}}
                      ]
                    }
                  ],
                  "response_format": { "type": "json_object" }
                }
                """.trimIndent().toRequestBody("application/json".toMediaTypeOrNull())

                val resp = service.uploadImageAndExtract(
                    auth = "Bearer $apiKey",
                    file = part,
                    model = model,
                    input = inputJson
                )
                val bodyString = resp.body()?.string().orEmpty()
                val result = runCatching { json.decodeFromString(ResponsesCreateResult.serializer(), bodyString) }.getOrNull()
                val jsonText = result?.output
                    ?.flatMap { it.content }
                    ?.firstNotNullOfOrNull { it.outputText?.content ?: it.text }
                    ?: "{}"
                val parsed = runCatching {
                    json.decodeFromString(VisionExtractResult.serializer(), jsonText)
                }.getOrNull() ?: VisionExtractResult("", "", "1 unit")
                parsed.copy(size = parsed.size.ifBlank { "1 unit" })
            } finally {
                tmp.delete()
            }
        }
    }

    suspend fun generateComparison(apiKey: String, items: List<ItemEntity>): ComparisonResultPayload {
        val prompt = buildComparisonRequestPayload(items)
        val body: RequestBody = prompt.toRequestBody("application/json".toMediaTypeOrNull())
        return withContext(Dispatchers.IO) {
            val resp = service.generateComparison("Bearer $apiKey", body)
            val bodyString = resp.body()?.string().orEmpty()
            val result = runCatching { json.decodeFromString(ResponsesCreateResult.serializer(), bodyString) }.getOrNull()
            val jsonText = result?.output
                ?.flatMap { it.content }
                ?.firstNotNullOfOrNull { it.outputText?.content ?: it.text }
                ?: """{"table":[],"ratings":[],"prosCons":[],"recommendations":[]}"""
            runCatching {
                json.decodeFromString(ComparisonResultPayload.serializer(), jsonText)
            }.getOrElse {
                ComparisonResultPayload(table = emptyList(), ratings = emptyList(), prosCons = emptyList(), recommendations = emptyList())
            }
        }
    }

    private fun buildComparisonRequestPayload(items: List<ItemEntity>): String {
        val itemsJson = items.joinToString(prefix = "[", postfix = "]") {
            """{"brand":"${it.brand}","name":"${it.name}","size":"${it.size}"}"""
        }
        return """
        {
          "model": "${AppConstants.MODEL_TEXT}",
          "input": [
            {
              "role": "system",
              "content": "You assist with product comparisons. Use public sources (Amazon, manufacturer sites, retailers) to synthesize average ratings (/5) and total review counts. Return only JSON conforming to the schema."
            },
            {
              "role": "user",
              "content": [
                {"type":"text","text":"Compare these items:"},
                {"type":"text","text": $itemsJson},
                {"type":"text","text":"Return only JSON with keys: table, ratings, prosCons, recommendations. The table is a list of rows {attribute, values[]} where values aligns with item order. ratings is [{itemIndex, averageOutOf5, totalReviews}]. prosCons is [{itemIndex, pros[3], cons[3]}]. recommendations is an array of strings up to 5 entries describing conditional choices."}
              ]
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
