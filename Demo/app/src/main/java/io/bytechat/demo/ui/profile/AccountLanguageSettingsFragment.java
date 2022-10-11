package io.bytechat.demo.ui.profile;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.FragmentAccountLanguageSettingsBinding;
import io.bytechat.demo.databinding.FragmentAccountSettingsBinding;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.bytechat.demo.oldrelease.ui.main.ProfileFragment;
import io.bytechat.demo.ui.SplashActivity;
import io.bytechat.demo.ui.auth.AuthActivity;
import io.bytechat.demo.ui.widget.LanguageChangeDialog;
import io.bytechat.demo.vm.PersonalInfoViewModel;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.net.bage.GsonHel;
import io.openim.android.ouicore.utils.SharedPreferencesUtil;

public class AccountLanguageSettingsFragment extends BaseFragment<PersonalInfoViewModel> implements PersonalInfoViewModel.ViewAction, LanguageChangeDialog.LanguageReturnListener {

    private FragmentAccountLanguageSettingsBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String language = SharedPreferencesUtil.get(getContext()).getString("language");
        if(language != null){
            if(language.equalsIgnoreCase(getResources().getString(io.openim.android.ouicore.R.string.follow_the_system))){
                binding.ivSelectionChinese.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
                binding.ivSelectionEnglish.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
                binding.ivSelectionSystem.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_blue_24dp));
            } else if(language.equalsIgnoreCase(getResources().getString(io.openim.android.ouicore.R.string.english))){
                binding.ivSelectionChinese.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
                binding.ivSelectionEnglish.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_blue_24dp));
                binding.ivSelectionSystem.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
            } else if(language.equalsIgnoreCase(getResources().getString(io.openim.android.ouicore.R.string.simplified_chinese))){
                binding.ivSelectionChinese.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_blue_24dp));
                binding.ivSelectionEnglish.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
                binding.ivSelectionSystem.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
            } else {
                binding.ivSelectionChinese.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
                binding.ivSelectionEnglish.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
                binding.ivSelectionSystem.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_blue_24dp));
            }
        } else {
            binding.ivSelectionChinese.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
            binding.ivSelectionEnglish.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
            binding.ivSelectionSystem.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_blue_24dp));
        }

        binding.rlLanguageSystem.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                LanguageChangeDialog languageChangeDialog = new LanguageChangeDialog(getActivity(), getActivity().getResources().getString(io.openim.android.ouicore.R.string.follow_the_system), new LanguageChangeDialog.LanguageReturnListener() {
                    @Override
                    public void returnLanguage(String language) {
                        if(!language.isEmpty()) {
                            SharedPreferencesUtil.get(getContext()).setCache("language", language);
                            binding.ivSelectionChinese.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
                            binding.ivSelectionEnglish.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
                            binding.ivSelectionSystem.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_blue_24dp));
                            getActivity().finishAffinity();
                            Intent intent = new Intent(getContext(), SplashActivity.class);
                            startActivity(intent);

                        }
                    }

                });
                languageChangeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                languageChangeDialog.show();
            }
        });

        binding.rlLanguageEnglish.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                LanguageChangeDialog languageChangeDialog = new LanguageChangeDialog(getActivity(), getActivity().getResources().getString(io.openim.android.ouicore.R.string.english), new LanguageChangeDialog.LanguageReturnListener() {
                    @Override
                    public void returnLanguage(String language) {
                        if(!language.isEmpty()) {
                            SharedPreferencesUtil.get(getContext()).setCache("language", language);
                            binding.ivSelectionChinese.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
                            binding.ivSelectionEnglish.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_blue_24dp));
                            binding.ivSelectionSystem.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
                            getActivity().finishAffinity();
                            Intent intent = new Intent(getContext(), SplashActivity.class);
                            startActivity(intent);
                        }
                    }

                });
                languageChangeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                languageChangeDialog.show();
            }

        });

        binding.rlLanguageChinese.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                LanguageChangeDialog languageChangeDialog = new LanguageChangeDialog(getActivity(), getActivity().getResources().getString(io.openim.android.ouicore.R.string.simplified_chinese), new LanguageChangeDialog.LanguageReturnListener() {
                    @Override
                    public void returnLanguage(String language) {
                        if(!language.isEmpty()) {
                            SharedPreferencesUtil.get(getContext()).setCache("language", language);
                            binding.ivSelectionChinese.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_blue_24dp));
                            binding.ivSelectionEnglish.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
                            binding.ivSelectionSystem.setImageDrawable(getResources().getDrawable(R.drawable.circle_hollow_gray_border));
                            getActivity().finishAffinity();
                            Intent intent = new Intent(getContext(), SplashActivity.class);
                            startActivity(intent);
                        }
                    }

                });
                languageChangeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                languageChangeDialog.show();
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
//                Navigation.findNavController(view).getPreviousBackStackEntry().getSavedStateHandle().set("language", languageValue);
                Navigation.findNavController(view).popBackStack();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountLanguageSettingsBinding.inflate(inflater, container, false);
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

    @Override
    public void returnLanguage(String language) {
        SharedPreferencesUtil.get(getContext()).setCache("language", language);
        startActivity(new Intent(getContext(), SplashActivity.class));
        getActivity().finishAffinity();
    }
}
