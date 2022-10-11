package io.bytechat.demo.vm;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;

import cn.jpush.android.api.JPushInterface;
import io.bytechat.demo.model.register.RequestImageVerificationCode;
import io.bytechat.demo.model.register.RequestInviteCode;
import io.bytechat.demo.model.register.RequestRegister;
import io.bytechat.demo.model.register.ResponseImageVerificationCode;
import io.bytechat.demo.model.register.ResponseInviteCode;
import io.bytechat.demo.model.register.ResponseInviteSwitch;
import io.bytechat.demo.model.register.ResponseRegisterType;
import io.bytechat.demo.oldrelease.repository.OpenIMService;
import io.bytechat.demo.ui.auth.AuthActivity;
import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.base.IView;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.net.RXRetrofit.N;
import io.openim.android.ouicore.net.RXRetrofit.NetObserver;
import io.openim.android.ouicore.net.RXRetrofit.Parameter;
import io.openim.android.ouicore.net.bage.Base;
import io.openim.android.ouicore.net.bage.GsonHel;
import io.openim.android.ouicore.widget.WaitDialog;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import okhttp3.ResponseBody;

public class LoginViewModel extends BaseViewModel<LoginViewModel.ViewAction> {
    public static final int MAX_COUNTDOWN = 10;

    public MutableLiveData<String> pwd = new MutableLiveData<>("");
    public MutableLiveData<String> phoneNumber = new MutableLiveData<>("");
    public MutableLiveData<String> areaCode = new MutableLiveData<>("");
    public MutableLiveData<Integer> registerType = new MutableLiveData<>(1);
    public MutableLiveData<Integer> inviteSwitch = new MutableLiveData<>();
    public MutableLiveData<String> imageArray = new MutableLiveData<>("");
    public MutableLiveData<String> verificationCode = new MutableLiveData<>("");
    public MutableLiveData<String> invitationCode = new MutableLiveData<>("");

    public void login() {
        Parameter parameter = getParameter(null);
        WaitDialog waitDialog = showWait();
        N.API(OpenIMService.class).login(parameter.buildJsonBody())
            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseBody>(getContext()) {

                @Override
                public void onComplete() {
                    super.onComplete();
                    waitDialog.dismiss();
                }

                @Override
                public void onSuccess(ResponseBody o) {
                    try {
                        String body = o.string();
                        Base<LoginCertificate> loginCertificate = GsonHel.dataObject(body, LoginCertificate.class);
                        if (loginCertificate.errCode != 0) {
                            if(loginCertificate.errCode == 10004){
                                IView.onErr(getContext().getString(io.openim.android.ouicore.R.string.invalid_password),0);
                                return;
                            }
                            if(loginCertificate.errCode == 10003){
                                IView.onErr(getContext().getString(io.openim.android.ouicore.R.string.no_user_found_error_message), 1);
                                return;
                            }
                            IView.onErr(loginCertificate.errMsg, 2);
                            return;
                        }
                        loginCertificate.data.cache(getContext());
                        authVM.userId.setValue(loginCertificate.data.userID);
                        JPushInterface.setAlias(getContext(), 123456, loginCertificate.data.userID);
                        authVM.token.setValue(loginCertificate.data.token);
                        OpenIMClient.getInstance().login(new OnBase<String>() {
                            @Override
                            public void onError(int code, String error) {
                                IView.onErr(error, 3);
                            }

                            @Override
                            public void onSuccess(String data) {
                                //缓存登录信息
                                loginCertificate.data.cache(getContext());
//                                IView.onSucce(data);
                                IView.onSucce(loginCertificate.data, "OpenIMClient.getInstance().login");

                            }
                        }, loginCertificate.data.userID, loginCertificate.data.token);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(Throwable e) {
                    IView.onErr(e.getMessage(), 4);
                }
            });
    }

    public void getRegisterType() {
//        WaitDialog waitDialog = showWait();
        N.API(OpenIMService.class).getRegisterType()
            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseRegisterType>(getContext()) {

                @Override
                public void onComplete() {
                    super.onComplete();
//                    waitDialog.dismiss();
                }

                @Override
                public void onSuccess(ResponseRegisterType responseRegisterType) {
//                    waitDialog.dismiss();
                    System.out.println("REGISTER TYPE API SUCCESS: " + responseRegisterType.toString());
                    registerType.setValue(responseRegisterType.getData().getType().get(0));
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
//                    waitDialog.dismiss();
                    System.out.println("REGISTER TYPE API ERROR: " + e.toString());
                }
            });
    }

