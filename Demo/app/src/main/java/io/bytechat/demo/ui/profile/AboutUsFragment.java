package io.bytechat.demo.ui.profile;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.bytechat.demo.databinding.FragmentAboutUsBinding;
import io.bytechat.demo.databinding.FragmentAccountLanguageSettingsBinding;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.bytechat.demo.vm.PersonalInfoViewModel;
import io.openim.android.ouicore.base.BaseFragment;

public class AboutUsFragment extends BaseFragment<ProfileViewModel> implements ProfileViewModel.ViewAction {

    private FragmentAboutUsBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rlVersionUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.latestVersionDataMutableLiveData.observe(getActivity(), data ->{
                    if(data !=null) {
                        PackageManager pm = getContext().getApplicationContext().getPackageManager();
                        String pkgName = getContext().getApplicationContext().getPackageName();
                        PackageInfo pkgInfo = null;
                        try {
                            pkgInfo = pm.getPackageInfo(pkgName, 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String ver = pkgInfo.versionName;
                        if (vm.latestVersionDataMutableLiveData.getValue().getVersion().equalsIgnoreCase(ver)) {
                            VersionUpdateLatestDialog versionUpdateLatestDialog = new VersionUpdateLatestDialog(getActivity());
                            versionUpdateLatestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            versionUpdateLatestDialog.show();
                        } else {
                            VersionUpdateByChoiceDialog versionUpdateByChoiceDialog = new VersionUpdateByChoiceDialog(getActivity(),
                                vm.latestVersionDataMutableLiveData.getValue().getDownloadUrl(), vm.latestVersionDataMutableLiveData.getValue().getVersion(),
                                vm.latestVersionDataMutableLiveData.getValue().getContent());
                            versionUpdateByChoiceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            versionUpdateByChoiceDialog.show();
                            versionUpdateByChoiceDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        }
                    }else{
                        VersionUpdateLatestDialog versionUpdateLatestDialog = new VersionUpdateLatestDialog(getActivity());
                        versionUpdateLatestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        versionUpdateLatestDialog.show();
                    }
                });
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutUsBinding.inflate(inflater, container, false);
        binding.setVm(vm);
        vm.setIView(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(ProfileViewModel.class);
        super.onCreate(savedInstanceState);
        vm.getLatestVersion();
    }

    @Override
    public void onErr(String msg) {
        Toast.makeText(this.getActivity(), "Failed, Check your internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSucc(Object o) {

    }

    private void removeAvatar() {
        if(authVM.avatar!= null && authVM.avatar.getValue() != null)
            authVM.avatar.getValue().delete();
    }
}
