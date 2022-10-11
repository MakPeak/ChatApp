package io.openim.android.ouigroup.ui;

import static io.openim.android.ouicore.utils.Constant.GROUP_ID;
import static io.openim.android.ouicore.utils.Constant.ID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.github.dhaval2404.imagepicker.ImagePicker;

import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.utils.Common;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.widget.ImageTxtViewHolder;
import io.openim.android.ouigroup.R;
import io.openim.android.ouigroup.databinding.ActivityCreateGroupBinding;
import io.openim.android.ouigroup.vm.GroupVM;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.GroupInfo;

public class CreateGroupActivity extends BaseActivity<GroupVM, ActivityCreateGroupBinding> implements GroupVM.ViewActionPicture{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVMByCache(GroupVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityCreateGroupBinding.inflate(getLayoutInflater()));
        view.setGroupVM(vm);
        sink();

        initView();

    }

    public void toBack(View view) {
        finish();
    }

    private void initView() {
        FriendInfo friendInfo=new FriendInfo();
        LoginCertificate loginCertificate=LoginCertificate.getCache(this);
        friendInfo.setUserID(loginCertificate.userID);
        friendInfo.setNickname(loginCertificate.nickname);
        vm.selectedFriendInfo.getValue().add(0, friendInfo);

//        view.selectNum.setText(vm.selectedFriendInfo.getValue().size()+1 + "人");
//        view.recyclerview.setLayoutManager(new GridLayoutManager(this, 5));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter<FriendInfo, ImageTxtViewHolder>(ImageTxtViewHolder.class) {

            @Override
            public void onBindView(@NonNull ImageTxtViewHolder holder, FriendInfo data, int position) {
                holder.view.img.load(data.getFaceURL());
                holder.view.txt.setText(data.getNickname());
            }

        };

        view.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(CreateGroupActivity.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
            }
        });
        view.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.submit.setEnabled(false);
                vm.createGroup();
            }
        });
        view.uploadImgText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                vm.uploadFile(this, CreateGroupActivity.this);
            }
        });

//        view.recyclerview.setAdapter(adapter);
        adapter.setItems(vm.selectedFriendInfo.getValue());
    }

    @Override
    public void onError(String error) {
        super.onError(error);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        view.submit.setEnabled(true);
    }

    @Override
    public void onSuccess(Object body) {
        super.onSuccess(body);
        Toast.makeText(this, getString(io.openim.android.ouicore.R.string.create_succ), Toast.LENGTH_SHORT).show();
        GroupInfo groupInfo = (GroupInfo) body;
        ARouter.getInstance().build(Routes.Conversation.CHAT)
            .withString(GROUP_ID, groupInfo.getGroupID())
            .withString(io.openim.android.ouicore.utils.Constant.K_NAME, groupInfo.getGroupName())
            .navigation();

        setResult(RESULT_OK);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            Uri uri =  data.getData() ;
//            imgProfile.setImageURI(fileUri);
            vm.imageURI.setValue(data.getData()) ;
            vm.uploadFile(this, this);
            view.avatarImage.setImageURI(data.getData());
            view.avatarImage.setPadding(0,0,0,0);
            view.avatarImage.setBackground(null);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finishUploadingFile() {
//        vm.setFaceURL(vm.avatar.getValue());
        Toast.makeText(this, "uploading successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void errorUploadingFile() {
        Toast.makeText(this, "uploading file failure , check your internet connection", Toast.LENGTH_SHORT).show();
    }
}
