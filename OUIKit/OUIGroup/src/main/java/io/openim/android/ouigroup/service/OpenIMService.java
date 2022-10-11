package io.openim.android.ouigroup.service;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface OpenIMService {
    @Multipart
    @POST("/third/minio_upload_persistence")
    Call<Root> uploadFile(
        @Header("token") String auth,
        @Part MultipartBody.Part file,
        @Part("fileType") int fileType,
        @Part("operationID") String operationID
    );

    @POST("/user/get_users_online_status")
    Observable<ResponseOnlineStatus> onlineStatus(@Header("token") String token, @Body RequestOnlineStatus requestOnlineStatus);
}
