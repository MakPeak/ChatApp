package io.bytechat.demo.ui.auth;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import io.bytechat.demo.R;
import io.bytechat.demo.adapter.AvatarAdapter;
import io.bytechat.demo.databinding.FragmentSelectAvatarBinding;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.bytechat.demo.ui.view.ViewSelectAvatar;
import io.openim.android.ouicore.base.BaseFragment;


public class SelectAvatarFragment extends BaseFragment<ProfileViewModel> implements ProfileViewModel.ViewAction, ProfileViewModel.ViewActionPicture, ViewSelectAvatar {

    private FragmentSelectAvatarBinding binding;
    private AvatarAdapter adapter ;
    private RecyclerView.LayoutManager mLayoutManager;
    View view ;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view= view ;
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(ProfileViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSelectAvatarBinding.inflate(inflater, container, false);
        binding.setVMProfile(vm);
        vm.setIView(this);
        mLayoutManager = new GridLayoutManager(getActivity(), 4);
        binding.rvDefaultPhoto.setLayoutManager(mLayoutManager);

        List<Integer> list = new LinkedList<>();
//        list.add(R.mipmap.ic_avatar_1);
        list.add(R.mipmap.default_photo_1);
        list.add(R.mipmap.default_photo_2);
        list.add(R.mipmap.default_photo_3);
        list.add(R.mipmap.default_photo_4);
        list.add(R.mipmap.default_photo_5);
        list.add(R.mipmap.default_photo_6);
        list.add(R.mipmap.default_photo_7);
        list.add(R.mipmap.default_photo_8);
        list.add(R.mipmap.default_photo_9);
        list.add(R.mipmap.default_photo_10);
        list.add(R.mipmap.default_photo_11);
        list.add(R.mipmap.default_photo_12);
        list.add(R.mipmap.default_photo_13);
        list.add(R.mipmap.default_photo_14);
        list.add(R.mipmap.default_photo_15);
        list.add(R.mipmap.default_photo_16);
        list.add(R.mipmap.default_photo_17);
        list.add(R.mipmap.default_photo_18);
        list.add(R.mipmap.default_photo_19);
        list.add(R.mipmap.default_photo_20);
        list.add(R.mipmap.default_photo_21);
        list.add(R.mipmap.default_photo_22);
        list.add(R.mipmap.default_photo_23);
        list.add(R.mipmap.default_photo_24);
        list.add(R.mipmap.default_photo_25);
        list.add(R.mipmap.default_photo_26);
        list.add(R.mipmap.default_photo_27);
        list.add(R.mipmap.default_photo_28);
        list.add(R.mipmap.default_photo_29);
        list.add(R.mipmap.default_photo_30);
        list.add(R.mipmap.default_photo_31);
        list.add(R.mipmap.default_photo_32);
        list.add(R.mipmap.default_photo_33);
        list.add(R.mipmap.default_photo_34);
        list.add(R.mipmap.default_photo_35);
        list.add(R.mipmap.default_photo_36);
        list.add(R.mipmap.default_photo_37);
        list.add(R.mipmap.default_photo_38);

        adapter = new AvatarAdapter(list, this);
        binding.rvDefaultPhoto.setAdapter(adapter);

        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void avatarSelected(int id) {

        Resources resources = view.getResources();

        try {
            System.out.println("avatarSelected");
            Bitmap bm = BitmapFactory.decodeResource( getResources(), id);
            File outputDir = this.getContext().getCacheDir(); // context being the Activity pointer
            File outputFile = File.createTempFile("ic_avatar_1", ".png", outputDir);

            File file = outputFile;
            FileOutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

            authVM.profileURI.setValue(Uri.fromFile(file));
            authVM.avatar.setValue(file);
            vm.imageURI.setValue(authVM.profileURI.getValue());
            vm.uploadFile(this);
            Navigation.findNavController(view).popBackStack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErr(String msg) {

    }

    @Override
    public void onSucc(Object o) {

    }

    @Override
    public void finishUploadingFile() {
        vm.setFaceURL(authVM.faceURL.getValue());
    }

    @Override
    public void errorUploadingFile() {

    }
}

