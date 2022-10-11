package io.bytechat.demo.ui.profile;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.IOException;

import io.bytechat.demo.R;

import io.bytechat.demo.databinding.ActivityMyInformationBinding;
import io.bytechat.demo.oldrelease.ui.search.WebViewActivity;
import io.bytechat.demo.oldrelease.vm.MainVM;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.openim.android.ouicore.base.BaseActivity;

public class MyInformationActivity extends BaseActivity<ProfileViewModel, ActivityMyInformationBinding> implements ProfileViewModel.ViewAction, ProfileViewModel.ViewActionPicture {

    String openFragment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(ProfileViewModel.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityMyInformationBinding.inflate(getLayoutInflater()));

        openFragment = getIntent().getExtras().getString("profilePhotoFragment");
        Bundle bundle = new Bundle();
        bundle.putString("openFragment", openFragment);

        Navigation.findNavController(this, R.id.nav_host_fragment_activity_my_information).setGraph(R.navigation.profile_navigation, bundle);

//        if(openFragment.equalsIgnoreCase("profilePhotoFragment")){
//            Navigation.findNavController(view.getRoot()).navigate(R.id.action_myInformationFragment_to_profilePhotoFragment);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            Uri uri =  data.getData() ;
            uploadProfileImage(uri);
//            imgProfile.setImageURI(fileUri);
            authVM.profileURI.setValue(data.getData()) ;
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadProfileImage(Uri profileImage){
        vm.imageURI.setValue(profileImage);
        vm.uploadFile(this);

    }

    @Override
    public void onErr(String msg) {
        Toast.makeText(MyInformationActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSucc(Object o) {
        Toast.makeText(MyInformationActivity.this, o.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishUploadingFile() {
//        Toast.makeText(MyInformationActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
        vm.setFaceURL(authVM.faceURL.getValue());
    }

    @Override
    public void errorUploadingFile() {
        Toast.makeText(MyInformationActivity.this, "File Uploading Error", Toast.LENGTH_SHORT).show();
    }
}
