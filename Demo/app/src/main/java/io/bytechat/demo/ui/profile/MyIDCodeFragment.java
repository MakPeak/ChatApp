package io.bytechat.demo.ui.profile;

import android.content.Context;
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
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.FragmentMyIDCodeBinding;
import io.bytechat.demo.databinding.FragmentMyInformationBinding;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.bytechat.demo.oldrelease.vm.MyQRCodeViewModel;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.openim.android.ouicore.base.BaseFragment;


public class MyIDCodeFragment extends BaseFragment<ProfileViewModel> implements ProfileViewModel.ViewAction {

    FragmentMyIDCodeBinding binding ;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm.nickname.observe(this.getActivity(), data->{
            binding.tvUserName.setText(vm.nickname.getValue());
        });

        vm.userID.observe(this.getActivity(), data->{
            binding.tvUserId.setText(vm.userID.getValue());
        });

        vm.avatar.observe(this.getActivity(), data->{
            if(getContext()!=null) {
                try {
                    synchronized(this){
                        if(getContext()!=null)
                            this.wait(100);
                        Glide.with(this)
                            .load(data)
                            .into(binding.ivUserPic);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        binding.ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(vm.userID.getValue());
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("User ID", vm.userID.getValue());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(getContext(), getContext().getString(io.openim.android.ouicore.R.string.user_id_copied), Toast.LENGTH_SHORT).show();
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyIDCodeBinding.inflate(inflater, container, false);
        binding.setVm(vm);
        vm.setIView(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(ProfileViewModel.class);
        super.onCreate(savedInstanceState);
        vm.getProfileData();
    }

    @Override
    public void onErr(String msg) {
        Toast.makeText(this.getActivity(), "Failed, Check your internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSucc(Object o) {
        this.getActivity().startActivity(new Intent(this.getActivity(), MainActivity.class));
        this.getActivity().finish();
    }
}
