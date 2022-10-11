package io.bytechat.demo.ui.profile;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.bytechat.demo.R;

import io.bytechat.demo.databinding.FragmentMyInformationBinding;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.bytechat.demo.ui.auth.BottomSheetAvatarFragment;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouicore.base.BaseFragment;

public class MyInformationFragment extends BaseFragment<ProfileViewModel> implements ProfileViewModel.ViewAction, ProfileViewModel.ViewActionPicture  {

    private FragmentMyInformationBinding binding;
    boolean selectAvatarScreen = true;
    public ProfileViewModel profileViewModel;
    boolean trigger = true;
    int dateInInt;
    boolean handleImageAvatarCase = false ;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm.getProfileData();

        Bundle bundle = getArguments();
        if(bundle.get("openFragment").toString().equalsIgnoreCase("profilePhotoFragment")){
            Navigation.findNavController(view).navigate(R.id.action_myInformationFragment_to_profilePhotoFragment, bundle);
        }

        vm.gender.observe(this.getActivity(), data->{
            if(data.toString().equalsIgnoreCase("2")) {
                binding.tvGenderValue.setText(getResources().getString(io.openim.android.ouicore.R.string.male));
            } else if(data.toString().equalsIgnoreCase("1")) {
                binding.tvGenderValue.setText(getResources().getString(io.openim.android.ouicore.R.string.female));
            }
        });

        vm.birthday.observe(this.getActivity(), data->{
//            long milliseconds = Integer.parseInt(data.toString()) * 1000L;
//            Date date = new Date(milliseconds);
//            SimpleDateFormat DateFor = new SimpleDateFormat("yyyy/MM/dd");
//            String stringDate = DateFor.format(date);
            binding.tvBirthdayValue.setText(data);
        });

        MutableLiveData<String> liveData = Objects.requireNonNull(NavHostFragment.findNavController(this).getCurrentBackStackEntry())
            .getSavedStateHandle()
            .getLiveData("date");
        liveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                trigger = false;
                long milliseconds = Integer.parseInt(s) * 1000L;
                Date date = new Date(milliseconds);
                SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd");
                String stringDate = DateFor.format(date);
                vm.birthday.postValue(stringDate);
                binding.tvBirthdayValue.setText(stringDate);
            }
        });

        vm.nickname.observe(this.getActivity(), data->{
            binding.tvNicknameValue.setText(vm.nickname.getValue());
        });

        vm.userID.observe(this.getActivity(), data->{
            binding.tvIdCodeValue.setText(vm.userID.getValue());
        });

        vm.phoneNumber.observe(this.getActivity(), data->{
            binding.tvPhoneValue.setText(vm.phoneNumber.getValue());
        });

        if(selectAvatarScreen) {
            vm.avatar.observe(this.getActivity(), data -> {
                if (getContext() != null) {
                    try {
                        synchronized (this) {
                            if (getContext() != null)
                                this.wait(100);
                            Glide.with(this)
                                .load(data)
                                .centerCrop()
                                .into(binding.ivAvatarImage);
                            System.out.println("vm.avatar " + vm.imageURI.getValue());
//                            if(vm.imageURI.getValue() != null) {
//                                vm.setFaceURL(Objects.requireNonNull(vm.imageURI.getValue()).getPath());
//                                vm.imageURI.setValue(null);
//                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            });
        } else {
            System.out.println("print set face url 2");
//            vm.avatar.postValue(Objects.requireNonNull(vm.imageURI.getValue()).getPath());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), vm.imageURI.getValue());
                binding.ivAvatarImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        authVM.profileURI.observe(this.getActivity(), v->{
            System.out.println("authVM.profileURI.observe 1");
            if(v != null) {
                System.out.println("authVM.profileURI.observe 2");
                setProfileImage(v);
            }
        });

        binding.rlNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("nickname", vm.nickname.getValue());
                Navigation.findNavController(view).navigate(R.id.action_myInformationFragment_to_setNicknameFragment, bundle);
            }
        });

        binding.rlGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenderDialog genderDialog = new GenderDialog(getActivity(), new GenderDialog.GenderReturnListener() {
                    @Override
                    public void returnInt(int gender) {
                        vm.setGender(gender);
                        if(gender == 2) {
                            binding.tvGenderValue.setText(getResources().getString(io.openim.android.ouicore.R.string.male));
                        } else if(gender == 1){
                            binding.tvGenderValue.setText(getResources().getString(io.openim.android.ouicore.R.string.female));
                        }
                    }
                });
                genderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                genderDialog.show();
            }
        });

        binding.rlBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Navigation.findNavController(view).navigate(R.id.action_myInformationFragment_to_datePickerFragment);
                Date currentDate = Calendar.getInstance().getTime();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -100); // to get previous year add -1
                Date previousDate = cal.getTime();

                new SingleDateAndTimePickerDialog.Builder(getContext())
                    .curved()
                    .displayMinutes(false)
                    .displayHours(false)
                    .displayDays(false)
                    .displayMonth(true)
                    .displayYears(true)
                    .displayDaysOfMonth(true)
                    .mainColor(Color.BLACK)
                    .maxDateRange(currentDate)
                    .bottomSheet()
                    .backgroundColor(Color.WHITE)
                    .listener(new SingleDateAndTimePickerDialog.Listener() {
                        @Override
                        public void onDateSelected(Date date) {
                            if(date.getTime() > System.currentTimeMillis()+26400000)
                                return;
                            SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                            try {
                                //DATE TO LONG
                                Date d = sdf.parse(date.toString());
                                long milliseconds = d.getTime();
                                dateInInt = (int) (milliseconds/1000);
                                SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd");
                                String stringDate = DateFor.format(d);
                                vm.birthday.postValue(stringDate);
                                vm.setBirthday(stringDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    })
                    .display();
            }
        });

        binding.rlQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_myInformationFragment_to_qrCodeFragment);
            }
        });

        binding.rlAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                handleImageAvatarCase = true ;
