package io.bytechat.demo.ui.auth;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;
import static io.bytechat.demo.utils.UtilsFunctions.isValidPhoneNumber;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.regex.Pattern;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.FragmentRegistrationCaptchaBinding;
import io.bytechat.demo.vm.LoginViewModel;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.entity.LoginCertificate;

public class RegistrationCaptchaFragment extends BaseFragment<LoginViewModel> implements LoginViewModel.ViewAction {

    FragmentRegistrationCaptchaBinding binding ;
    View view ;
    boolean passwordValidation = false;
    String stringInvitationCode = "";
    String stringVerificationCode = "";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view ;

        if(!authVM.phoneNumber.getValue().isEmpty()){
            vm.phoneNumber.setValue(authVM.phoneNumber.getValue());
            binding.countryCodePicker.setCountryForPhoneCode(Integer.parseInt(authVM.areaCode.getValue()));
        }

        vm.imageArray.observe(getViewLifecycleOwner(), data->{
            if(!data.equalsIgnoreCase("")) {
                Glide.with(getContext())
                    .load(Base64.decode(data, 0))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//to prevent caching
                    .skipMemoryCache(true)//to prevent caching
                    .into(binding.ivCaptcha);
            }
        });

        vm.verificationCode.observe(getViewLifecycleOwner(), data->{
            if(!data.equalsIgnoreCase("")) {
                stringVerificationCode = data;
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();

            }
        });

        binding.ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vm.getImageVerificationCode();
            }
        });

        binding.shetPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {
                    binding.cvPassValidation.setVisibility(View.VISIBLE);
                    Log.e("TAG", "e1 focused");
                } else {
                    binding.cvPassValidation.setVisibility(View.GONE);
                    Log.e("TAG", "e1 not focused");
                }
            }
        });

        binding.shetPassword.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(containsNumber(s.toString())){
                    binding.ivPassValidation1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_correct));
                } else {
                    binding.ivPassValidation1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_wrong));
                }
                if(containsAlphabet(s.toString())){
                    binding.ivPassValidation2.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_correct));
                } else {
                    binding.ivPassValidation2.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_wrong));
                }
                if(s.toString().length() > 5 && s.toString().length() < 21){
                    binding.ivPassValidation3.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_correct));
                } else {
                    binding.ivPassValidation3.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_wrong));
                }

                passwordValidation = containsNumber(s.toString()) && containsAlphabet(s.toString()) && s.toString().length() > 5 && s.toString().length() < 21;
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        binding.etCaptcha.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.etPhoneNumber.length() < 1){
                    onErr(getString(io.openim.android.ouicore.R.string.please_enter_phone_number), 1);
                    return;
                }
