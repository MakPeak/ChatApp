package io.bytechat.demo.oldrelease.vm;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;
import static io.openim.android.ouicore.net.RXRetrofit.N.mRetrofit;

import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import io.bytechat.demo.R;
import io.bytechat.demo.model.DiscoverResponse;
import io.bytechat.demo.model.LatestVersionData;
import io.bytechat.demo.model.LatestVersionResponse;
import io.bytechat.demo.oldrelease.repository.OpenIMService;
import io.bytechat.demo.oldrelease.repository.Root;
import io.bytechat.demo.vm.PersonalInfoViewModel;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.base.IView;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.net.RXRetrofit.N;
import io.openim.android.ouicore.net.RXRetrofit.NetObserver;
import io.openim.android.ouicore.net.RXRetrofit.Parameter;
import io.openim.android.ouicore.net.bage.Base;
import io.openim.android.ouicore.net.bage.GsonHel;
import io.openim.android.ouicore.utils.L;
import io.openim.android.ouicore.widget.WaitDialog;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.models.UserInfo;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends BaseViewModel<ProfileViewModel.ViewAction> {

    public MutableLiveData<String> nickname = new MutableLiveData<>("");
    public MutableLiveData<String> avatar = new MutableLiveData<>("");
    public MutableLiveData<Integer> gender = new MutableLiveData<>(0);
    public MutableLiveData<String> userID = new MutableLiveData<>("");
    public MutableLiveData<String> phoneNumber = new MutableLiveData<>("");
    public MutableLiveData<String> birthday = new MutableLiveData<>();
    public MutableLiveData<Uri> imageURI = new MutableLiveData<>();
    public MutableLiveData<LatestVersionData> latestVersionDataMutableLiveData = new MutableLiveData<>();
    ProfileViewModel.ViewActionPicture Iview2 ;

    public void getProfileData(){
//        WaitDialog showWait = showWait();
        OpenIMClient.getInstance().userInfoManager.getSelfUserInfo(new OnBase<UserInfo>() {
            @Override
            public void onError(int code, String error) {
//                showWait.dismiss();
                L.e("error :"+code + " " + error);
            }

            @Override
            public void onSuccess(UserInfo data) {
//                showWait.dismiss();
                // 返回当前登录用户的资料

                L.e("ProfileViewModel getFaceURL:"+data.getFaceURL());
                L.e("ProfileViewModel getNickname:"+data.getNickname());
                L.e("ProfileViewModel getUserID:"+data.getUserID());
                L.e("ProfileViewModel getGender:"+data.getGender());
                L.e("ProfileViewModel getPhoneNumber:"+data.getPhoneNumber());
                L.e("ProfileViewModel getBirth:"+data.getBirth());

                nickname.setValue(data.getNickname());
                avatar.setValue(data.getFaceURL());
                gender.setValue(data.getGender());
                userID.setValue(data.getUserID());
                phoneNumber.setValue(data.getPhoneNumber());
                birthday.setValue(data.getBirth());

            }
        });
    }

    public void getLatestVersion() {
        Parameter parameter = getParameter();
//        WaitDialog waitDialog = showWait();
        N.API(OpenIMService.class).getLatestVersion(LoginCertificate.getCache(getContext()).token, parameter.buildJsonBody())
            .compose(N.IOMain())
            .subscribe(new NetObserver<LatestVersionResponse>(getContext()) {

                @Override
                public void onComplete() {
                    super.onComplete();
//                    waitDialog.dismiss();
                }

                @Override
                public void onSuccess(LatestVersionResponse latestVersionResponse) {
//                    waitDialog.dismiss();
                    latestVersionDataMutableLiveData.setValue(latestVersionResponse.getData());
                    System.out.println("LATEST VERSION API: " + latestVersionResponse.toString());
                }

                @Override
                public void onError(Throwable e) {
//                    waitDialog.dismiss();
                    e.printStackTrace();
                }
            });
    }

    @NonNull
    private Parameter getParameter() {
        return new Parameter()
            .add("client", "android")
            .add("operationID", "11");
    }

    public void uploadFile(ProfileViewModel.ViewActionPicture Iview2){
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

    public void setNickname(View view){
        WaitDialog showWait = showWait();
        OpenIMClient.getInstance().userInfoManager.setSelfNickname(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                showWait.dismiss();
                L.e("PVMError :"+code + " " + error);
            }
            @Override
            public void onSuccess(String data) {
                showWait.dismiss();
                L.e("PVMSuccess :"+data.toString());
                Toast.makeText(getContext(), io.openim.android.ouicore.R.string.nickname_updated_successfully, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
            }
            }, nickname.getValue());
    }

    public void setGender(int gender){
        WaitDialog showWait = showWait();
        OpenIMClient.getInstance().userInfoManager.setSelfGender(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                showWait.dismiss();
                L.e("PVMError :"+code + " " + error);
            }
            @Override
            public void onSuccess(String data) {
                showWait.dismiss();
                L.e("PVMSuccess :"+data.toString());
                Toast.makeText(getContext(), io.openim.android.ouicore.R.string.gender_updated_successfully, Toast.LENGTH_SHORT).show();
            }
        }, gender);
    }

    public void setBirthday(String birthDay){
        WaitDialog showWait = showWait();
        OpenIMClient.getInstance().userInfoManager.setSelfBirthday(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                showWait.dismiss();
                L.e("PVMError :"+code + " " + error);
            }
            @Override
            public void onSuccess(String data) {
                showWait.dismiss();
                L.e("PVMSuccess :"+data.toString());
                Toast.makeText(getContext(), io.openim.android.ouicore.R.string.birthday_updated_successfully, Toast.LENGTH_SHORT).show();
            }
        }, birthDay);
    }

    public void setFaceURL(String faceURL){
        WaitDialog showWait = showWait();
        System.out.println("set face url 1");
        OpenIMClient.getInstance().userInfoManager.setSelfFaceURL(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                showWait.dismiss();
                L.e("PVMError :"+code + " " + error);
            }
            @Override
            public void onSuccess(String data) {
                showWait.dismiss();
                L.e("PVMSuccess :"+data.toString());
                avatar.postValue(faceURL);
                Toast.makeText(getContext(), io.openim.android.ouicore.R.string.photo_updated_successfully, Toast.LENGTH_SHORT).show();
            }
        }, faceURL);
    }

    @NonNull
    private WaitDialog showWait() {
        WaitDialog waitDialog = new WaitDialog(getContext());
        waitDialog.setNotDismiss();
        waitDialog.show();
        return waitDialog;
    }

    public interface ViewAction extends IView {
        void onErr(String msg);
        void onSucc(Object o);
    }

    public interface ViewActionPicture extends IView {
        void finishUploadingFile();
        void errorUploadingFile();
    }

}
