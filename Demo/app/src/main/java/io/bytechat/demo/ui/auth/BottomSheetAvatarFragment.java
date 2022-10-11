package io.bytechat.demo.ui.auth;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import android.Manifest;

import java.util.List;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.FragmentBottomSheetAvatarBinding;
import io.openim.android.ouiconversation.utils.Constant;

public class BottomSheetAvatarFragment extends BottomSheetDialogFragment {

    private FragmentBottomSheetAvatarBinding binding;
    String fromPersonalInfo = "";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fromPersonalInfo = requireArguments().getString("fromPersonalInfo");

        if(fromPersonalInfo.equalsIgnoreCase("yes")){
            binding.tvSavePhoto.setVisibility(View.GONE);
            binding.tvDelete.setVisibility(View.VISIBLE);
            binding.tvLine3.setVisibility(View.GONE);
            binding.tvLine4.setVisibility(View.VISIBLE);
        }  else if(fromPersonalInfo.equalsIgnoreCase("save_photo_only")){
            binding.tvTakePhoto.setVisibility(View.GONE);
            binding.tvChooseFromAlbum.setVisibility(View.GONE);
            binding.tvChooseDefaultPhoto.setVisibility(View.GONE);
            binding.tvLine1.setVisibility(View.GONE);
            binding.tvLine2.setVisibility(View.GONE);
            binding.tvLine3.setVisibility(View.GONE);
            binding.tvSavePhoto.setVisibility(View.VISIBLE);
        } else {
            binding.tvLine3.setVisibility(View.VISIBLE);
            binding.tvLine4.setVisibility(View.VISIBLE);
            binding.tvSavePhoto.setVisibility(View.VISIBLE);
            binding.tvDelete.setVisibility(View.VISIBLE);
        }

        binding.tvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker
                    .with(BottomSheetAvatarFragment.this.getActivity())
                    .cameraOnly()
                    .cropSquare()
                    .compress(1024)
                    .start();

                dismiss();
            }
        });

        binding.tvChooseDefaultPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Entering Default Photo");
                Dexter.withContext(BottomSheetAvatarFragment.this.getActivity())
                    .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener(){
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            System.out.println("onPermissionsChecked: accepted " );
                            if(multiplePermissionsReport.areAllPermissionsGranted()){
                                if(!Constant.IS_LOGIN) {
                                    NavHostFragment.findNavController(BottomSheetAvatarFragment.this).navigate(R.id.action_bottomSheetFragment_to_selectAvatarFragment2);
                                } else {
                                    NavHostFragment.findNavController(BottomSheetAvatarFragment.this).navigateUp();
                                }
                                authVM.openAvatar.setValue(true);
                                dismiss();
                            } else {
                                Log.e("onPermissionsChecked: ","rejected " );
                            }

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            Log.e("onPermissionRat ","rejected  " );
                            permissionToken.continuePermissionRequest();
                        }
                    }).withErrorListener(new PermissionRequestErrorListener() {
                        @Override public void onError(DexterError error) {
                            Log.e("Dexter", "There was an error: " + error.toString());
                        }
                    }).check();

            }
        });

        binding.tvChooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker
                    .with(BottomSheetAvatarFragment.this.getActivity())
                    .galleryOnly()
                    .cropSquare()
                    .compress(1024)
                    .start();

                dismiss();
            }
        });

        binding.tvSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authVM.saveRes.setValue("run");
                dismiss();
            }
        });

        binding.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authVM.deleteRes.setValue("run");
                dismiss();
            }
        });

        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(Constant.IS_LOGIN){
            authVM.checkAndSendDefaultImage(BottomSheetAvatarFragment.this.getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBottomSheetAvatarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
