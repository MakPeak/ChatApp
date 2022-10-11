package io.openim.android.ouiconversation.ui.groupsettings;

import static io.openim.android.ouiconversation.utils.Constant.GROUP_ANNOUNCEMENT;
import static io.openim.android.ouiconversation.utils.Constant.GROUP_ANNOUNCEMENT_TIME;
import static io.openim.android.ouiconversation.utils.Constant.GROUP_ID;
import static io.openim.android.ouiconversation.utils.Constant.GROUP_NAME;
import static io.openim.android.ouiconversation.utils.Constant.OWNER_ID;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.ActivityGroupAnnouncementBinding;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouiconversation.vm.GroupChatSettingsVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.entity.LoginCertificate;

public class GroupAnnouncementActivity extends BaseActivity<GroupChatSettingsVM, ActivityGroupAnnouncementBinding> {

    String groupID;
    String ownerID;
    String groupName;
    String groupAnnouncement;
    String groupAnnouncementTime;
    String loggedInUserId = LoginCertificate.getCache(this).userID;
    Boolean isLoggedInUserAnOwner=false;
    String groupFaceUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(GroupChatSettingsVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityGroupAnnouncementBinding.inflate(getLayoutInflater()));

        groupID = getIntent().getExtras().getString(Constant.GROUP_ID);
        ownerID= getIntent().getExtras().getString(Constant.OWNER_ID);
        groupName = getIntent().getExtras().getString(Constant.GROUP_NAME);
        groupAnnouncement=getIntent().getExtras().getString(Constant.GROUP_ANNOUNCEMENT);
        groupAnnouncementTime=getIntent().getExtras().getString(Constant.GROUP_ANNOUNCEMENT_TIME);
        groupFaceUrl=getIntent().getExtras().getString(Constant.GROUP_FACE_URL);

        if(loggedInUserId.equalsIgnoreCase(ownerID))
            isLoggedInUserAnOwner=true;
        else
            isLoggedInUserAnOwner=false;

        if(isLoggedInUserAnOwner) {
            view.groupOwnerAnnouncementLayout.setVisibility(View.VISIBLE);
            view.groupMemberAnnouncementLayout.setVisibility(View.GONE);
            view.noticeLayout.setVisibility(View.GONE);
        }else{
            view.groupOwnerAnnouncementLayout.setVisibility(View.GONE);
            view.groupMemberAnnouncementLayout.setVisibility(View.VISIBLE);
            view.noticeLayout.setVisibility(View.VISIBLE);
        }

        view.profileImg.load(groupFaceUrl,true);
        view.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = 0 ;
                for(int i =0  ;i < view.groupAnnouncementOwner.getText().toString().length() ;i++)
                {
                    if(Character.isWhitespace(view.groupAnnouncementOwner.getText().toString().charAt(i)) || view.groupAnnouncementOwner.getText().toString().charAt(i)=='/'){
                        cnt++;
                        continue;
                    }
                }
                if(cnt == view.groupAnnouncementOwner.getText().toString().length()){
                    Toast.makeText(GroupAnnouncementActivity.this, "Blank announcement not allowed", Toast.LENGTH_SHORT).show();
                    return;
                }

                vm.modifyGroupInfo(groupID,"", "",view.groupAnnouncementOwner.getText().toString(),
                        "","" );

            }
        });

        vm.modifyGroupInfoStatusMsg.observe(this, data->{
            if(data==null)
                return;
            else{
                toast(getString(io.openim.android.ouicore.R.string.group_announcement_done));
                finish();
            }
        });

        // Owner Announcement screen
        view.groupAnnouncementOwner.setText(""+groupAnnouncement);
        //Members Announcement screen
        view.groupName.setText(groupName);
        view.groupDescription.setText(groupAnnouncementTime);
        view.tvGroupMemberAnnouncement.setText(groupAnnouncement);

        view.backBtn.setOnClickListener(v->finish());

    }
}