    public void getInviteSwitch() {
//        WaitDialog waitDialog = showWait();
        N.API(OpenIMService.class).getInviteSwitch()
            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseInviteSwitch>(getContext()) {

                @Override
                public void onComplete() {
                    super.onComplete();
//                    waitDialog.dismiss();
                }

                @Override
                public void onSuccess(ResponseInviteSwitch responseInviteSwitch) {
//                    waitDialog.dismiss();
                    System.out.println("INVITE SWITCH API SUCCESS: " + responseInviteSwitch.toString());
                    inviteSwitch.postValue(responseInviteSwitch.getData().getSwitch());
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
//                    waitDialog.dismiss();
                    System.out.println("INVITE SWITCH API ERROR: " + e.toString());
                }
            });
    }

    public void getInviteCode(Activity activity) {
//        WaitDialog waitDialog = showWait();
        String deviceMan = android.os.Build.MANUFACTURER;
        PackageManager pm = getContext().getApplicationContext().getPackageManager();
        String pkgName = getContext().getApplicationContext().getPackageName();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);

        String languageTage = Locale.getDefault().getLanguage();
        String release = Build.VERSION.RELEASE;
        int cnt =0 ;
        for(int i = 0 ; i < release.length() ; i ++ ){
            if(release.charAt(i) == '.')
                cnt++;
        }
        for(int i = cnt ; i < 2 ; i++ ){
            release += ".0";
        }
        String timezone = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
        int sdkVersion = Build.VERSION.SDK_INT;

        timezone = "UTC"+timezone.substring(3);
        if(timezone.charAt(4) == '0')
            timezone = timezone.substring(0,4) +timezone.substring(5) ;


        RequestInviteCode requestInviteCode = new RequestInviteCode("132131", timezone, deviceMan,
            "android", release,  width, ""+languageTage);
        N.API(OpenIMService.class).getInviteCode(requestInviteCode)
            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseInviteCode>(getContext()) {

                @Override
                public void onComplete() {
                    super.onComplete();
//                    waitDialog.dismiss();
                }

                @Override
                public void onSuccess(ResponseInviteCode responseInviteCode) {
//                    waitDialog.dismiss();
                    System.out.println("INVITE CODE API SUCCESS: " + responseInviteCode.toString());
                    invitationCode.setValue(responseInviteCode.getData().getChannelCode());
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
//                    waitDialog.dismiss();
                    System.out.println("INVITE CODE API ERROR: " + e.toString());
                }
            });
    }

    public void getImageVerificationCode() {
        WaitDialog waitDialog = showWait();
        RequestImageVerificationCode requestImageVerificationCode = new RequestImageVerificationCode(2, "1657698965378");
        N.API(OpenIMService.class).getImageVerificationCode(requestImageVerificationCode)
            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseImageVerificationCode>(getContext()) {

                @Override
                public void onComplete() {
                    super.onComplete();
                    waitDialog.dismiss();
                }

                @Override
                public void onSuccess(ResponseImageVerificationCode responseImageVerificationCode) {
                    waitDialog.dismiss();
                    System.out.println("IMAGE VERIFICATION CODE API SUCCESS: " + responseImageVerificationCode.toString());
                    imageArray.setValue(responseImageVerificationCode.getData().getImage().substring(responseImageVerificationCode.getData().getImage().
                        lastIndexOf(",") + 1).trim());
                    verificationCode.setValue(responseImageVerificationCode.getData().getInviteCode());

                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    waitDialog.dismiss();
                    System.out.println("IMAGE VERIFICATION CODE API ERROR: " + e.toString());
                }
            });
    }

    public void register(String phoneNumber, String password, String inviteCode, String verificationID, String verificationCode) {
        WaitDialog waitDialog = showWait();
        RequestRegister requestRegister = new RequestRegister(2, "1657698965378", phoneNumber, password, inviteCode, verificationID, verificationCode);
        N.API(OpenIMService.class).newRegister(requestRegister)
            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseBody>(getContext()) {

                @Override
                public void onComplete() {
                    super.onComplete();
                    waitDialog.dismiss();
                }

                @Override
                public void onSuccess(ResponseBody responseBody) {
                    waitDialog.dismiss();
                    System.out.println("REGISTER API SUCCESS: " + responseBody.toString());

                    String body = null;
                    try {
                        body = responseBody.string();
                        Base<LoginCertificate> data = GsonHel.dataObject(body, LoginCertificate.class);
                        if(data.errCode != 0){
                            IView.onErr(data.errMsg, 5);
                            return;
                        }
                        data.data.cache(getContext());
                        IView.onSucce(data.data, "new RequestRegister");

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    waitDialog.dismiss();
                    System.out.println("REGISTER API ERROR: " + e.toString());
                }
            });
    }


    @NonNull
    private Parameter getParameter(String verificationCode) {
        Parameter parameter = new Parameter()
            .add("phoneNumber", areaCode.getValue() + phoneNumber.getValue())
            .add("password", pwd.getValue())
            .add("platform", 2)
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

    /**
     * 检查验证码并注册
     */
//    public void checkVerificationCode(String verificationCode) {
//        Parameter parameter = getParameter(verificationCode);
//        WaitDialog waitDialog = showWait();
//        N.API(OpenIMService.class).checkVerificationCode(parameter.buildJsonBody())
//            .map(OpenIMService.turn(HashMap.class))
//            .compose(N.IOMain())
//            .subscribe(new NetObserver<HashMap>(getContext()) {
//                @Override
//                public void onComplete() {
//                    super.onComplete();
//                    waitDialog.dismiss();
//                }
//
//                @Override
//                public void onSuccess(HashMap o) {
//                    LoginViewModel.this.verificationCode=verificationCode;
//                    IView.succ("checkVerificationCode");
//                }
//
//                @Override
//                protected void onFailure(Throwable e) {
//                    IView.err(e.getMessage());
//                }
//            });
//    }
//    public void register(){
//        Parameter parameter = getParameter(verificationCode);
//        WaitDialog waitDialog = showWait();
//        N.API(OpenIMService.class).register(parameter.buildJsonBody())
//            .map(OpenIMService.turn(LoginCertificate.class))
//            .compose(N.IOMain())
//            .subscribe(new NetObserver<LoginCertificate>(context.get()) {
//                @Override
//                public void onComplete() {
//                    super.onComplete();
//                    waitDialog.dismiss();
//                }
//
//                @Override
//                public void onSuccess(LoginCertificate o) {
//                    setSelfInfo();
//                    o.cache(getContext());
//                    IView.jump();
//                }
//
//                @Override
//                protected void onFailure(Throwable e) {
//                   IView.err(e.getMessage());
//                }
//            });
//    }



    public interface ViewAction extends IView {

        void onErr(String msg, int errCode);

        void onSucce(Object o, String msg);
    }

}
