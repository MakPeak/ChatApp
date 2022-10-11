package io.bytechat.demo.ui.profile;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.FragmentAccountSettingsBinding;
import io.bytechat.demo.databinding.FragmentMyInformationBinding;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.bytechat.demo.vm.PersonalInfoViewModel;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.utils.SharedPreferencesUtil;

public class AccountSettingsFragment extends BaseFragment<PersonalInfoViewModel> implements PersonalInfoViewModel.ViewAction {

    private FragmentAccountSettingsBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rlLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_accountSettingsFragment_to_accountLanguageSettingsFragment);
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        String language = SharedPreferencesUtil.get(getContext()).getString("language");
        if(!language.isEmpty()) {
            binding.tvLanguageValue.setText(language);
        } else {
            binding.tvLanguageValue.setText(getContext().getString(io.openim.android.ouicore.R.string.follow_the_system));
        }

//        MutableLiveData<String> liveData = Objects.requireNonNull(NavHostFragment.findNavController(this).getCurrentBackStackEntry())
//            .getSavedStateHandle()
//            .getLiveData("language");
//        liveData.observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//                binding.tvLanguageValue.setText(s);
//            }
//        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false);
        binding.setVm(vm);
        vm.setIView(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(PersonalInfoViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onErr(String msg) {
        Toast.makeText(this.getActivity(), "Failed, Check your internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override

    public void onSucce(Object o) {
        this.getActivity().startActivity(new Intent(this.getActivity(), MainActivity.class));
        this.getActivity().finish();
    }

    @Override
    public void finishUploadingFile() {
        removeAvatar();
    }

    private void removeAvatar() {
        if(authVM.avatar!= null && authVM.avatar.getValue() != null)
            authVM.avatar.getValue().delete();
    }

    @Override
    public void errorUploadingFile() {
        removeAvatar();
    }
}
