package io.bytechat.demo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.zxing.EncodeHintType;

import net.glxn.qrgen.android.QRCode;

import io.bytechat.demo.databinding.FragmentMyQRCodeBinding;
import io.bytechat.demo.oldrelease.vm.MyQRCodeViewModel;
import io.openim.android.ouicore.base.BaseFragment;


public class MyQRCodeFragment extends BaseFragment<MyQRCodeViewModel> implements MyQRCodeViewModel.ViewAction {

    FragmentMyQRCodeBinding binding ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        bindVM(MyQRCodeViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyQRCodeBinding.inflate(inflater, container, false);
        binding.setVm(vm);
        vm.setIView(this);
        init();
        return binding.getRoot();
    }

    private void init() {
        vm.avatar.observe(this.getActivity(),data->{
            binding.profileImg.load(data);

        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });

        vm.nickname.observe(this.getActivity(), data->{
            String nickname = vm.nickname.getValue();

            binding.nickname.setText(nickname);
        });

        vm.userID.observe(this.getActivity(), data->{
            if(data.isEmpty())return;
            Bitmap myBitmap = QRCode.from("io.openim.app/addFriend/"+data).withHint(EncodeHintType.MARGIN, 0).bitmap();
            binding.qrCodeImage.setImageBitmap(myBitmap);
        });

        vm.getProfileData();

    }

    @Override
    public void onErr(String msg) {

    }

    @Override
    public void onSucc(Object o) {

    }
}
