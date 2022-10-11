package io.bytechat.demo.ui.profile;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.FragmentProfilePhotoBinding;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouicore.base.BaseFragment;

public class ProfilePhotoFragment extends BaseFragment<ProfileViewModel> implements ProfileViewModel.ViewAction, ProfileViewModel.ViewActionPicture{

    private FragmentProfilePhotoBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm.getProfileData();

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        binding.ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.IS_LOGIN = false;
                Bundle bundle = new Bundle();
                bundle.putString("fromPersonalInfo", "no");
                Navigation.findNavController(view).navigate(R.id.action_profilePhotoFragment_to_bottomSheetFragment, bundle);
            }
        });

        binding.ivProfilePhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Constant.IS_LOGIN = false;
                Bundle bundle = new Bundle();
                bundle.putString("fromPersonalInfo", "save_photo_only");
                Navigation.findNavController(view).navigate(R.id.action_profilePhotoFragment_to_bottomSheetFragment, bundle);
                return true;
            }
        });

        vm.avatar.observe(this.getActivity(), data -> {
            if (getContext() != null) {
                try {
                    synchronized (this) {
                        if (getContext() != null)
                            this.wait(100);
                        Glide.with(this)
                            .load(data)
                            .centerCrop()
                            .into(binding.ivProfilePhoto);
                        System.out.println("vm.avatar " + vm.avatar.getValue());
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        authVM.profileURI.observe(this.getActivity(), v->{
            if(v != null) {
                setProfileImage(v);
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

        authVM.saveRes.observe(this.getActivity(), v->{
            if(!v.equalsIgnoreCase("")) {
                Glide.with(vm.getContext().getApplicationContext())
                    .asBitmap()
                    // FOR PICTURE
                    .load(vm.avatar.getValue())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            String path = MediaStore.Images.Media.insertImage(vm.getContext().getContentResolver(), resource, "", null);
                            Uri screenshotUri = Uri.parse(path);
                            Toast.makeText(vm.getContext(), "Photo saved Successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
//                            Toast.makeText(vm.getContext(), "Starting", Toast.LENGTH_SHORT).show();
                            super.onLoadStarted(placeholder);
                        }
                    });
                authVM.saveRes.setValue("");

            }
        });

        authVM.faceURL.observe(this.getActivity() , data->{
            Glide.with(view)
                .load(data)
                .into(binding.ivProfilePhoto);
//            ApngDecoder.decodeApngAsyncInto(vm.getContext(), data, binding.ivProfilePhoto);
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfilePhotoBinding.inflate(inflater, container, false);
        binding.setVMProfile(vm);
        vm.setIView(this);
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(ProfileViewModel.class);
        super.onCreate(savedInstanceState);
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

    public void setProfileImage(Uri profileImage){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), profileImage);
            binding.ivProfilePhoto.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        vm.imageURI.setValue(authVM.profileURI.getValue());
    }

    @Override
    public void finishUploadingFile() {
        vm.setFaceURL(authVM.faceURL.getValue());
    }

    @Override
    public void errorUploadingFile() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
