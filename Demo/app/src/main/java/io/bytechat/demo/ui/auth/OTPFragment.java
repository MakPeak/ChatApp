package io.bytechat.demo.ui.auth;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

import java.util.Objects;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.FragmentOTPBinding;
import io.bytechat.demo.databinding.FragmentRegistrationBinding;
import io.bytechat.demo.vm.OTPViewModel;
import io.bytechat.demo.vm.RegistrationViewModel;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.net.bage.Base;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class OTPFragment extends BaseFragment<OTPViewModel> implements OTPViewModel.ViewAction {

    FragmentOTPBinding binding ;
    View view ;
    int time = 60 ;
    CountDownTimer countDownTimer ;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view  ;
//        binding.backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Navigation.findNavController(view).popBackStack();
//            }
//        });
        binding.pinView.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                vm.verificationCode.setValue(binding.pinView.getValue());
                vm.phoneNumber.setValue(authVM.phoneNumber.getValue());
                vm.usedforvalue.setValue(authVM.usedFor.getValue());
                vm.checkVerificationCode(authVM.usedFor.getValue());
            }
        });

        binding.OTPView.setOnFinishListener(new Function1<String, Unit>() {
            @Override
            public Unit invoke(String s) {
                vm.verificationCode.setValue(s);
                vm.phoneNumber.setValue(authVM.phoneNumber.getValue());
                vm.usedforvalue.setValue(authVM.usedFor.getValue());
                vm.checkVerificationCode(authVM.usedFor.getValue());
                return null;
            }
        });

        binding.enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.pinView.getValue().length()<6){
                    binding.response.setText(io.openim.android.ouicore.R.string.verification_code_empty_error_message);
                    return;
                }
                vm.verificationCode.setValue(binding.pinView.getValue());
                vm.phoneNumber.setValue(authVM.phoneNumber.getValue());
                vm.usedforvalue.setValue(authVM.usedFor.getValue());
                vm.checkVerificationCode(authVM.usedFor.getValue());
            }
        });

        // that's mean you are came from set password page

        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                try {
                    time--;
                    binding.timerText.setText(getString(io.openim.android.ouicore.R.string.counter_message));
                    binding.timerNumber.setText(""+time);
                }catch (Exception e){
                    e.printStackTrace();
                }

//                try {
//                    wait(500);
//                    binding.timerText.animate().alpha(1.0f).setDuration(400);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onFinish() {
                binding.timerNumber.setText("");
                binding.timerText.setText(io.openim.android.ouicore.R.string.resend_verification_code);
                try {
                    binding.timerText.setTextColor(requireContext().getColor(R.color.purple_200));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        };
        if(time==0){
            binding.timerNumber.setText("");
            binding.timerText.setText(io.openim.android.ouicore.R.string.resend_verification_code);
            binding.timerText.setTextColor(requireContext().getColor(R.color.purple_200));
        }else{
            binding.timerText.setText(getString(io.openim.android.ouicore.R.string.counter_message));
            countDownTimer.start();
        }
        binding.timerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time == 0){
                    vm.phoneNumber.setValue(authVM.phoneNumber.getValue());
                    vm.usedforvalue.setValue(authVM.usedFor.getValue());
                    vm.getVerificationCode(authVM.usedFor.getValue());
                }
            }
        });

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(OTPViewModel.class);
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOTPBinding.inflate(inflater, container, false);
        binding.setVm(vm);
        vm.setIView(this);
        return binding.getRoot();
    }

    @Override
    public void onErr(String msg) {
        if(!binding.pinView.getValue().isEmpty())
            binding.pinView.clearValue();
        binding.response.setVisibility(View.VISIBLE);
       // binding.response.setText(""+msg);
    }

    @Override
    public void onSucce(Object o) {
        authVM.verificationCode.setValue(vm.verificationCode.getValue());
        time = 0 ;
        countDownTimer.cancel();
        binding.response.setVisibility(View.INVISIBLE);
        Navigation.findNavController(view).navigate(R.id.action_OTPFragment_to_setPasswordFragment);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResendCode(Object o) {
        binding.pinView.clearValue();
        binding.timerText.setTextColor(getContext().getColor(io.openim.android.ouicore.R.color.counter_color));
        time = 60;
        countDownTimer.start();
//        Toast.makeText(getContext(), "Code resend Successfully", Toast.LENGTH_SHORT).show();

    }
}
