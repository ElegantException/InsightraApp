package com.insightra.app.network

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OpenAIService {
    @GET("v1/models")
    suspend fun listModels(
        @Header("Authorization") auth: String
    ): Response<Unit>

    @Multipart
    @POST("v1/responses")
    suspend fun uploadImageAndExtract(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("input") input: RequestBody
    ): Response<ResponseBody>

    @POST("v1/responses")
    suspend fun generateComparison(
        @Header("Authorization") auth: String,
        @Body body: RequestBody
    ): Response<ResponseBody>

    companion object {
        fun create(): OpenAIService {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
            return retrofit.create(OpenAIService::class.java)
        }
    }
}
