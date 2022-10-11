package io.openim.android.ouiconversation.ui;

import static android.os.Build.VERSION_CODES.R;

import static io.openim.android.ouicore.utils.Constant.ID;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.ActivityPrivateChatSettingsBinding;
import io.openim.android.ouiconversation.ui.groupsettings.GroupMemberDetailsActivity;
import io.openim.android.ouiconversation.ui.groupsettings.ViewChatHistoryActivity;
import io.openim.android.ouiconversation.ui.groupsettings.ViewGroupMembersActivity;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouiconversation.vm.PrivateChatSettingsVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.sdk.models.SearchResult;

public class PrivateChatSettingsActivity extends BaseActivity<PrivateChatSettingsVM,ActivityPrivateChatSettingsBinding> implements PrivateChatSettingsVM.ViewAction{
    ActivityPrivateChatSettingsBinding binding ;
    boolean chatIsPined ,isDND ;
    String chatID ;
    String nickName, joinTime ="", faceURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindVM(PrivateChatSettingsVM.class, true);
        bindViewDataBinding(ActivityPrivateChatSettingsBinding.inflate(getLayoutInflater()));
        binding = view ;
        init();
        listener();
    }

    public void init(){
        chatID = this.getIntent().getExtras().getString("chatID"); //conversation id
        chatIsPined = this.getIntent().getExtras().getBoolean("isPined");

        vm.userID = chatID.substring(7,chatID.length()) ;
        vm.chatID.setValue(chatID);
        vm.searchPerson();
        vm.getDND();
        vm.searchOneConversation(vm.userID,1);

        binding.nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity( new Intent(PrivateChatSettingsActivity.this, Class.forName("io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity"))
                        .putExtra(ID,vm.userID));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
//                startActivity(new Intent(PrivateChatSettingsActivity.this ,
//                    GroupMemberDetailsActivity.class)
//                    .putExtra("group_nick_name", nickName).putExtra("join_time", joinTime)
//                    .putExtra("member_user_id", vm.userID).putExtra("face_url",faceURL));
            }
        });

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PrivateChatSettingsActivity.this , ViewChatHistoryActivity.class).putExtra("ChatID",chatID));
            }
        });
        binding.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrivateChatSettingsActivity.this , ViewChatHistoryActivity.class).putExtra("ChatID",chatID) ;
                intent.putExtra("Tap Selected",1);
                startActivity(intent);
            }
        });
        binding.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrivateChatSettingsActivity.this , ViewChatHistoryActivity.class).putExtra("ChatID",chatID) ;
                intent.putExtra("Tap Selected",2);
                startActivity(intent);
            }
        });
        binding.file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrivateChatSettingsActivity.this , ViewChatHistoryActivity.class).putExtra("ChatID",chatID) ;
                intent.putExtra("Tap Selected",3);
                startActivity(intent);
            }
        });



        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setCheckboxsListners();


        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearChatDialog clearChatDialog = new ClearChatDialog(PrivateChatSettingsActivity.this,vm,getString(io.openim.android.ouicore.R.string.clear_chat_history_question),getString(io.openim.android.ouicore.R.string.clear),1);
                clearChatDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                clearChatDialog.show();
            }
        });
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearChatDialog clearChatDialog = new ClearChatDialog(PrivateChatSettingsActivity.this,vm,getString(io.openim.android.ouicore.R.string.delete_friend_question),getString(io.openim.android.ouicore.R.string.delete),2);
                clearChatDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                clearChatDialog.show();
            }
        });

    }

    private void setCheckboxsListners() {
        binding.pin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                vm.changePin(b);
            }
        });
        binding.noDisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int status = 0 ;
                if(b)
                    status = 2 ;

                vm.DND(status);
            }
        });
        binding.addBlackList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                System.out.println("compoundButton : " + b);
                if(!b){
                    vm.removeBlacklist();
                }else{
                    vm.addBlacklist();
                }
            }
        });
        binding.burnConversation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                vm.burnChat(chatID, b);
            }
        });
    }

    public void listener() {
        vm.conversationInfoResponse.observe(this , data->{
            if(data == null)return;
            binding.pin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                }
            });
            binding.burnConversation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                }
            });
            binding.pin.setChecked(data.isPinned());
            binding.pin.jumpDrawablesToCurrentState();

            binding.burnConversation.setChecked(data.isPrivateChat());
            binding.burnConversation.jumpDrawablesToCurrentState();

            setCheckboxsListners();
        });
        vm.userInfo.observe(this , data->{
            if(data == null || data.isEmpty())return;

            nickName = data.get(0).getNickname();
            faceURL = data.get(0).getFaceURL();

            binding.nickname.setText(data.get(0).getNickname());
            binding.avatar2.load(data.get(0).getFaceURL());

            binding.addBlackList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // just for updating the default value

                }
            });
            binding.burnConversation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // just for updating the default value
                }
            });

            binding.addBlackList.setChecked(data.get(0).isBlacklist());
            binding.addBlackList.jumpDrawablesToCurrentState();

            if(!data.get(0).isFriendship())
                binding.delete.setVisibility(View.GONE);
            else
                binding.delete.setVisibility(View.VISIBLE);

            setCheckboxsListners();
        });
        vm.DNDResponse.observe(this , data ->{
            if(data == null)
                return;

            binding.noDisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                }
            });
            if(!data.isEmpty())
                binding.noDisturb.setChecked(data.get(0).getResult() == 2);
            else
                binding.noDisturb.setChecked(false);
            binding.noDisturb.jumpDrawablesToCurrentState();
            setCheckboxsListners();

        });
        vm.oneConversationPrivateChat.observe(this, data->{
            System.out.println("Burning" + data);
            if(data==null || data.isEmpty())
                return;
            System.out.println("Burn done "+ChatActivity.isBurn);
            ChatActivity.isBurn = !ChatActivity.isBurn ;
            System.out.println("Burn updated "+ChatActivity.isBurn);

        });
        vm.clearChatHistoryResponse.observe(this , data ->{
            if(data == null)
                return;
            Toast.makeText(this, "Chat history cleared successfully", Toast.LENGTH_SHORT).show();
            vm.isConvCleared=true;
//            Intent intent = null;
//            try {
//                intent = new Intent(PrivateChatSettingsActivity.this, Class.forName("io.bytechat.demo.oldrelease.ui.main.MainActivity"));
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            finishAffinity();
//            startActivity(intent);


        });
        vm.deleteFriend.observe(this , data ->{
            if(data == null)
                return;
            System.out.println("delete friend successfully.");
            Intent intent = null;
            try {
                intent = new Intent(PrivateChatSettingsActivity.this, Class.forName("io.bytechat.demo.oldrelease.ui.main.MainActivity"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            finishAffinity();
            startActivity(intent);
        });
    }


    @Override
    public void onSuccess(SearchResult o) {

    }
}
