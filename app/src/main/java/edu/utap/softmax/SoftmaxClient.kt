package edu.utap.softmax

import com.google.gson.annotations.SerializedName

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Retrofit

import okhttp3.HttpUrl
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient

interface SoftmaxClient {

    @GET("/api/v1/models")
    suspend fun getModels(): List<Model>

    @GET("/api/v1/get/{model_id}")
    suspend fun get(@Path("model_id")modelId: String): Model

    data class Model(
        @SerializedName("name")
        val name: String,
        @SerializedName("model_id")
        val modelId: String,
        @SerializedName("run_id")
        val runId: String,
        @SerializedName("runs")
        val runs: List<Run>
    )

    data class Run(
        @SerializedName("run_id")
        val runId: String,
        @SerializedName("timestamp")
        val timestamp: String,
        @SerializedName("log")
        val log: List<LogItem>
    )

    data class LogItem(
        @SerializedName("step")
        val step: Int,
        @SerializedName("data")
        val data: LogData
    )

    data class LogData(
        @SerializedName("loss")
        val loss: Float,
        @SerializedName("accuracy")
        val accuracy: Float
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