//                if(binding.etPhoneNumber.length() < 3){
//                    onErr(getString(io.openim.android.ouicore.R.string.phone_number_invalid_error_message), 2);
//                    return;
//                }
//                boolean isValid = isValidPhoneNumber( "+"+binding.countryCodePicker.getSelectedCountryCode()+binding.etPhoneNumber.getText().toString(), binding.countryCodePicker.getSelectedCountryCode());
//                if(!isValid){
//                    onErr(getString(io.openim.android.ouicore.R.string.phone_number_invalid_error_message), 3);
//                    return;
//                }

                if(binding.shetPassword.length()<1){
                    onErr(getString(io.openim.android.ouicore.R.string.please_enter_your_password), 4);
                    return;
                }
                if(binding.shetPassword.length()<6){
                    onErr(getString(io.openim.android.ouicore.R.string.password_length_error_message), 5);
                    return;
                }
                if(binding.shetPassword.length()>20 || !containsNumber(binding.shetPassword.getText().toString())
                || !containsAlphabet(binding.shetPassword.getText().toString())){
                    onErr(getString(io.openim.android.ouicore.R.string.password_format_error), 6);
                    return;
                }
                if(binding.shetConfirmPassword.length()<1){
                    onErr(getString(io.openim.android.ouicore.R.string.please_enter_your_confirm_password), 7);
                    return;
                }

                if(!binding.shetPassword.getText().toString().equalsIgnoreCase(binding.shetConfirmPassword.getText().toString())) {
                    onErr(getString(io.openim.android.ouicore.R.string.password_does_not_match_err), 8);
                    return;
                }

                if(binding.etCaptcha.getText()==null || binding.etCaptcha.getText().toString().isEmpty()){
                    onErr(getString(io.openim.android.ouicore.R.string.please_enter_captcha), 9);
                    return;
                }

                if(binding.cbAgreementPp.isChecked()) {
                    stringInvitationCode = binding.etInvitationCode.getText().toString();

                    vm.register("+" + binding.countryCodePicker.getSelectedCountryCode() + binding.etPhoneNumber.getText().toString(),
                        binding.shetPassword.getText().toString(), stringInvitationCode, stringVerificationCode, binding.etCaptcha.getText().toString());
                }else {
                    onErr(getString(io.openim.android.ouicore.R.string.accept_agreement), 10);
                    return;
                }

            }
        });

        vm.inviteSwitch.observe(getViewLifecycleOwner(), data->{
            if(data == 1){
                vm.getInviteCode(RegistrationCaptchaFragment.this.getActivity());
                binding.tvInvitationCode.setVisibility(View.VISIBLE);
                binding.rlInvitationCode.setVisibility(View.VISIBLE);
                vm.invitationCode.observe(getViewLifecycleOwner(), invitationCode ->{
                    if(invitationCode != null){
                        ClipboardManager clipboard = (ClipboardManager) this.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        String inviteCodeClip="" ;
                        boolean inviteCodeCorrect = false ;
                        try{
                             inviteCodeClip = clipboard.getText().toString() ;
                            if(inviteCodeClip.contains("invite-code-")){
                                inviteCodeCorrect = true ;
                                inviteCodeClip = inviteCodeClip.substring(12);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if (!invitationCode.equalsIgnoreCase("") && !inviteCodeCorrect) {
                            stringInvitationCode = invitationCode;
                            binding.etInvitationCode.setText(vm.invitationCode.getValue());
//                            binding.etInvitationCode.setClickable(false);
//                            binding.etInvitationCode.setEnabled(false);
//                            binding.etInvitationCode.setKeyListener(null);
                        } else {
                            if(inviteCodeCorrect){
                                stringInvitationCode = inviteCodeClip ;
                                binding.etInvitationCode.setText(inviteCodeClip);
                            }else{
                                stringInvitationCode = binding.etInvitationCode.getText().toString();
                            }
                        }
                    }
                });
            } else {
                binding.tvInvitationCode.setVisibility(View.GONE);
                binding.rlInvitationCode.setVisibility(View.GONE);
            }
        });

    }
    private void phoneNumberEditTextFilter() {
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        InputFilter lengthFilter = new InputFilter.LengthFilter(11);
        binding.etPhoneNumber.setFilters(new InputFilter[] { filter ,lengthFilter});
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistrationCaptchaBinding.inflate(inflater, container, false);
        binding.setLoginViewModel(vm);
        vm.setIView(this);
        phoneNumberEditTextFilter();
        return binding.getRoot();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(LoginViewModel.class);
        super.onCreate(savedInstanceState);
        vm.getImageVerificationCode();
        vm.getInviteSwitch();
    }
    @Override
    public void onErr(String msg, int errCode) {
        binding.response.setVisibility(View.VISIBLE);
        binding.response.setText(""+msg);
    }

    @Override
    public void onSucce(Object o, String msg) {
        authVM.phoneNumber.setValue(vm.areaCode.getValue() + vm.phoneNumber.getValue());
        authVM.usedFor.setValue(1);
        authVM.userId.setValue(((LoginCertificate) o).userID);
        authVM.token.setValue(((LoginCertificate) o).token);

        if(msg.equalsIgnoreCase("new")) {
            Navigation.findNavController(view).navigate(R.id.action_registrationCaptchaFragment_to_personalInfoFragment);
        } else {
           // Navigation.findNavController(view).navigate(R.id.action_registrationFragment_to_OTPFragment);
            Navigation.findNavController(view).navigate(R.id.action_registrationCaptchaFragment_to_personalInfoFragment);
        }
    }

    public static boolean containsAlphabet(String str) {
        return ((!str.equals("")) && (str != null) && (str.matches(".*[a-zA-Z]+.*")));
    }

    public static boolean containsNumber(String str) {
        String regex = "(.)*(\\d)(.)*";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(str).matches();
    }
}
