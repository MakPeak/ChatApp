package io.openim.android.ouiconversation.ui.groupsettings;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.zxing.EncodeHintType;

import net.glxn.qrgen.android.QRCode;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.ActivityGroupQrcodeBinding;
import io.openim.android.ouiconversation.vm.GroupChatSettingsVM;
import io.openim.android.ouicore.base.BaseActivity;

public class GroupQRCodeActivity extends BaseActivity<GroupChatSettingsVM, ActivityGroupQrcodeBinding> {

    String groupID;
    String groupName, groupDesc, groupURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(GroupChatSettingsVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityGroupQrcodeBinding.inflate(getLayoutInflater()));

        groupID = getIntent().getExtras().getString("ChatID");
        groupName = getIntent().getExtras().getString("groupName");
        groupDesc = getIntent().getExtras().getString("groupDesc");
        groupURL = getIntent().getExtras().getString("groupURL");

        init();
    }

    private void init(){

        if(groupID.isEmpty())return;
        Bitmap myBitmap = QRCode.from("io.openim.app/joinGroup/"+groupID).withHint(EncodeHintType.MARGIN, 0).bitmap();

        //Set Group UI elements
        view.profileImg.load(groupURL,true);

        if(groupName.length()>12)
            view.groupName.setText(groupName.substring(0,12)+"...");
        else
            view.groupName.setText(groupName);

        view.groupDescription.setText(groupDesc);
        view.qrCodeImage.setImageBitmap(myBitmap);

        // Onclicklisteners
        view.ivBack.setOnClickListener(v->finish());
    }
}
