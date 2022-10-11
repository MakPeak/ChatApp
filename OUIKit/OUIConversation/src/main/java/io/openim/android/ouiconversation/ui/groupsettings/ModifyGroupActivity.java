package io.openim.android.ouiconversation.ui.groupsettings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import io.openim.android.ouiconversation.R;

import io.openim.android.ouiconversation.databinding.ActivityModifyGroupBinding;
import io.openim.android.ouiconversation.vm.GroupChatSettingsVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouigroup.ui.CreateGroupActivity;

public class ModifyGroupActivity extends BaseActivity<GroupChatSettingsVM, ActivityModifyGroupBinding> implements GroupChatSettingsVM.ViewActionPicture{

    String groupID="",groupName="", groupDesc="";
    Boolean isLoggedInUserAnOwner=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(GroupChatSettingsVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityModifyGroupBinding.inflate(getLayoutInflater()));

        groupID = getIntent().getExtras().getString("ChatID");
        groupName=getIntent().getExtras().getString("groupName");
        groupDesc=getIntent().getExtras().getString("groupDescription");
        isLoggedInUserAnOwner=getIntent().getExtras().getBoolean("isLoggedInUserAnOwner");

        view.groupName.setText(groupName);
        view.groupDescription.setText(groupDesc);
        if(!isLoggedInUserAnOwner){
            view.groupName.setEnabled(false);
            view.groupDescription.setEnabled(false);
            view.groupName.setClickable(false);
            view.groupDescription.setClickable(false);
            view.btnSubmit.setVisibility(View.GONE);
        }
        init();
    }

    private void init(){

        vm.modifyGroupInfoStatusMsg.observe(this,v->{
            toast(getString(io.openim.android.ouicore.R.string.group_information_updated));
            finish();
        });

        view.btnSubmit.setOnClickListener(v ->
            vm.modifyGroupInfo(groupID, view.groupName.getText().toString(), vm.faceURL.getValue(),"",
                view.groupDescription.getText().toString(),"" ));

        view.backBtn.setOnClickListener(v->finish());

        view.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ModifyGroupActivity.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
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
        Toast.makeText(this, "uploading successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void errorUploadingFile() {
        Toast.makeText(this, "uploading file failure , check your internet connection", Toast.LENGTH_SHORT).show();
    }
}
