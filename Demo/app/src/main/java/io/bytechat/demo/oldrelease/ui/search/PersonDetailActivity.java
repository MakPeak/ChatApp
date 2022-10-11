package io.bytechat.demo.oldrelease.ui.search;

import static io.openim.android.ouicore.utils.Constant.ID;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;


import java.util.ArrayList;
import java.util.List;

import io.bytechat.demo.R;
import io.bytechat.demo.model.DiscoverResponse;
import io.bytechat.demo.model.RequestOnlineStatus;
import io.bytechat.demo.model.ResponseOnlineStatus;
import io.bytechat.demo.oldrelease.repository.OpenIMService;
import io.bytechat.demo.oldrelease.vm.SearchVM;
import io.bytechat.demo.databinding.ActivityPersonDetailBinding;
import io.bytechat.demo.ui.profile.GenderDialog;
import io.bytechat.demo.ui.widget.SetRemarkDialog;
import io.openim.android.ouiconversation.ui.PreviewActivity;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.databinding.ViewBackBinding;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.net.RXRetrofit.N;
import io.openim.android.ouicore.net.RXRetrofit.NetObserver;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.utils.SinkHelper;
import io.openim.android.sdk.models.FriendshipInfo;
import io.openim.android.sdk.models.UserInfo;

public class PersonDetailActivity extends BaseActivity<SearchVM, ActivityPersonDetailBinding> {

    private long clickedIDTime=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(SearchVM.class);
        bindViewDataBinding(ActivityPersonDetailBinding.inflate(getLayoutInflater()));
        super.onCreate(savedInstanceState);

        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());

        listener();
        vm.searchContent = getIntent().getStringExtra(ID);
        vm.searchPerson();
        getOnlineStatus(getIntent().getStringExtra(ID));

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
            startActivity(new Intent(this, SendVerifyActivity.class).putExtra(ID, vm.searchContent));
        });
        view.id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long clickTimeRes = System.currentTimeMillis()- clickedIDTime ;
                clickTimeRes = clickTimeRes/1000;
                if( clickTimeRes <= 1 && clickedIDTime != -1){
                    return;
                }
                clickedIDTime = System.currentTimeMillis();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(view.userId.getText(), view.userId.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(PersonDetailActivity.this, io.openim.android.ouicore.R.string.id_copied, Toast.LENGTH_SHORT).show();


            }
        });

        view.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonDetailActivity.this.finish();
            }
        });

        view.tvRemarkValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetRemarkDialog setRemarkDialog = new SetRemarkDialog(PersonDetailActivity.this, new SetRemarkDialog.RemarkReturnListener() {
                    @Override
                    public void returnRemark(String remark) {

                        vm.setFriendRemark(vm.searchContent, remark);
                        view.tvRemarkValue.setText(remark);

                    }
                });
                setRemarkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                setRemarkDialog.show();
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

    private void listener() {
        view.idNumberTv.setText(io.openim.android.ouicore.R.string.id_number);
        view.idNumberTv.setText(io.openim.android.ouicore.R.string.send_msg);
        view.addFriend.setText(io.openim.android.ouicore.R.string.add_friend);


        vm.userInfo.observe(this, v -> {
            if (null != v && !v.isEmpty()) {
                vm.checkFriend(v);
                UserInfo userInfo = v.get(0);
                String name = userInfo.getNickname() ;
                if(name.length()>12)
                    name = name.substring(0,12)+"...";
                view.nickName.setText(name);

                if(userInfo.getNickname() != null && !userInfo.getNickname().isEmpty())
                    view.fullNickname.setText(userInfo.getNickname());
                else
                    view.fullNickname.setVisibility(View.GONE);
                view.id.setText(userInfo.getUserID());
                if(userInfo.getFaceURL()!=null)
                    view.avatar.load(userInfo.getFaceURL());
                if(userInfo.getGender()==2){
                    view.gender.setText(io.openim.android.ouicore.R.string.male);
                }else{
                    view.gender.setText(io.openim.android.ouicore.R.string.female);
                }
                view.tvRemarkValue.setText(userInfo.getRemark());
                view.avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.getContext().startActivity(
                            new Intent(view.getContext(), PreviewActivity.class).putExtra(PreviewActivity.MEDIA_URL,v.get(0).getFaceURL() ));
                    }
                });
            }
        });
        vm.friendshipInfo.observe(this, v -> {
            if (null != v && !v.isEmpty()) {
                FriendshipInfo friendshipInfo = v.get(0);
                System.out.println("friendshipInfo.getResult() "+friendshipInfo.getResult());
                if (friendshipInfo.getResult() == 1) {
                    view.addFriend.setVisibility(View.GONE);
//                    view.part.setVisibility(View.VISIBLE);
                } else {
                    view.addFriend.setVisibility(View.VISIBLE);
//                    view.part.setVisibility(View.GONE);
                }
                if(LoginCertificate.getCache(this).userID.equals(vm.searchContent)){
                    view.addFriend.setVisibility(View.GONE);
                    view.sendMsg.setVisibility(View.GONE);
                }
            }

        });
    }

}
