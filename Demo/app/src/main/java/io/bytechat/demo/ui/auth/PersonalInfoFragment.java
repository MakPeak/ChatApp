package io.bytechat.demo.ui.auth;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.FragmentPersonalInfoBinding;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.bytechat.demo.vm.LoginViewModel;
import io.bytechat.demo.vm.PersonalInfoViewModel;
import io.openim.android.ouicore.base.BaseFragment;

public class PersonalInfoFragment extends BaseFragment<PersonalInfoViewModel> implements PersonalInfoViewModel.ViewAction, PersonalInfoViewModel.ViewActionPicture {

    private FragmentPersonalInfoBinding binding;
    boolean skipBtnClicked = false ;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });

        binding.skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vm.nickname.getValue() == null || vm.nickname.getValue().isEmpty())
                    vm.nickname.setValue(authVM.phoneNumber.getValue());
                vm.userID.setValue(authVM.userId.getValue());
                authVM.checkAndSendDefaultImage(PersonalInfoFragment.this.getActivity());
                skipBtnClicked = true ;

            }
        });

        binding.enterBytechatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vm.nickname.getValue() == null || vm.nickname.getValue().isEmpty())
                    vm.nickname.setValue(authVM.phoneNumber.getValue());
                vm.userID.setValue(authVM.userId.getValue());
                authVM.checkAndSendDefaultImage(PersonalInfoFragment.this.getActivity());
                skipBtnClicked = true ;

//                vm.userID.setValue(authVM.userId.getValue());
//                if(vm.nickname.getValue().equals("")){
//                    binding.response.setVisibility(View.VISIBLE);
//                    binding.response.setText(getString(io.openim.android.ouicore.R.string.nickname_empty_error_message));
//                    return;
//                }
//                if(vm.imageURI == null || vm.imageURI.getValue() ==null ){
//                    binding.response.setVisibility(View.VISIBLE);
//                    binding.response.setText(getString(io.openim.android.ouicore.R.string.image_empty_error_message));
//                    return;
//                }
                vm.sendPersonalInfo();

            }
        });

        binding.cameraIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("fromPersonalInfo", "yes");
                Navigation.findNavController(view).navigate(R.id.action_personalInfoFragment_to_bottomSheetFragment, bundle);
            }
        });

        binding.uploadAvatarTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("fromPersonalInfo", "yes");
                Navigation.findNavController(view).navigate(R.id.action_personalInfoFragment_to_bottomSheetFragment, bundle);
            }
        });

        authVM.profileURI.observe(this.getActivity(), v->{
            setProfileImage(v);
        });

        authVM.openAvatar.observe(this.getActivity(), v->{
            if(v){
                authVM.openAvatar.setValue(false);
//                Navigation.findNavController(view).navigate(R.id.action_personalInfoFragment_to_selectAvatarFragment);
                NavHostFragment.findNavController(PersonalInfoFragment.this).navigate(R.id.action_personalInfoFragment_to_selectAvatarFragment);
            }
        });

        authVM.deleteRes.observe(this.getActivity(), v->{
            if(!v.equalsIgnoreCase("")) {
                try {
                    System.out.println("checkAndSendDefaultImage");
                    Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.default_profilephoto);
                    String extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                    File file = new File(extStorageDirectory, "ic_avatar_1.png");
                    FileOutputStream outStream = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    authVM.profileURI.setValue(Uri.fromFile(file));
                    authVM.avatar.setValue(file);
                    vm.imageURI.setValue(authVM.profileURI.getValue());
                    vm.uploadFile(this);
                    authVM.deleteRes.setValue("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        authVM.faceURL.observe(this.getActivity() , data->{
            vm.faceURL.setValue(data);

        });

        nicknameEditTextFilter();
    }

    private void nicknameEditTextFilter() {
        InputFilter lengthFilter = new InputFilter.LengthFilter(20);
        binding.nickname.setFilters(new InputFilter[] { lengthFilter});

        TextWatcher mWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.nickname.getText().toString().startsWith(" "))
                    binding.nickname.setText("");
            }
        };
        binding.nickname.addTextChangedListener(mWatcher);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonalInfoBinding.inflate(inflater, container, false);
        binding.setVm(vm);
        vm.setIView(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(PersonalInfoViewModel.class);
        super.onCreate(savedInstanceState);
    }

    public void setProfileImage(Uri profileImage){
        binding.cameraIv.setPadding(0,0,0,0);
        binding.cameraIv.setBackgroundResource(R.color.background);
        binding.cameraIv.setImageURI(profileImage);
        vm.imageURI.setValue(authVM.profileURI.getValue());
        vm.uploadFile();
    }

    @Override
    public void onErr(String msg) {
        Toast.makeText(this.getActivity(),getString(io.openim.android.ouicore.R.string.no_internet_error_message), Toast.LENGTH_SHORT).show();
    }

    @Override

    public void onSucce(Object o) {
        this.getActivity().startActivity(new Intent(this.getActivity(), MainActivity.class));
        this.getActivity().finish();
    }

    @Override
    public void finishUploadingFile() {
//        Toast.makeText(this.getActivity(), "uploading file finished successfully", Toast.LENGTH_SHORT).show();
        removeAvatar();
        if(skipBtnClicked){ // handle request after click skip btn
            vm.sendPersonalInfo();
            skipBtnClicked = false ;
        }
    }

    private void removeAvatar() {
        if(authVM.avatar!= null && authVM.avatar.getValue() != null)
            authVM.avatar.getValue().delete();

    }

    @Override
    public void errorUploadingFile() {
        removeAvatar();
//        Toast.makeText(this.getActivity(), "uploading file failure , check your internet connection", Toast.LENGTH_SHORT).show();

    }
}
