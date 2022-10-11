package io.bytechat.demo.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import java.util.regex.Pattern;

import io.bytechat.demo.databinding.FragmentSetNicknameBinding;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.openim.android.ouicore.base.BaseFragment;

public class SetNicknameFragment extends BaseFragment<ProfileViewModel> implements ProfileViewModel.ViewAction {

    private FragmentSetNicknameBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String nickname = requireArguments().getString("nickname");
        vm.nickname.setValue(nickname);
        binding.tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.nickname.setValue(vm.nickname.getValue().trim());
                if(isEmpty(binding.etNickname)){
                    if(isValidNickname(vm.nickname.getValue()))
                        vm.setNickname(view);
//                    Navigation.findNavController(view).getPreviousBackStackEntry().getSavedStateHandle().set("nickname", binding.etNickname.getText().toString().trim());
                } else {
                    Toast.makeText(getContext(), "Nickname is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).popBackStack();
            }
        });

        InputFilter lengthFilter = new InputFilter.LengthFilter(20);
        String charsToBeBlocked="[!@#$%^&*()+.=|<>?{}\\[\\]\\~\"-':;,`/§]";
        InputFilter inputFilter= (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            if(source!=null && charsToBeBlocked.contains(""+source) )
                return "";
            else
                return null;
        };
        binding.etNickname.setFilters(new InputFilter[] { lengthFilter, inputFilter});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSetNicknameBinding.inflate(inflater, container, false);
        binding.setVMProfile(vm);
        vm.setIView(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(ProfileViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onErr(String msg) {
      //  Toast.makeText(this.getActivity(), "Failed, Check your internet connection", Toast.LENGTH_SHORT).show();
        Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSucc(Object o) {
        this.getActivity().startActivity(new Intent(this.getActivity(), MainActivity.class));
        this.getActivity().finish();
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return true;

        return false;
    }

    private boolean isValidNickname(String nm){

        Pattern special = Pattern.compile ("[!@#$%^&*()+.=|<>?{}\\[\\]\\~\"-':;,`/]");

        if(special.matcher(nm).find()){
            onErr(getString(io.openim.android.ouicore.R.string.no_special_characters));
            return false;
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
