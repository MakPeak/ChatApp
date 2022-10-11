package io.bytechat.demo.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.Timer;

import io.bytechat.demo.model.DiscoverResponse;
import io.bytechat.demo.model.register.ResponseRegisterType;
import io.bytechat.demo.oldrelease.repository.OpenIMService;
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

public class RegistrationViewModel extends BaseViewModel<RegistrationViewModel.ViewAction> {

    public MutableLiveData<String> phoneNumber = new MutableLiveData<>("");
    public MutableLiveData<String> areaCode = new MutableLiveData<>("");


    @NonNull
    private Parameter getParameter() {
        Parameter parameter = new Parameter()
            .add("phoneNumber", areaCode.getValue() + phoneNumber.getValue())
            .add("platform", 2)
            .add("email", "")
            .add("usedFor", 1)
            .add("language", "en")
            .add("operationID", System.currentTimeMillis() + "");
        return parameter;
    }

    public void getVerificationCode() {
        Parameter parameter = getParameter();
        WaitDialog waitDialog = showWait();
        N.API(OpenIMService.class).getVerificationCode(parameter.buildJsonBody())
            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseBody>(getContext()) {
                @Override
                public void onSuccess(ResponseBody o) {
                    String body = null;
                    try {
                        body = o.string();
                        Base<String> data = GsonHel.fromJson(body, Base.class);
                        if (data.errCode != 0 && data.errCode != 10008) {
                            IView.onErr(data.errMsg);
                            return;
                        }
                        IView.onSucce(null);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onComplete() {
                    super.onComplete();
                    waitDialog.dismiss();
                }

                @Override
                protected void onFailure(Throwable e) {
                    IView.onErr(e.getMessage());
                }
            });

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



    public interface ViewAction extends IView {

        void onErr(String msg);

        void onSucce(Object o);
    }

}
