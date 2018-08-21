package com.example.haunguyen.mapexample.Map.Network;

import com.example.haunguyen.mapexample.Utlis.Utils;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DirectionServices {
    private Retrofit retrofit = null;



    public IGetDirection getDirection() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder.build();

        if (retrofit == null) {
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(Utils.BASE_DIRECTION_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return retrofit.create(IGetDirection.class);
    }
}
