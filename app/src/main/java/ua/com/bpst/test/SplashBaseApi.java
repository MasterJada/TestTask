package ua.com.bpst.test;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by olegreksa on 28.09.17.
 */

public interface SplashBaseApi {
    @GET("api/v1/images/search")
    Observable<ResponseModel> searchImages(@Query("query") String query);
}
