package io.bytechat.demo.ui.auth;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.FragmentSetPasswordBinding;
import io.bytechat.demo.vm.LoginViewModel;
import io.bytechat.demo.vm.SetPasswordViewModel;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.entity.LoginCertificate;

public class SetPasswordFragment extends BaseFragment<SetPasswordViewModel> implements SetPasswordViewModel.ViewAction {

    private FragmentSetPasswordBinding binding;
    View view ;
    boolean passwordValidation = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view ;

        binding.enterPasswordEt.addTextChangedListener(new TextWatcher() {

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

        binding.enterPasswordEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        binding.nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordValidation) {
                    vm.phoneNumber.setValue(authVM.phoneNumber.getValue());
                    vm.verificationCode.setValue(authVM.verificationCode.getValue());

                    boolean isValid = passwordValidate();
                    if (!isValid) return;
                    if (authVM.usedFor.getValue() == 1) {
                        vm.register();
                    } else {
                        vm.reSetPassword();
                    }
                } else {
                    onErr(getString(io.openim.android.ouicore.R.string.password_format_error));
                }
            }
        });

        vm.pwd.observe(getViewLifecycleOwner() , data ->{
            if(vm.rePwd.getValue().isEmpty()) return;
            if(Objects.equals(data, vm.rePwd.getValue()) && !vm.rePwd.getValue().isEmpty()){
                binding.response.setVisibility(View.INVISIBLE);
            }else{
                if(!vm.rePwd.getValue().isEmpty()) {

                    binding.response.setVisibility(View.VISIBLE);
                    binding.response.setText(getString(io.openim.android.ouicore.R.string.password_does_not_match));
                }
            }
        });
        vm.rePwd.observe(getViewLifecycleOwner() , data ->{
            if(Objects.equals(data, vm.pwd.getValue()) && !vm.pwd.getValue().isEmpty()){
                binding.response.setVisibility(View.INVISIBLE);
            }else{
                if(!vm.pwd.getValue().isEmpty()) {
                    binding.response.setVisibility(View.VISIBLE);
                    binding.response.setText(getString(io.openim.android.ouicore.R.string.password_does_not_match));
                }
            }
        });
    }

    private void passwordEditTextFilter() {
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        InputFilter lengthFilter = new InputFilter.LengthFilter(20);
        binding.enterPasswordEt.setFilters(new InputFilter[] { filter,lengthFilter });
        binding.reenterPasswordEt.setFilters(new InputFilter[] { filter,lengthFilter });
    }

    private boolean passwordValidate() {
        System.out.println("vm.pwd.getValue() : "+ vm.pwd.getValue() + " vm.rePwd.getValue() " + vm.rePwd.getValue());
        boolean containLetter = false;
        if(Objects.requireNonNull(vm.pwd.getValue()).length() < 6){
            onErr(getString(io.openim.android.ouicore.R.string.password_length_error_message));
            return false ;
        }
        if(!Objects.equals(vm.pwd.getValue(), vm.rePwd.getValue())){
            System.out.println("vm.pwd.getValue()111 : "+ vm.pwd.getValue() + " vm.rePwd.getValue()111 " + vm.rePwd.getValue());
            binding.response.setText(getString(io.openim.android.ouicore.R.string.password_does_not_match));
            return false ;
        }

        for (int i = 0; i < vm.pwd.getValue().length(); ++i) {
            if (Character.isLetter(vm.pwd.getValue().charAt(i))) {
                containLetter= true;
                break;
            }
        }
        if(!containLetter){
            onErr(getString(io.openim.android.ouicore.R.string.password_minimum_length_error_message));
            return false;
        }
        if(vm.pwd.getValue().length() < 6){
            onErr(getString(io.openim.android.ouicore.R.string.password_length_error_message));
            return false ;
        }

//        Pattern special = Pattern.compile ("[!@#$%&*()_+.=|<>?{}\\[\\]~-]");
//        if(!special.matcher(vm.pwd.getValue()).find()){
//            onErr(getString(io.openim.android.ouicore.R.string.password_special_character_error_message));
//            return false ;
//        }

        return true ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(SetPasswordViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSetPasswordBinding.inflate(inflater, container, false);
        binding.setVm(vm);
        vm.setIView(this);
        passwordEditTextFilter();
        return binding.getRoot();
    }

    @Override
    public void onErr(String msg) {
        binding.response.setVisibility(View.VISIBLE);
        binding.response.setText(""+msg);
    }

    @Override
    public void onSucce(LoginCertificate o) {
        toast(getString(io.openim.android.ouicore.R.string.password_changed_successfully));

        if(authVM.usedFor.getValue() == 1 ){
            authVM.userId.setValue(o.userID);
            authVM.token.setValue(o.token);
            Navigation.findNavController(view).navigate(R.id.action_setPasswordFragment_to_personalInfoFragment);
        }else{
            Navigation.findNavController(view).navigate(R.id.action_setPasswordFragment_to_mainFragment);
        }

    }

    public static boolean containsAlphabet(String str) {
        return ((!str.equals("")) && (str != null) && (str.matches(".*[a-zA-Z]+.*")));
    }

    public static boolean containsNumber(String str) {
        String regex = "(.)*(\\d)(.)*";
        Pattern pattern = Pattern.compile(regex);
        boolean containsNumber = pattern.matcher(str).matches();
        return containsNumber;
    }
}
