package edu.utap.softmax

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET

import com.google.gson.annotations.SerializedName
import retrofit2.converter.gson.GsonConverterFactory

interface SoftmaxClient {

    @GET("/api/v1/models")
    suspend fun getModels(): List<Model>

    data class Model(
        @SerializedName("name")
        val name: String,
        @SerializedName("model_id")
        val modelId: String,
        @SerializedName("run_id")
        val runId: String
    )

    companion object {
        private var httpUrl = HttpUrl.Builder()
            .scheme("http")
            .host("jerryr.us")
            .port(23800)
            .build()

        fun create(): SoftmaxClient = create(httpUrl)

        private fun create(httpUrl: HttpUrl): SoftmaxClient {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()

            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SoftmaxClient::class.java)
        }
    }
}