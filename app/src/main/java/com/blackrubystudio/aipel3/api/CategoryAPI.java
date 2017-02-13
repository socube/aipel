package com.blackrubystudio.aipel3.api;

import com.blackrubystudio.aipel3.api.gson.Category;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class CategoryAPI {

    private interface CategoryAPIService {
        @GET("/api/category/{place}")
        Call<Category> category(@Path("place") String place);
    }

    public Call<Category> getCategory(String place) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://aipel.blackrubystudio.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CategoryAPIService service = retrofit.create(CategoryAPIService.class);

        return service.category(place);
    }

    public ArrayList<Call<Category>> getCategories(ArrayList<String> places) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://aipel.blackrubystudio.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CategoryAPIService service = retrofit.create(CategoryAPIService.class);

        ArrayList<Call<Category>> callArrayList = new ArrayList<>();

        for (String place : places) {
            callArrayList.add(service.category(place));
        }

        return callArrayList;
    }
}
