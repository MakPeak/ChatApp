package io.bytechat.demo.oldrelease.ui.main;

import static io.openim.android.ouicore.entity.LoginCertificate.signOut;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Locale;

import cn.jpush.android.api.JPushInterface;
import io.bytechat.demo.R;
import io.bytechat.demo.databinding.FragmentProfileBinding;
import io.bytechat.demo.oldrelease.utils.LocaleHelper;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.bytechat.demo.ui.auth.AuthActivity;
import io.bytechat.demo.ui.profile.AboutUsActivity;
import io.bytechat.demo.ui.profile.AccountSettingsActivity;

import io.bytechat.demo.ui.profile.MyInformationActivity;
import io.bytechat.demo.ui.profile.ProfilePhotoFragment;
import io.bytechat.demo.ui.profile.SignOutDialog;

import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.utils.SharedPreferencesUtil;

public class ProfileFragment extends BaseFragment<ProfileViewModel> implements ProfileViewModel.ViewAction{

    FragmentProfileBinding binding ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.setVm(vm);
        vm.setIView(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LocaleHelper.settingLanguage(getActivity());
        bindVM(ProfileViewModel.class);
        super.onCreate(savedInstanceState);
        System.out.println("Matt 2");
        vm.getProfileData();
    }

    @Override
    public void onErr(String msg) {

    }

    @Override
    public void onSucc(Object o) {

    }

    @Override
    public void onResume(){
        super.onResume();
        vm.getProfileData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm.nickname.observe(this.requireActivity(), data->{
            binding.nickname.setText(vm.nickname.getValue());
        });

        vm.userID.observe(this.requireActivity(), data->{
            binding.userId.setText("ID: " + vm.userID.getValue());
        });

        binding.clSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignOutDialog signOutDialog = new SignOutDialog(getActivity(), new SignOutDialog.GenderReturnListener() {
                    @Override
                    public void returnInt(int gender) {
                        signOut(ProfileFragment.this.getContext());
                        startActivity(new Intent(ProfileFragment.this.getContext(), AuthActivity.class));
                        ProfileFragment.this.getActivity().finish();
                        JPushInterface.deleteAlias(getContext(), 123456);
                    }
                });
                signOutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                signOutDialog.show();

            }
        });

        binding.profileImgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), MyInformationActivity.class);
                intent.putExtra("profilePhotoFragment", "profilePhotoFragment");
                startActivity(intent);

            }
        });

        vm.avatar.observe(this.requireActivity(),data->{
            if(getContext()!=null) {
                try {
                    synchronized(this){
                        if(getContext()!=null)
                            this.wait(100);
                        binding.profileImg.load(data);
                        binding.profileImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        binding.myInformationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyInformationActivity.class);
                intent.putExtra("profilePhotoFragment", "informationFragment");
                startActivity(intent);
            }
        });

        binding.accountSettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AccountSettingsActivity.class);
                startActivity(intent);
            }
        });

        binding.aboutUsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AboutUsActivity.class);
                startActivity(intent);
            }
        });

        binding.qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment.this.getActivity().startActivity(new Intent(ProfileFragment.this.getActivity(), MyQRCodeActivity.class));
            }
        });

        binding.userId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment.this.getActivity().startActivity(new Intent(ProfileFragment.this.getActivity(), MyIDCodeActivity.class));
            }
        });

    }
}
