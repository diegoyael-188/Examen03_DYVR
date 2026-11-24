package com.example.examen03_dyvr;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {
    private static final String BASE_URL = "https://dummyjson.com/";

    public static final ProductService SERVICE = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductService.class);

    private RetrofitClient() {}
}