//                Constant.IS_LOGIN = false;
//                Navigation.findNavController(view).navigate(R.id.action_myInformationFragment_to_bottomSheetFragment);
                Navigation.findNavController(view).navigate(R.id.action_myInformationFragment_to_profilePhotoFragment);
            }
        });

        binding.rlIdCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_myInformationFragment_to_idCodeFragment);
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
    public void onResume(){
        super.onResume();

    }

    public void setProfileImage(Uri profileImage){
        selectAvatarScreen = false;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), profileImage);
            binding.ivAvatarImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        vm.imageURI.setValue(authVM.profileURI.getValue());
        if(handleImageAvatarCase) {
            handleImageAvatarCase = false;
            vm.uploadFile(this);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyInformationBinding.inflate(inflater, container, false);
        binding.setVMProfile(vm);
        vm.setIView(this);
//        if(trigger) {
//            profileViewModel.birthday.observe(getViewLifecycleOwner(), data -> {
//                trigger = false;
////                long milliseconds = Integer.parseInt(data.toString()) * 1000L;
////                Date date = new Date(milliseconds);
////                SimpleDateFormat DateFor = new SimpleDateFormat("yyyy/MM/dd");
////                String stringDate = DateFor.format(date);
//                binding.tvBirthdayValue.setText(data.toString());
//            });
//        }
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(ProfileViewModel.class);
        super.onCreate(savedInstanceState);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        vm.getProfileData();

    }

    @Override
    public void onErr(String msg) {
        Toast.makeText(this.getActivity(), "Failed, Check your internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishUploadingFile() {
//        Toast.makeText(this.getActivity(), "Photo updated successfully", Toast.LENGTH_SHORT).show();
//        removeAvatar();
        System.out.println("finishUploadingFile 1");
        vm.setFaceURL(authVM.faceURL.getValue());
        authVM.profileURI.setValue(null);

    }

    private void removeAvatar() {
        if(authVM.avatar!= null && authVM.avatar.getValue() != null) {
            authVM.avatar.getValue().delete();
        }

    }

    @Override
    public void errorUploadingFile() {
        removeAvatar();
        Toast.makeText(this.getActivity(), "uploading file failure , check your internet connection", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSucc(Object o) {
        this.getActivity().startActivity(new Intent(this.getActivity(), MainActivity.class));
        this.getActivity().finish();
    }

}
