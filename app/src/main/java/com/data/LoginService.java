package com.data;

import com.massky.sraum.User;

import org.json.JSONObject;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginService {

    @Headers({"Content-Type: application/json", "Accept: application/json"
            , "Cache-Control: public,max-age=300"
    })//需要添加头
    @POST("sraum_getToken")
    Flowable<Response<JSONObject>> getMessage(@Body RequestBody info);   // 请求体味RequestBody 类型


    @Headers({"Content-Type: application/json", "Accept: application/json"
            , "Cache-Control: public,max-age=300"
    })//需要添加头
    @POST("sraum_setPwd")
    Call<ResponseBody> sraum_setPwd(@Body RequestBody info);   // 请求体味RequestBody 类型
}
