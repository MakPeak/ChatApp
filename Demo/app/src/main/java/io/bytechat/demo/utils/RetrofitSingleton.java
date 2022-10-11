package io.bytechat.demo.utils;

import io.openim.android.ouicore.utils.Constant;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {
    static Retrofit getRetrofit() {
        return new Retrofit.Builder()
            .baseUrl(Constant.APP_AUTH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }
}
