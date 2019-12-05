package com.shubhamkhuva.searchtaskgoalwise.apiInterface

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import com.google.gson.GsonBuilder
import com.shubhamkhuva.searchtaskgoalwise.helper.CommonKey
import com.shubhamkhuva.searchtaskgoalwise.response.SearchResultRes
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.*

interface ApiInterface {
    @Headers("x-api-key: "+CommonKey.XAPIKEY)
    @POST("dev/search")
    fun getResult(@Body params: RequestBody):Call<SearchResultRes>

    companion object Factory {

        fun create(): ApiInterface {
            val gson = GsonBuilder()
                    .setLenient()
                    .create()

            val logging =  HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS).build()
            val retrofit = Retrofit.Builder()
                    .baseUrl(CommonKey.BaseURL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}