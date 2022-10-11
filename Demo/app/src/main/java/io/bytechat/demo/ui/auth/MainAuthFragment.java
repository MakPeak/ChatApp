package io.bytechat.demo.ui.auth;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;
import static io.bytechat.demo.utils.UtilsFunctions.isValidPhoneNumber;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.FragmentMainBinding;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.bytechat.demo.vm.LoginViewModel;
import io.openim.android.ouicore.base.BaseFragment;

public class MainAuthFragment extends BaseFragment<LoginViewModel>  implements LoginViewModel.ViewAction{

    FragmentMainBinding binding ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        binding.setLoginViewModel(vm);
        vm.setIView(this);
        vm.getRegisterType();
        click();
        listeners();
        passwordEditTextFilter();
        phoneNumberEditTextFilter();
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(LoginViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        binding.phoneNumber.setFilters(new InputFilter[] { filter,lengthFilter});
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
        binding.passwordEt.setFilters(new InputFilter[] { filter });
    }

    private void listeners(){

        vm.phoneNumber.observe(getViewLifecycleOwner() , data->{
            vm.areaCode.setValue("+"+binding.countryCodePicker.getSelectedCountryCode());
            authVM.phoneNumber.setValue(data);
            authVM.areaCode.setValue(binding.countryCodePicker.getSelectedCountryCode());

        });

        vm.areaCode.observe(getViewLifecycleOwner() , data->{
        });

        vm.pwd.observe(getViewLifecycleOwner() , data->{
        });
    }

    private void click() {
        binding.registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vm.registerType.observe(getViewLifecycleOwner(), data -> {
                    if(data == 1) {
                        Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_registrationFragment);
                    } else if (data == 2) {
                        Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_registrationCaptchaFragment);
                    }
                });

            }
        });

        binding.forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authVM.areaCode.setValue(binding.countryCodePicker.getSelectedCountryCode());
                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_forgetPasswordFragment);
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.cbAgreementPp.isChecked()) {
                    vm.areaCode.setValue("+" + binding.countryCodePicker.getSelectedCountryCode());
                    if (vm.phoneNumber.getValue().length() < 1) {
                        onErr(getString(io.openim.android.ouicore.R.string.mobile_number_empty_error_message), 6);
                        return;
                    }

//                    if (vm.phoneNumber.getValue().length() < 3) {
//                        onErr(getString(io.openim.android.ouicore.R.string.phone_number_invalid_error_message), 7);
//                        return;
//                    }

//                    boolean isValid = isValidPhoneNumber("+" + vm.areaCode.getValue() + vm.phoneNumber.getValue(), vm.areaCode.getValue());
//                    if (isValid) {
                        vm.login();
//                    } else {
//                        onErr(getString(io.openim.android.ouicore.R.string.phone_number_invalid_error_message), 8);
//                    }

                } else {
                    Toast.makeText(getContext(), getString(io.openim.android.ouicore.R.string.accept_agreement), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onErr(String msg, int errCode) {
        System.out.println(msg);
        Log.e("API Exception", "Error: $msg:::"+msg);
        Log.e("API Exception", "Error: Code:::"+errCode);
        binding.response.setVisibility(View.VISIBLE);
        binding.response.setText(""+msg);

    }

    @Override
    public void onSucce(Object o, String msg) {
        this.getActivity().startActivity(new Intent(this.getActivity(), MainActivity.class));
        this.getActivity().finish();
    }
}
