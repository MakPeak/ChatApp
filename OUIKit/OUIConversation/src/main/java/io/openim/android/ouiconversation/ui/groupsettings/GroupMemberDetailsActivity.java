package io.openim.android.ouiconversation.ui.groupsettings;

import static io.openim.android.ouicore.utils.Constant.ID;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.List;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.ActivityGroupMemberDetailsBinding;
import io.openim.android.ouiconversation.ui.PreviewActivity;
import io.openim.android.ouiconversation.vm.GroupChatSettingsVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.net.RXRetrofit.N;
import io.openim.android.ouicore.net.RXRetrofit.NetObserver;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.utils.SinkHelper;
import io.openim.android.ouigroup.service.OpenIMService;
import io.openim.android.ouigroup.service.RequestOnlineStatus;
import io.openim.android.ouigroup.service.ResponseOnlineStatus;
import io.openim.android.sdk.models.FriendshipInfo;
import io.openim.android.sdk.models.UserInfo;

public class GroupMemberDetailsActivity extends BaseActivity<GroupChatSettingsVM, ActivityGroupMemberDetailsBinding> {

    private long clickedIDTime=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(GroupChatSettingsVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityGroupMemberDetailsBinding.inflate(getLayoutInflater()));

        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());

        view.groupNickname.setText(getIntent().getStringExtra("group_nick_name"));
        if(getIntent().getStringExtra("join_time").equalsIgnoreCase("")) {
            view.groupLayout.setVisibility(View.GONE);
            view.joinTimeLayout.setVisibility(View.GONE);
        }else{
            view.groupLayout.setVisibility(View.VISIBLE);
            view.joinTimeLayout.setVisibility(View.VISIBLE);
            view.joinTime.setText(getIntent().getStringExtra("join_time"));
        }
        view.avatar.load(getIntent().getStringExtra("face_url"));

        view.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupMemberDetailsActivity.this.startActivity(
                    new Intent(GroupMemberDetailsActivity.this, PreviewActivity.class).putExtra(PreviewActivity.MEDIA_URL,
                        getIntent().getStringExtra("face_url") ));
            }
        });

        listener();
        vm.searchContent = getIntent().getStringExtra("member_user_id");
        vm.searchPerson();
        getOnlineStatus(getIntent().getStringExtra("member_user_id"));

        click();
        if(LoginCertificate.getCache(this).userID.equals(vm.searchContent)){
            view.addFriend.setVisibility(View.GONE);
            view.sendMsg.setVisibility(View.GONE);
        }
    }

    private void click() {
        view.sendMsg.setOnClickListener(v -> ARouter.getInstance().build(Routes.Conversation.CHAT)
            .withString(ID, vm.searchContent)
            .withString(io.openim.android.ouicore.utils.Constant.K_NAME, vm.userInfo.getValue().get(0).getNickname())
            .navigation());


        view.addFriend.setOnClickListener(v -> {
            Intent intent = null;
            try {
                intent = new Intent(GroupMemberDetailsActivity.this,
                    Class.forName("io.bytechat.demo.oldrelease.ui.search.SendVerifyActivity"));
                intent.putExtra(ID, vm.searchContent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            startActivity(intent);
            //startActivity(new Intent(this, SendVerifyActivity.class).putExtra(ID, vm.searchContent));
        });
        view.userId.setOnClickListener(v -> {
            long clickTimeRes = System.currentTimeMillis()- clickedIDTime ;
            clickTimeRes = clickTimeRes/1000;
            if( clickTimeRes <= 1 && clickedIDTime != -1){
                return;
            }
            clickedIDTime = System.currentTimeMillis();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(view.userId.getText(), view.userId.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(GroupMemberDetailsActivity.this, io.openim.android.ouicore.R.string.id_copied, Toast.LENGTH_SHORT).show();
        });

        view.backBtn.setOnClickListener(v -> GroupMemberDetailsActivity.this.finish());
    }

    private void listener() {
        vm.userInfo.observe(this, v -> {
            if (null != v && !v.isEmpty()) {
                vm.checkFriend(v);
                UserInfo userInfo = v.get(0);
                String name = userInfo.getNickname() ;
                if(name.length()>12)
                    name = name.substring(0,12)+"...";
                view.nickName.setText(name);



                System.out.println("user idddd : "+userInfo.getUserID() );
                view.userId.setText(userInfo.getUserID());

//                if(userInfo.getFaceURL()!=null)
//                    view.avatar.load(userInfo.getFaceURL());
                System.out.println(" " + userInfo.getGender());
                if(userInfo.getGender()==2){
                    view.gender.setImageResource(R.mipmap.icon_male);
                }else{
                    view.gender.setImageResource(R.mipmap.icon_female);
                }
            }
        });
        vm.friendshipInfo.observe(this, v -> {
            if (null != v && !v.isEmpty()) {
                FriendshipInfo friendshipInfo = v.get(0);
                if (friendshipInfo.getResult() == 1) {
                    view.addFriend.setVisibility(View.GONE);
                    view.part.setVisibility(View.VISIBLE);
                } else {
                    view.addFriend.setVisibility(View.VISIBLE);
                    view.part.setVisibility(View.GONE);
                }
            }
            if(LoginCertificate.getCache(this).userID.equals(vm.searchContent)){
                view.addFriend.setVisibility(View.GONE);
                view.sendMsg.setVisibility(View.GONE);
            }
        });
    }

    private void getOnlineStatus(String ID){
        List<String> idList = new ArrayList<>();
        idList.add(ID);
        RequestOnlineStatus requestOnlineStatus = new RequestOnlineStatus("abc123", idList);
        N.API(OpenIMService.class).onlineStatus(LoginCertificate.getCache(this).token, requestOnlineStatus)
            .compose(N.IOMain())
            .subscribe(new NetObserver<ResponseOnlineStatus>(this) {

                @Override
                public void onComplete() {
                    super.onComplete();
                }

                @Override
                public void onSuccess(ResponseOnlineStatus responseOnlineStatus) {
                    System.out.println("ONLINE STATUS API: " + responseOnlineStatus.toString());
                    StringBuilder platform = new StringBuilder();
                    if(responseOnlineStatus.getData().get(0).getStatus().equalsIgnoreCase("online")) {
                        for ( int i = 0; i < responseOnlineStatus.getData().get(0).getDetailPlatformStatus().size(); i++) {
                            if(responseOnlineStatus.getData().get(0).getDetailPlatformStatus().size() == 1) {
                                platform.append(responseOnlineStatus.getData().get(0).getDetailPlatformStatus().get(i).getPlatform());
                            } else {
                                platform.append(responseOnlineStatus.getData().get(0).getDetailPlatformStatus().get(i).getPlatform()).append("/");
                            }
                        }
                        if(responseOnlineStatus.getData().get(0).getDetailPlatformStatus().size() > 1) {
                            platform.deleteCharAt(platform.length() - 1);
                        }
                        view.tvOnline.setText(platform + " " + responseOnlineStatus.getData().get(0).getDetailPlatformStatus().get(0).getStatus());
                    } else {
                        view.tvOnline.setText(responseOnlineStatus.getData().get(0).getStatus());
                        view.isOnlineImg.setImageResource(io.openim.android.ouicore.R.mipmap.offline);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }
            });
    }

}
