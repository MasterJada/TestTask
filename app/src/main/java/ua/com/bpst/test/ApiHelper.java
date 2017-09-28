package ua.com.bpst.test;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by olegreksa on 28.09.17.
 */

public class ApiHelper {


    static  SplashBaseApi getInstance(){
        Retrofit retrofit = new  Retrofit.Builder()
                .baseUrl("http://www.splashbase.co/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    return  retrofit.create(SplashBaseApi.class);
    }
}
