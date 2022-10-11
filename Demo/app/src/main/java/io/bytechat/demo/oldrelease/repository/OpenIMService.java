package io.bytechat.demo.oldrelease.repository;

import io.bytechat.demo.model.DiscoverResponse;
import io.bytechat.demo.model.LatestVersionData;
import io.bytechat.demo.model.LatestVersionResponse;
import io.bytechat.demo.model.RequestOnlineStatus;
import io.bytechat.demo.model.ResponseOnlineStatus;
import io.bytechat.demo.model.register.RequestImageVerificationCode;
import io.bytechat.demo.model.register.RequestInviteCode;
import io.bytechat.demo.model.register.RequestRegister;
import io.bytechat.demo.model.register.ResponseImageVerificationCode;
import io.bytechat.demo.model.register.ResponseInviteCode;
import io.bytechat.demo.model.register.ResponseInviteSwitch;
import io.bytechat.demo.model.register.ResponseRegister;
import io.bytechat.demo.model.register.ResponseRegisterType;
import io.openim.android.ouicore.net.RXRetrofit.Exception.RXRetrofitException;
import io.openim.android.ouicore.net.bage.Base;
import io.openim.android.ouicore.net.bage.GsonHel;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface OpenIMService {
    @POST("/demo/login")
    Observable<ResponseBody> login(@Body RequestBody requestBody);

    @POST("/demo/get_register_type")
    Observable<ResponseRegisterType> getRegisterType();

    @POST("/demo/get_invite_switch")
    Observable<ResponseInviteSwitch> getInviteSwitch();

    @POST("/demo/get_invite_code")
    Observable<ResponseInviteCode> getInviteCode(@Body RequestInviteCode requestInviteCode);

    @POST("/demo/get_verification_code")
    Observable<ResponseImageVerificationCode> getImageVerificationCode(@Body RequestImageVerificationCode requestImageVerificationCode);

    @POST("/demo/register")
    Observable<ResponseBody> newRegister(@Body RequestRegister requestRegister);

    @POST("/api/discover/get_discover_url")
    Observable<DiscoverResponse> discover(@Header("token") String auth, @Body RequestBody requestBody);

    @POST("/api/version/latest")
    Observable<LatestVersionResponse> getLatestVersion(@Header("token") String auth, @Body RequestBody requestBody);

    @POST("/demo/password")
    Observable<ResponseBody> register(@Body RequestBody requestBody);

    @POST("/demo/reset_password")
    Observable<ResponseBody> reSetPassword(@Body RequestBody requestBody);

    @POST("/demo/code")
    Observable<ResponseBody> getVerificationCode(@Body RequestBody requestBody);

    @POST("/user/update_user_info")
    Observable<ResponseBody> sendPersonalInfo(@Header("token")String token ,@Body RequestBody requestBody);

//    @Multipart
//    @POST("/third/minio_upload")
//    Observable<ResponseBody> uploadFile(@Header("token") String auth , @Body RequestBody name);

    @Multipart
    @POST("/third/minio_upload_persistence")
    Call<Root> uploadFile(
        @Header("token") String auth,
        @Part MultipartBody.Part file,
        @Part("fileType") int fileType,
        @Part("operationID") String operationID
    );

    @POST("/demo/verify")
    Observable<ResponseBody> checkVerificationCode(@Body RequestBody requestBody);

    @POST("/user/get_users_online_status")
    Observable<ResponseOnlineStatus> onlineStatus(@Header("token") String token, @Body RequestOnlineStatus requestOnlineStatus);

    static <T> Function<ResponseBody, T> turn(Class<T> tClass) {
        return responseBody -> {
            String body=responseBody.string();
            Base<T> base = GsonHel.dataObject(body, tClass);
            if (base.errCode == 0)
                return base.data;
            throw new RXRetrofitException(base.errMsg);
        };
    }
}
