package io.bytechat.demo.vm;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;
import static io.openim.android.ouicore.net.RXRetrofit.N.mRetrofit;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

import io.bytechat.demo.oldrelease.repository.OpenIMService;
import io.bytechat.demo.oldrelease.repository.Root;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.base.IView;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.net.RXRetrofit.N;
import io.openim.android.ouicore.net.RXRetrofit.NetObserver;
import io.openim.android.ouicore.net.RXRetrofit.Parameter;
import io.openim.android.ouicore.net.bage.Base;
import io.openim.android.ouicore.net.bage.GsonHel;
import io.openim.android.ouicore.widget.WaitDialog;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoViewModel extends BaseViewModel<PersonalInfoViewModel.ViewAction> {

    public MutableLiveData<String> nickname = new MutableLiveData<>("");
    public MutableLiveData<Uri> imageURI = new MutableLiveData<>();
    public MutableLiveData<String> userID = new MutableLiveData<>("");
    public MutableLiveData<String> faceURL = new MutableLiveData<>("");

    public void uploadFile(){
        WaitDialog waitDialog = showWait();
        RequestBody requestFile =
            RequestBody.create(
                MediaType.parse("image/jpg"),
                new File(imageURI.getValue().getPath())
            );
        System.out.println(imageURI.getValue().getPath());
        MultipartBody.Part body =
            MultipartBody.Part.createFormData("file", imageURI.getValue().getPath(), requestFile);

        OpenIMService service = mRetrofit.create(OpenIMService.class);
        Call<Root> call = service.uploadFile(authVM.token.getValue() , body,3,""+System.currentTimeMillis());
        call.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                System.out.println("1111111");
                waitDialog.dismiss();
                if (response.code() == 200){
                    System.out.println("3333333");
                    authVM.faceURL.setValue(response.body().data.uRL) ;
                    IView.finishUploadingFile();
                }else{
                    IView.errorUploadingFile();
                }

            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                System.out.println("2222222" + t.getMessage());
                waitDialog.dismiss();
                IView.errorUploadingFile();

            }
        });
    }

    public void uploadFile(PersonalInfoViewModel.ViewActionPicture Iview2){
        WaitDialog waitDialog = showWait();
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), new File(imageURI.getValue().getPath()));
        System.out.println(imageURI.getValue().getPath());
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageURI.getValue().getPath(), requestFile);

        OpenIMService service = mRetrofit.create(OpenIMService.class);
        Call<Root> call = service.uploadFile(authVM.token.getValue() , body,3,""+System.currentTimeMillis());
        call.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                System.out.println("1111111");
                waitDialog.dismiss();
                if (response.code() == 200){
                    System.out.println("3333333");
                    authVM.faceURL.setValue(response.body().data.uRL) ;
                    Iview2.finishUploadingFile();
                } else {
                    Iview2.errorUploadingFile();
                }

            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                System.out.println("2222222" + t.getMessage());
                waitDialog.dismiss();
                Iview2.errorUploadingFile();

            }
        });
    }

    public void sendPersonalInfo(){
        Parameter parameter = getParameterSendInfo();
        WaitDialog waitDialog = showWait();
        N.API(OpenIMService.class).sendPersonalInfo(authVM.token.getValue(),parameter.buildJsonBody())
            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseBody>(context.get()) {
                @Override
                public void onComplete() {
                    super.onComplete();
                    waitDialog.dismiss();
                }

                @Override
                    public void onSuccess(ResponseBody o) {
                    String body = null;
                    try {
                        body = o.string();
                        Base<LoginCertificate> data = GsonHel.dataObject(body, LoginCertificate.class);
                        if(data.errCode != 0){
                            IView.onErr(data.errMsg);
                            return;
                        }
                        IView.onSucce(o);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void onFailure(Throwable e) {
                    IView.onErr(e.getMessage());
                }
            });
    }

    @NonNull
    private Parameter getParameterSendInfo() {
        Parameter parameter = new Parameter()
            .add("userID",userID.getValue())
            .add("nickname", nickname.getValue())
            .add("faceURL", faceURL.getValue())
//            .add("gender", 0)
//            .add("phoneNumber", "")
//            .add("birth", 0)
//            .add("email", "")
//            .add("ex", "")
            .add("operationID", System.currentTimeMillis() + "");
        return parameter;
    }

    @NonNull
    private WaitDialog showWait() {
        WaitDialog waitDialog = new WaitDialog(getContext());
        waitDialog.setNotDismiss();
        waitDialog.show();
        return waitDialog;
    }

    private Timer timer;

    @Override
    protected void viewDestroy() {
        super.viewDestroy();
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
    }

    public interface ViewAction extends io.openim.android.ouicore.base.IView {

        void onErr(String msg);

        void onSucce(Object o);

        void finishUploadingFile();

        void errorUploadingFile();
    }

    public interface ViewActionPicture extends io.openim.android.ouicore.base.IView {
        void finishUploadingFile();
        void errorUploadingFile();
    }

}
