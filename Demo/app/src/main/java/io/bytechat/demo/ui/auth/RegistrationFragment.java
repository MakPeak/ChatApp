package io.bytechat.demo.ui.auth;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;
import static io.bytechat.demo.utils.UtilsFunctions.isValidPhoneNumber;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.FragmentRegistrationBinding;
import io.bytechat.demo.vm.RegistrationViewModel;
import io.openim.android.ouicore.base.BaseFragment;


public class RegistrationFragment extends BaseFragment<RegistrationViewModel> implements RegistrationViewModel.ViewAction {

    FragmentRegistrationBinding binding ;
    View view ;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view ;

        if(!authVM.phoneNumber.getValue().isEmpty()){
            vm.phoneNumber.setValue(authVM.phoneNumber.getValue());
            binding.countryCodePicker.setCountryForPhoneCode(Integer.parseInt(authVM.areaCode.getValue()));
        }
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();

            }
        });
        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vm.phoneNumber.getValue().length() < 1){
                    onErr(getString(io.openim.android.ouicore.R.string.mobile_number_empty_error_message));
                    return;
                }
//                if(vm.phoneNumber.getValue().length() < 3){
//                    onErr(getString(io.openim.android.ouicore.R.string.phone_number_invalid_error_message));
//                    return;
//                }

                vm.areaCode.setValue("+"+binding.countryCodePicker.getSelectedCountryCode());
//                boolean isValid = isValidPhoneNumber( "+"+vm.areaCode.getValue()+vm.phoneNumber.getValue() , vm.areaCode.getValue());
//                if(isValid){
                    vm.getVerificationCode();
//                }else{
//                    onErr(getString(io.openim.android.ouicore.R.string.phone_number_invalid_error_message));
//                }
            }
        });
        vm.phoneNumber.observe(this , data->{
            vm.areaCode.setValue("+"+binding.countryCodePicker.getSelectedCountryCode());
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
        binding.phoneNumber.setFilters(new InputFilter[] { filter ,lengthFilter});
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);

        binding.setVm(vm);
        vm.setIView(this);
        phoneNumberEditTextFilter();
        return binding.getRoot();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(RegistrationViewModel.class);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onErr(String msg) {
        binding.response.setVisibility(View.VISIBLE);
        binding.response.setText(""+msg);
    }

    @Override
    public void onSucce(Object o) {
        authVM.phoneNumber.setValue(vm.areaCode.getValue() + vm.phoneNumber.getValue());
        authVM.usedFor.setValue(1);
        Navigation.findNavController(view).navigate(R.id.action_registrationFragment_to_OTPFragment);
    }
}
