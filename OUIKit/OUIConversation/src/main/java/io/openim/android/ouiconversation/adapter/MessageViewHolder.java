package io.openim.android.ouiconversation.adapter;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.liulishuo.filedownloader.model.FileDownloadStatus.error;
import static io.openim.android.ouiconversation.adapter.MessageAdapter.OWN_ID;
import static io.openim.android.ouicore.utils.Constant.ID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.download.library.DownloadImpl;
import com.google.gson.Gson;
import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.AbsDownloadListener;
import com.ixuea.android.downloader.callback.DownloadManager;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.android.downloader.exception.DownloadException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.GroupNotificationLayoutBinding;
import io.openim.android.ouiconversation.databinding.LayoutLoadingSmallBinding;
import io.openim.android.ouiconversation.databinding.LayoutMsgAudioLeftBinding;
import io.openim.android.ouiconversation.databinding.LayoutMsgAudioRightBinding;
import io.openim.android.ouiconversation.databinding.LayoutMsgCardLeftBinding;
import io.openim.android.ouiconversation.databinding.LayoutMsgCardRightBinding;
import io.openim.android.ouiconversation.databinding.LayoutMsgFileLeftBinding;
import io.openim.android.ouiconversation.databinding.LayoutMsgFileRightBinding;
import io.openim.android.ouiconversation.databinding.LayoutMsgImgLeftBinding;
import io.openim.android.ouiconversation.databinding.LayoutMsgImgRightBinding;
import io.openim.android.ouiconversation.databinding.LayoutMsgTxtLeftBinding;
import io.openim.android.ouiconversation.databinding.LayoutMsgTxtRightBinding;
import io.openim.android.ouiconversation.databinding.LayoutMsgVideoLeftBinding;
import io.openim.android.ouiconversation.databinding.LayoutMsgVideoRightBinding;
import io.openim.android.ouiconversation.databinding.LeftMergeMessageBinding;
import io.openim.android.ouiconversation.databinding.RecallLayoutBinding;
import io.openim.android.ouiconversation.databinding.RightMergeMessageBinding;
import io.openim.android.ouiconversation.entity.UserModel;
import io.openim.android.ouiconversation.ui.PreviewConvAllMediaActivity;
import io.openim.android.ouiconversation.ui.frowardmessage.ChatHistoryMergeMessagesActivity;
import io.openim.android.ouiconversation.ui.groupsettings.GroupAnnouncementActivity;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouiconversation.widget.MessageHoldDialog;
import io.openim.android.ouicore.utils.ByteUtil;
import io.openim.android.ouicore.utils.DownloadMedia;
import io.openim.android.ouicore.utils.FolderHelper;
import io.openim.android.ouicore.utils.TimeUtil;
import io.openim.android.ouicore.voice.SPlayer;
import io.openim.android.ouicore.voice.listener.PlayerListener;
import io.openim.android.ouicore.voice.player.SMediaPlayer;
import io.openim.android.sdk.models.AtElem;
import io.openim.android.sdk.models.AtUserInfo;
import io.openim.android.sdk.models.BusinessCard;
import io.openim.android.sdk.models.Message;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class MessageViewHolder {
    private static final String TAG = "MessageViewHolder";
    public static boolean isChatHistory = false;
    static CountDownTimer timerCount;

    public static RecyclerView.ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType, ChatVM vm) {

        if (viewType == Constant.LOADING)
            return new LoadingView(parent);
        if (viewType == Constant.MsgType.TXT)
            return new TXTView(parent, vm, Constant.MsgType.TXT);

        if (viewType == Constant.MsgType.PICTURE)
            return new IMGView(parent, vm);

        if (viewType == Constant.MsgType.VOICE)
            return new AudioView(parent,vm);
        if (viewType == Constant.MsgType.Date)
            return new GroupAnnouncement(parent, vm, Constant.MsgType.Date);
            //return new DateView(parent, vm);

        if (viewType == Constant.MsgType.CARD)
            return new BusinessCardView(parent,vm);

        if (viewType == Constant.MsgType.VIDEO)
            return new VideoView(parent,vm);
        if (viewType == Constant.MsgType.MERGE)
            return new MergeView(parent, vm);///
        if (viewType == Constant.MsgType.FILE)
            return new FileView(parent,vm);
        if (viewType == Constant.MsgType.REPLY)
            return new TXTView(parent,vm, Constant.MsgType.REPLY);
        if (viewType == Constant.MsgType.REVOKE)
            return new RecallView(parent, vm);///
        //------------ GROUP NOTIFICATIONS
        if(viewType == Constant.MsgType.MEMBER_KICKED_NOTIFICATION)
            return new GroupAnnouncement(parent, vm,Constant.MsgType.MEMBER_KICKED_NOTIFICATION);
        else if(viewType == Constant.MsgType.GROUP_OWNER_TRANSFERRED_NOTIFICATION)
            return new GroupAnnouncement(parent, vm,Constant.MsgType.GROUP_OWNER_TRANSFERRED_NOTIFICATION);
        else if(viewType == Constant.MsgType.GROUP_INFO_SET_NOTIFICATION)
            return new GroupAnnouncement(parent, vm, Constant.MsgType.GROUP_INFO_SET_NOTIFICATION);
        else if(viewType == Constant.MsgType.GROUP_NOTIFICATION)
            return new TXTView(parent, vm, Constant.MsgType.GROUP_NOTIFICATION);
            // return new GroupAnnouncement(parent,vm,Constant.MsgType.GROUP_INFO_SET_NOTIFICATION);
        else if(viewType == Constant.MsgType.MEMBER_INVITED_NOTIFICATION)
            return new GroupAnnouncement(parent,vm,Constant.MsgType.MEMBER_INVITED_NOTIFICATION);
        else if(viewType == Constant.MsgType.MEMBER_ENTER_NOTIFICATION)
            return new GroupAnnouncement(parent,vm,Constant.MsgType.MEMBER_ENTER_NOTIFICATION);
        else if(viewType==Constant.MsgType.GROUP_CREATED_NOTIFICATION)
            return new GroupAnnouncement(parent, vm, Constant.MsgType.GROUP_CREATED_NOTIFICATION);
        else if(viewType==Constant.MsgType.MEMBER_QUIT_NOTIFICATION)
            return new GroupAnnouncement(parent,vm, Constant.MsgType.MEMBER_QUIT_NOTIFICATION);
            //---------PRIVATE CHAT NOTIFICATIONS --------------------
        else if(viewType==Constant.MsgType.BURN_AFTER_READING_NOTIFICATION)
            return new GroupAnnouncement(parent,vm,Constant.MsgType.BURN_AFTER_READING_NOTIFICATION);
        else if(viewType==Constant.MsgType.FRIEND_ADDED_NOTIFICATION)
            return new GroupAnnouncement(parent, vm, Constant.MsgType.FRIEND_ADDED_NOTIFICATION);
        else if(viewType==Constant.MsgType.OA_NOTIFICATION)
            return new TXTView(parent, vm, Constant.MsgType.OA_NOTIFICATION);
        else if(viewType==Constant.MsgType.GROUP_MUTED_NOTIFICATION)
            return new GroupAnnouncement(parent, vm, Constant.MsgType.GROUP_MUTED_NOTIFICATION);
        else if(viewType==Constant.MsgType.GROUP_CANCEL_MUTED_NOTIFICATION)
            return new GroupAnnouncement(parent, vm, Constant.MsgType.GROUP_CANCEL_MUTED_NOTIFICATION);
        else if(viewType==Constant.MsgType.GROUP_MEMBER_MUTED_NOTIFICATION)
            return new GroupAnnouncement(parent, vm, Constant.MsgType.GROUP_MEMBER_MUTED_NOTIFICATION);
        else if(viewType==Constant.MsgType.GROUP_MEMBER_CANCEL_MUTED_NOTIFICATION)
            return new GroupAnnouncement(parent, vm, Constant.MsgType.GROUP_MEMBER_CANCEL_MUTED_NOTIFICATION);
        else if (viewType == Constant.MsgType.MENTION)
            return new MentionView(parent,vm);
        else
            return new TXTView(parent, vm, 1);
    }

//-------------------------GROUP ANNOUNCEMENTS ------------------------------

    public static class GroupAnnouncement extends MessageViewHolder.MsgViewHolder {

        ChatVM vm;
        int msgType = 1;

        public GroupAnnouncement(ViewGroup itemView, ChatVM vm, int msgType) {
            super(itemView);
            this.vm = vm;
            this.msgType = msgType;
        }

        @Override
        int getLeftInflatedId() {
            return R.layout.group_notification_layout;
        }

        @Override
        int getRightInflatedId() {
            return R.layout.group_notification_layout;
        }

        @Override
        void bindLeft(View itemView, Message message, int position) {
            bindRight(itemView, message, position);
        }

        @SuppressLint("SetTextI18n")
        @Override
        void bindRight(View itemView, Message message, int position) {
            //AnnouncementLayoutBinding view = AnnouncementLayoutBinding.bind(itemView);
            GroupNotificationLayoutBinding view=
                GroupNotificationLayoutBinding.bind(itemView);

            Log.d(TAG, "bindRight: msgType ->" + msgType);
            JSONObject messageContentJSONObject = null;
            JSONObject jsonObject = null;
            JSONArray jsonArr = null;
            String nameStr = "";
            String nameStr1Blue = "", nameStr2Blue = "", nameStr1Blue2="", nameStr1Blue3="";
            String nameStr3 = "";
            String userID_ = "";
            String nickname = "";
            List<UserModel> userModelList = null;
            String groupNotificationStr="";

            try {
                messageContentJSONObject = new JSONObject(message.getContent());
                jsonObject = new JSONObject(messageContentJSONObject.getString("jsonDetail"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }

            if (msgType == Constant.MsgType.MEMBER_KICKED_NOTIFICATION) {

                int loopTimes=3;
                try {
                    jsonArr = new JSONArray(jsonObject.getString("kickedUserList"));
                    userModelList = new LinkedList<>();

                    if(jsonArr.length()<3)
                        loopTimes=jsonArr.length();

                    for(int i=0; i<loopTimes;i++){
                        userID_=jsonArr.getJSONObject(i).getString("userID");
                        UserModel userModel = new UserModel();
                        userModel.userID=userID_;
                        userModel.nickname=jsonArr.getJSONObject(i).getString("nickname");
                        if(userID_.equalsIgnoreCase(OWN_ID))
                            userModel.displayName=vm.getContext().getString(io.openim.android.ouicore.R.string.you);
                        else
                            userModel.displayName=trimString(jsonArr.getJSONObject(i).getString("nickname"));

                        groupNotificationStr+=userModel.displayName+" ";
                        userModelList.add(userModel);
                    }
                    groupNotificationStr = groupNotificationStr.substring(0,groupNotificationStr.length()-1);

                    if (jsonArr.length() > 1)
                        nameStr = vm.getContext().getString(io.openim.android.ouicore.R.string.were_moved);
                    else {
                        userID_ = jsonArr.getJSONObject(0).getString("userID");
                        if (nameStr1Blue.contains("You"))
                            nameStr = vm.getContext().getString(io.openim.android.ouicore.R.string.were_moved);
                        else
                            nameStr = vm.getContext().getString(io.openim.android.ouicore.R.string.was_moved);
                    }
                }  catch (JSONException e) {
                    Log.d(TAG, "bindRight: JSONException"+e);
                }
                SpannableString ss = new SpannableString(groupNotificationStr+nameStr);

                for(int i=0;i<loopTimes;i++) {
                    int startIndex=groupNotificationStr.indexOf(userModelList.get(i).displayName);
                    int endIndex=startIndex+userModelList.get(i).displayName.length();
                    Log.d(TAG, "bindRight:removed list start index and end index "+startIndex+ " : "+endIndex);

                    ss.setSpan(getClickableSpan(userModelList.get(i).userID), startIndex,endIndex, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                }
                Log.d("atmention", "bindRight: before setting the value - "+vm.groupMembersUpdated.getValue());
                view.textAnnouncement2.setText(ss);
                view.textAnnouncement2.setMovementMethod(LinkMovementMethod.getInstance());

            } else if (msgType == Constant.MsgType.GROUP_OWNER_TRANSFERRED_NOTIFICATION) {
                try {
                    userID_ = new JSONObject(jsonObject.getString("newGroupOwner")).getString("userID");
                    if (userID_.equalsIgnoreCase(OWN_ID)) {
                        view.textAnnouncement1Blue.setText(vm.getContext().getString(io.openim.android.ouicore.R.string.you));
                        view.textAnnouncement2.setText(vm.getContext().getString(io.openim.android.ouicore.R.string.are_new_owner));
                    } else {
                        nameStr1Blue = trimString(new JSONObject(jsonObject.getString("newGroupOwner")).getString("nickname"));
                        view.textAnnouncement1Blue.setText(nameStr1Blue);
                        nameStr = vm.getContext().getString(io.openim.android.ouicore.R.string.is_new_owner);
                        view.textAnnouncement2.setText(nameStr);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (msgType == Constant.MsgType.GROUP_INFO_SET_NOTIFICATION) {
                nameStr=vm.getContext().getString(io.openim.android.ouicore.R.string.group_details_updated);
                view.textAnnouncement2.setText(nameStr);
            } else if (msgType == Constant.MsgType.MEMBER_INVITED_NOTIFICATION) {
                int loopTimes=3;
                try {
                    jsonArr = new JSONArray(jsonObject.getString("invitedUserList"));
                    userModelList = new LinkedList<>();

                    if(jsonArr.length()<3)
                        loopTimes=jsonArr.length();

                    for(int i=0; i<loopTimes;i++){
                        userID_=jsonArr.getJSONObject(i).getString("userID");
                        UserModel userModel = new UserModel();
                        userModel.userID=userID_;
                        userModel.nickname=jsonArr.getJSONObject(i).getString("nickname");
                        if(userID_.equalsIgnoreCase(OWN_ID))
                            userModel.displayName=vm.getContext().getString(io.openim.android.ouicore.R.string.you);
                        else
                            userModel.displayName=trimString(jsonArr.getJSONObject(i).getString("nickname"));

                        groupNotificationStr+=userModel.displayName+" ";
                        userModelList.add(userModel);
                    }
                    groupNotificationStr = groupNotificationStr.substring(0,groupNotificationStr.length()-1);
                }  catch (JSONException e) {
                    Log.d(TAG, "bindRight: JSONException"+e);
                }
                if(jsonArr.length()>3)
                    nameStr=vm.getContext().getString(io.openim.android.ouicore.R.string.dots_added_to_the_group);
                else
                    nameStr= vm.getContext().getString(io.openim.android.ouicore.R.string.added_to_the_group);

                SpannableString ss = new SpannableString(groupNotificationStr+nameStr);

                for(int i=0;i<loopTimes;i++) {
                    int startIndex=groupNotificationStr.indexOf(userModelList.get(i).displayName);
                    int endIndex=startIndex+userModelList.get(i).displayName.length();
                    Log.d(TAG, "bindRight:invited list start index and end index "+startIndex+ " : "+endIndex);

                    ss.setSpan(getClickableSpan(userModelList.get(i).userID), startIndex,endIndex, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                }
             //   vm.groupMembersUpdated.setValue(true);
               // vm.getGroupMemberList(vm.groupID);
                view.textAnnouncement2.setText(ss);
                view.textAnnouncement2.setMovementMethod(LinkMovementMethod.getInstance());
            }
            else if(msgType==Constant.MsgType.MEMBER_ENTER_NOTIFICATION) {
              nameStr1Blue="";
                try {

                    JSONObject jsonObject1=new JSONObject(new JSONObject(messageContentJSONObject.getString("jsonDetail")).getString("entrantUser"));
                    userID_=jsonObject1.getString("userID");
                    if(userID_.equalsIgnoreCase(OWN_ID)){
                        nameStr1Blue=vm.getContext().getString(io.openim.android.ouicore.R.string.you);
                    }else{
                        nameStr1Blue=jsonObject1.getString("nickname");
                    }
                }  catch (JSONException e) {
                    Log.d(TAG, "bindRight: JSONException"+e);
                }
                view.textAnnouncement1Blue.setText(nameStr1Blue);
                view.textAnnouncement2.setText(vm.getContext().getString(io.openim.android.ouicore.R.string.entered_the_group));
            }
            else if(msgType==Constant.MsgType.GROUP_CREATED_NOTIFICATION) {
                try {
                    userID_=jsonObject.getJSONObject("opUser").getString("userID");
                    if(userID_.equalsIgnoreCase(OWN_ID))
                        nameStr1Blue=vm.getContext().getString(io.openim.android.ouicore.R.string.you);
                    else {
                        nameStr1Blue = trimString(jsonObject.getJSONObject("opUser").getString("nickname"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                nameStr= vm.getContext().getString(io.openim.android.ouicore.R.string.created_this_group);
                view.textAnnouncement1Blue.setText(nameStr1Blue);
                view.textAnnouncement2.setText(nameStr);
            }
            else if(msgType==Constant.MsgType.MEMBER_QUIT_NOTIFICATION){
                try {
                    userID_=jsonObject.getJSONObject("quitUser").getString("userID");
                    if(userID_.equalsIgnoreCase(OWN_ID))
                        nameStr1Blue=vm.getContext().getString(io.openim.android.ouicore.R.string.you);
                    else {
                        nameStr1Blue = trimString(jsonObject.getJSONObject("quitUser").getString("nickname"));
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                nameStr=vm.getContext().getString(io.openim.android.ouicore.R.string.quit_the_group);
                view.textAnnouncement1Blue.setText(nameStr1Blue);
                view.textAnnouncement2.setText(nameStr);
            } else if (msgType == Constant.MsgType.BURN_AFTER_READING_NOTIFICATION) {
                try {
                    String s;
                    JSONObject jsonObject1=new JSONObject(message.getNotificationElem().getDetail());
                     if(jsonObject1.has("isPrivate") && jsonObject1.getString("isPrivate").equalsIgnoreCase("true"))
                     {
                         s= vm.getContext().getString(io.openim.android.ouicore.R.string.burn_after_reading_on);
                     }else{
                         s= vm.getContext().getString(io.openim.android.ouicore.R.string.burn_after_reading_off);
                     }
                    view.textAnnouncement2.setText(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (msgType == Constant.MsgType.FRIEND_ADDED_NOTIFICATION) {
                try {
                    //String s = messageContentJSONObject.getString("defaultTips");
                    String s = vm.getContext().getString(io.openim.android.ouicore.R.string.we_have_become_friends);
                    view.textAnnouncement2.setText(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (msgType == Constant.MsgType.GROUP_MUTED_NOTIFICATION) {
                try {
                    userID_ = new JSONObject(jsonObject.getString("opUser")).getString("userID");
                    if (userID_.equalsIgnoreCase(OWN_ID)) {
                        nameStr1Blue = vm.getContext().getString(io.openim.android.ouicore.R.string.you);
                    } else {
                        nameStr1Blue = vm.getContext().getString(io.openim.android.ouicore.R.string.owner);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                view.textAnnouncement1Blue.setText(nameStr1Blue);
                nameStr = vm.getContext().getString(io.openim.android.ouicore.R.string.turn_on_all_bans);
                view.textAnnouncement2.setText(nameStr);
            } else if (msgType == Constant.MsgType.GROUP_CANCEL_MUTED_NOTIFICATION) {
                try {
                    userID_ = new JSONObject(jsonObject.getString("opUser")).getString("userID");
                    if (userID_.equalsIgnoreCase(OWN_ID)) {
                        //vm.banUpdate.setValue(1);
                        nameStr1Blue = vm.getContext().getString(io.openim.android.ouicore.R.string.you);
                    } else {
                        nameStr1Blue = vm.getContext().getString(io.openim.android.ouicore.R.string.owner);//new JSONObject(jsonObject.getString("opUser")).getString("nickname");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                nameStr = vm.getContext().getString(io.openim.android.ouicore.R.string.turned_off_all_bans);
                view.textAnnouncement1Blue.setText(nameStr1Blue);
                view.textAnnouncement2.setText(nameStr);

            } else if (msgType == Constant.MsgType.GROUP_MEMBER_MUTED_NOTIFICATION) {
                try {
                    JSONObject jsonDetailObj=(new JSONObject(messageContentJSONObject.getString("jsonDetail")));
                    userID_= jsonDetailObj.getJSONObject("mutedUser").getString("userID");
                    String mutedStr="";
                    if(jsonDetailObj.has("mutedSeconds")) {
                        mutedStr=getMutedString(Long.valueOf(jsonDetailObj.getString("mutedSeconds")));
                    }
                    if(userID_.equalsIgnoreCase(OWN_ID)) {
                        nameStr1Blue = vm.getContext().getString(io.openim.android.ouicore.R.string.you);
                        Log.d(TAG, "bindRight:getMutedString(Long.valueOf(seconds) - "+mutedStr);
                        nameStr=vm.getContext().getString(io.openim.android.ouicore.R.string.were_banned);
                       // vm.banUpdate.setValue(Constant.MsgType.GROUP_MEMBER_MUTED_NOTIFICATION);
                    }
                    else {
                        Log.d(TAG, "bindRight:getMutedString(Long.valueOf(seconds) - "+mutedStr);
                        nameStr1Blue = trimString((new JSONObject(messageContentJSONObject.getString("jsonDetail"))).getJSONObject("mutedUser").getString("nickname"));
                        nameStr = vm.getContext().getString(io.openim.android.ouicore.R.string.was_banned);
                    }
                    if(!mutedStr.isEmpty())
                        nameStr=nameStr + " "+vm.getContext().getString(io.openim.android.ouicore.R.string.forr)+" "+mutedStr;
                    view.textAnnouncement1Blue.setText(nameStr1Blue);
                    view.textAnnouncement2.setText(nameStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (msgType == Constant.MsgType.GROUP_MEMBER_CANCEL_MUTED_NOTIFICATION) {
                try {
                    userID_ = (new JSONObject(messageContentJSONObject.getString("jsonDetail"))).getJSONObject("mutedUser").getString("userID");
                    if (userID_.equalsIgnoreCase(OWN_ID)) {
                        nameStr1Blue = vm.getContext().getString(io.openim.android.ouicore.R.string.you);
                        nameStr = vm.getContext().getString(io.openim.android.ouicore.R.string.were_unbanned);
                    } else {
                        nameStr1Blue = trimString((new JSONObject(messageContentJSONObject.getString("jsonDetail"))).getJSONObject("mutedUser").getString("nickname"));
                        nameStr = vm.getContext().getString(io.openim.android.ouicore.R.string.was_unbanned);
                    }
                    view.textAnnouncement1Blue.setText(nameStr1Blue);
                    view.textAnnouncement2.setText(nameStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(msgType == Constant.MsgType.Date){
                view.dateText.setVisibility(View.VISIBLE);
                view.dateText.setText(message.getContent());
            }

            String finalUserID_ = userID_;
            List<UserModel> finalUserModelList = userModelList;
            view.textAnnouncement1Blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalUserModelList == null) {
                        if (!finalUserID_.equalsIgnoreCase("") && !finalUserID_.equalsIgnoreCase(OWN_ID)) {
                            openPersonalDetails(finalUserID_);
                        }
                    } else {
                        Log.d(TAG, "onClick: " + v);
//                        for (int i = 0; i < finalUserModelList.size(); i++) {
//                            openPersonalDetails(finalUserModelList.get(i));
//                        }
                    }
                }
            });

        }
        private String getMutedString(long seconds){
            String mutedString="";
            if(seconds==600)
                mutedString=vm.getContext().getResources().getString(io.openim.android.ouicore.R.string._10_minutes);
            else if(seconds==3600)
                mutedString=vm.getContext().getResources().getString(io.openim.android.ouicore.R.string._1_hour);
            else if(seconds==43200)
                mutedString=vm.getContext().getResources().getString(io.openim.android.ouicore.R.string._12_hours);
            else if(seconds==86400)
                mutedString=vm.getContext().getResources().getString(io.openim.android.ouicore.R.string._1_day);
            else
                mutedString=seconds+vm.getContext().getResources().getString(io.openim.android.ouicore.R.string.sec);
            return mutedString;
        }

    }

    //-------------------------GROUP ANNOUNCEMENTS END------------------------------

    public abstract static class MsgViewHolder extends RecyclerView.ViewHolder {
        protected RecyclerView recyclerView;
        protected MessageAdapter messageAdapter;

        private boolean leftIsInflated = false, rightIsInflated = false;
        private final ViewStub right;
        private final ViewStub left;
        ChatVM vm;
        public MessageHoldDialog dialog;

        public MsgViewHolder(ViewGroup itemView, ChatVM vm) {
            this(itemView);
            this.vm = vm;
            dialog = new MessageHoldDialog(itemView, vm, isChatHistory);
            if (messageAdapter != null)
                dialog.setMessageAdapter(messageAdapter);
        }

        public MsgViewHolder(ViewGroup itemView) {
            super(buildRoot(itemView));
            left = this.itemView.findViewById(R.id.left);
            right = this.itemView.findViewById(R.id.right);

            left.setOnInflateListener((stub, inflated) -> leftIsInflated = true);
            right.setOnInflateListener((stub, inflated) -> rightIsInflated = true);

        }

        public int getImage(String imageName) {
            int drawableResourceId = vm.getContext().getResources().getIdentifier(imageName, "drawable", vm.getContext().getPackageName());
            return drawableResourceId;
        }

        public static View buildRoot(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_msg, parent, false);
        }

        abstract int getLeftInflatedId();

        abstract int getRightInflatedId();

        public void initCheckbox(CheckBox v, int position, Message message) {
            System.out.println("is selecting : " + messageAdapter.isSelecting);
            if (messageAdapter.isSelecting) {
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.GONE);
            }

            if (vm.selectedMessages.getValue().contains(message)) {
                System.out.println("the message is found !" + message.getContent());
                v.setOnCheckedChangeListener(null);
                v.setChecked(true);
            } else {
                v.setOnCheckedChangeListener(null);
                v.setChecked(false);
            }
        }

        public void selectMessageLeft(Message message, boolean b) {
            if (b) {
                List<Message> list = vm.selectedMessages.getValue();
                list.add(message);
                vm.selectedMessages.setValue(list);
            } else {
                List<Message> list = vm.selectedMessages.getValue();
                if (list.size() == 1)
                    list.clear();
                else
                    list.remove(message);
                vm.selectedMessages.setValue(list);
            }
        }

        public void selectMessageRight(Message message, boolean b) {
            System.out.println(" msg selected : " + message.getContent() + " ");
            if (b) {
                List<Message> list = vm.selectedMessages.getValue();
                list.add(message);
                vm.selectedMessages.setValue(list);
            } else {
                List<Message> list = vm.selectedMessages.getValue();
                if (list.size() == 1)
                    list.clear();
                else
                    list.remove(message);
                list.remove(message);
                vm.selectedMessages.setValue(list);
            }
        }

        public String trimString(String str) {
            if (str!=null && str.length() > 12) {
                return str.substring(0, 12) + "...";
            } else
                return str;
        }

        public void openPersonalDetails(String finalUserID_) {
            Intent intent = null;
            try {
                Log.d(TAG, "onClick: userID clicked - " + finalUserID_);
                intent = new Intent(itemView.getContext(), Class.forName("io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity"))
                    .putExtra(ID, finalUserID_);
                itemView.getContext().startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        public ClickableSpan getClickableSpan(String userID) {

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Log.d(TAG, "onClick: clickableSpan ");
                    openPersonalDetails(userID);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(itemView.getContext().getResources().getColor(io.openim.android.ouicore.R.color.light_blue));
                    ds.setUnderlineText(false); // set to false to remove underline
                }
            };

            return clickableSpan;
        }

        abstract void bindLeft(View itemView, Message message, int position);

        abstract void bindRight(View itemView, Message message, int position);

        /**
         * 是否是自己发的消息
         */
        protected boolean isOwn = false;

        //绑定数据
        public void bindData(Message message, int position) {
            if (isOwn = message.getSendID().equals(OWN_ID)) {
                if (leftIsInflated)
                    left.setVisibility(View.GONE);
                if (rightIsInflated)
                    right.setVisibility(View.VISIBLE);
                if (!rightIsInflated) {
                    right.setLayoutResource(getRightInflatedId());
                    right.inflate();
                }
                bindRight(itemView, message, position);
            } else {
                if (leftIsInflated)
                    left.setVisibility(View.VISIBLE);
                if (rightIsInflated)
                    right.setVisibility(View.GONE);
                if (!leftIsInflated) {
                    left.setLayoutResource(getLeftInflatedId());
                    left.inflate();
                }
                bindLeft(itemView, message, position);
            }

        }

        public void bindRecyclerView(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        public void setMessageAdapter(MessageAdapter messageAdapter) {
            this.messageAdapter = messageAdapter;
            if (dialog != null)
                dialog.setMessageAdapter(messageAdapter);
        }
        void toPreview(View view, String messageId) {
            view.setOnClickListener(v -> {
                PreviewConvAllMediaActivity.parentVM = vm;
                view.getContext().startActivity(
                    new Intent(view.getContext(), PreviewConvAllMediaActivity.class)
                        .putExtra(PreviewConvAllMediaActivity.CONVERSATION_ID, vm.conversationID)
                        .putExtra(PreviewConvAllMediaActivity.CLICKED_MESSAGE_ID, messageId));
            });
        }
    }

    //加载中...
    public static class LoadingView extends RecyclerView.ViewHolder {
        public LoadingView(ViewGroup parent) {
            super(LayoutLoadingSmallBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false).getRoot());
        }
    }

    //文本消息
    public static class TXTView extends MessageViewHolder.MsgViewHolder {
        private static int CLICK_THRESHOLD = 100;
        int msgType = 1;

        public TXTView(ViewGroup parent, ChatVM vm, int msgType) {
            super(parent, vm);
            this.msgType = msgType;
        }

        @Override
        int getLeftInflatedId() {
            return R.layout.layout_msg_txt_left;
        }

        @Override
        int getRightInflatedId() {
            return R.layout.layout_msg_txt_right;
        }

        @Override
        void bindLeft(View itemView, Message message, int position) {
            LayoutMsgTxtLeftBinding v = LayoutMsgTxtLeftBinding.bind(itemView);
            String replyMsg, msg = "";

            if (msgType == Constant.MsgType.GROUP_NOTIFICATION) {
                // group announcement
                msg = message.getContent();
                v.contentContainer.setVisibility(View.GONE);
                v.tvAnnouncementTime.setText(TimeUtil.getTimeOnly(message.getSendTime()));
                v.announcementLayout.setVisibility(View.VISIBLE);
                v.announcementLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vm.getContext().startActivity(new Intent(vm.getContext(),
                            GroupAnnouncementActivity.class).putExtra(Constant.GROUP_ID, vm.groupID).putExtra(Constant.OWNER_ID, vm.groupsInfo.getValue().get(0).getOwnerUserID())
                            .putExtra(Constant.GROUP_NAME,vm.groupsInfo.getValue().get(0).getGroupName())
                            .putExtra(Constant.GROUP_FACE_URL,vm.groupsInfo.getValue().get(0).getFaceURL())
                            .putExtra(Constant.GROUP_ANNOUNCEMENT_TIME,vm.groupsInfo.getValue().get(0).getNotificationUpdateTime())
                            .putExtra(Constant.GROUP_ANNOUNCEMENT, vm.groupsInfo.getValue().get(0).getNotification()));                    }
                });
                //  new JSONObject(new JSONObject(msg).getString("jsonDetail")).getString("opUser")
                String s = null;
                try {
                    s = new JSONObject(new JSONObject(new JSONObject(msg).getString("jsonDetail")).getString("opUser")).getString("faceURL");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                v.avatar.load(s);

                try {
                    JSONObject json = new JSONObject(message.getContent());
                    JSONObject jsonObject = new JSONObject(json.getString("jsonDetail"));
                    if (jsonObject.getJSONObject("group").has("notification")) {
                        msg = jsonObject.getJSONObject("group").getString("notification");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                v.tvAnnouncement.setText(msg);
            } else if (msgType == Constant.MsgType.OA_NOTIFICATION) {

                // system notification
                try {
                    JSONObject json = new JSONObject(message.getContent());
                    JSONObject jsonObject = new JSONObject(json.getString("jsonDetail"));
                    msg = jsonObject.getString("text");
                    v.tvAnnouncement.setText(msg);
                    v.avatar.setVisibility(View.INVISIBLE);
                    v.avatarBell.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                initCheckbox(v.selectLayout.select, position, message);
                // handle reply message
                if (message.getQuoteElem().getQuoteMessage() != null) {
                    replyMsg = message.getQuoteElem().getQuoteMessage().getContent();
                    msg = message.getQuoteElem().getText();
                    String replyContent ="";
                    switch (message.getQuoteElem().getQuoteMessage().getContentType()){
                        case Constant.MsgType.TXT : replyContent = message.getQuoteElem().getQuoteMessage().getContent() ; break;
                        case Constant.MsgType.PICTURE: replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_picture_message);break;
                        case Constant.MsgType.VIDEO: replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_video_message); break;
                        case Constant.MsgType.FILE: replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_file_message);break;
                        case Constant.MsgType.VOICE: replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_voice_message);break;
                        case Constant.MsgType.MERGE: replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_merge_message);break;
                        case Constant.MsgType.CARD: replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_name_card);break;

                    }
                    replyMsg = replyContent;
                    if (replyMsg.length() >= 30)
                        replyMsg = replyMsg.substring(0, 30) + "...";
                    v.llReplyText.setVisibility(View.VISIBLE);
                    if(message.getQuoteElem().getQuoteMessage().getContentType() == Constant.MsgType.PICTURE) {
                        v.ivImage.setVisibility(View.VISIBLE);
                        v.flImage.setVisibility(View.VISIBLE);

                        v.lottieViewVoice.setVisibility(View.GONE);
                        v.duration2.setVisibility(View.GONE);
                        v.ivVideoPlayButton.setVisibility(View.GONE);
                        JSONObject jsonObjContent = null;
                        JSONObject jsonObjQuoteMessage = null;
                        JSONObject jsonObjQuoteMessageContent = null;
                        try {
                            jsonObjContent = new JSONObject(message.getContent());
                            jsonObjQuoteMessage = new JSONObject(String.valueOf(jsonObjContent.get("quoteMessage")));
                            jsonObjQuoteMessageContent = new JSONObject(String.valueOf(jsonObjQuoteMessage.get("content")));
//                            replyMsg = jsonObjQuoteMessageContent.getJSONObject("sourcePicture").get("uuid").toString();
                            replyMsg = "";
                            Glide.with(vm.getContext())
                                .load(jsonObjQuoteMessageContent.getJSONObject("sourcePicture").get("url"))
                                .centerCrop()
                                .into(v.ivImage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if(message.getQuoteElem().getQuoteMessage().getContentType() == Constant.MsgType.VIDEO) {
                        v.ivImage.setVisibility(View.VISIBLE);
                        v.flImage.setVisibility(View.VISIBLE);
                        v.ivVideoPlayButton.setVisibility(View.VISIBLE);
                        v.lottieViewVoice.setVisibility(View.GONE);
                        v.duration2.setVisibility(View.GONE);
                        JSONObject jsonObjContent = null;
                        try {
                            jsonObjContent = new JSONObject(message.getQuoteElem().getQuoteMessage().getContent());
//                            replyMsg = jsonObjContent.get("videoUUID").toString();
                            replyMsg = "";
                            Glide.with(vm.getContext())
                                .load(String.valueOf(jsonObjContent.get("snapshotUrl")))
                                .centerCrop()
                                .into(v.ivImage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if(message.getQuoteElem().getQuoteMessage().getContentType() == Constant.MsgType.VOICE) { //handling reply to voice
                        v.ivImage.setVisibility(View.GONE);
                        v.flImage.setVisibility(View.VISIBLE);
                        v.ivVideoPlayButton.setVisibility(View.GONE);
                        v.lottieViewVoice.setVisibility(View.VISIBLE);
                        v.duration2.setVisibility(View.VISIBLE);

                        JSONObject jsonObjContent = null;
                        try {
                            jsonObjContent = new JSONObject(message.getQuoteElem().getQuoteMessage().getContent());
//                            replyMsg = jsonObjContent.get("videoUUID").toString();
                            v.duration2.setText(jsonObjContent.getString("duration"));
                            replyMsg = "";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(message.getQuoteElem().getQuoteMessage().getContentType() == Constant.MsgType.FILE) {
                        v.ivImage.setVisibility(View.VISIBLE);
                        replyMsg = message.getQuoteElem().getQuoteMessage().getFileElem().getFileName();
                        String fileName = "";
                        try {
                            fileName = java.net.URLDecoder.decode(message.getQuoteElem().getQuoteMessage().getFileElem().getFileName(), StandardCharsets.UTF_8.name());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        int index = fileName.lastIndexOf('.');
                        if(index > 0) {
                            String extension = fileName.substring(index + 1);
                            System.out.println(fileName + "\t" + extension);

                            switch(extension) {
                                case "pdf":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_ftp"))
                                        .into(v.ivImage);
                                    break;
                                case "txt":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_txt"))
                                        .into(v.ivImage);
                                    break;
                                case "ppt":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_ppt"))
                                        .into(v.ivImage);
                                    break;
                                case "jpg":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_image"))
                                        .into(v.ivImage);
                                    break;
                                case "jpeg":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_image"))
                                        .into(v.ivImage);
                                    break;
                                case "xls":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_excel"))
                                        .into(v.ivImage);
                                    break;
                                case "xlsx":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_excel"))
                                        .into(v.ivImage);
                                    break;
                                case "mp4":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_video"))
                                        .into(v.ivImage);
                                    break;
                                case "mp3":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_music"))
                                        .into(v.ivImage);
                                    break;
                                case "zip":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_zip"))
                                        .into(v.ivImage);
                                    break;
                                case "rar":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_zip"))
                                        .into(v.ivImage);
                                    break;
                                case "docx":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_word"))
                                        .centerCrop()
                                        .into(v.ivImage);
                                    break;
                                default:
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_other"))
                                        .centerCrop()
                                        .into(v.ivImage);
                            }
                        }

                    } else {
                        v.ivImage.setVisibility(View.GONE);
                    }
                    v.replyText.setText(message.getQuoteElem().getQuoteMessage().getSenderNickname() + " " + replyMsg);
                } else {
                    msg = message.getContent();
                }

            }
            v.sendState.setSendState(message.getStatus());
            if (message.getSenderFaceUrl() != null)
                v.avatar.load(message.getSenderFaceUrl());
            v.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    try {
                        intent = new Intent(itemView.getContext(), Class.forName("io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity"))
                            .putExtra(ID, message.getSendID());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    itemView.getContext().startActivity(intent);
                }
            });
            v.content.setText(msg);
            v.tvTime.setText(TimeUtil.getTimeOnly(message.getSendTime()));
            v.selectLayout.select.setOnCheckedChangeListener((compoundButton, b) -> selectMessageLeft(message,b));

            final boolean[] test = {false};
            Message finalMessage = message;
            v.contentContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.showDialog(finalMessage, Constant.MsgType.TXT, itemView);
                    return true;
                }
            });
            if (message.getSessionType() == io.openim.android.ouicore.utils.Constant.SessionType.GROUP_CHAT) {
                v.nickName.setVisibility(View.VISIBLE);
                String nickname = message.getSenderNickname();
                if (nickname != null && nickname.length() > 12)
                    nickname = nickname.substring(0, 12) + "...";
                v.nickName.setText(nickname);
            } else
                v.nickName.setVisibility(View.GONE);

//            if (message.getAttachedInfoElem().isPrivateChat()) {
//                v.timer.setVisibility(View.VISIBLE);
//                new CountDownTimer(30000, 1000) {
//                    public void onTick(long millisUntilFinished) {
//                        v.timer.setText(millisUntilFinished / 1000 + " secs" );
//                    }
//
//                    public void onFinish() {
////                        v.timer.setText("done!");
//                        vm.deleteMessage(message);
//                        v.timer.setVisibility(View.GONE);
//                    }
//                }.start();
//            } else {
//                v.timer.setVisibility(View.GONE);
//            }

        }

        @Override
        void bindRight(View itemView, Message message, int position) {
            LayoutMsgTxtRightBinding v = LayoutMsgTxtRightBinding.bind(itemView);
            String replyMsg = null, msg = "";

            if (msgType == Constant.MsgType.GROUP_NOTIFICATION) {
                msg = message.getContent();
                v.contentLayout.setVisibility(View.GONE);
                v.tvAnnouncementTime2.setText(TimeUtil.getTimeOnly(message.getSendTime()));
                v.announcementLayout2.setVisibility(View.VISIBLE);
                v.announcementLayout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vm.getContext().startActivity(new Intent(vm.getContext(),
                            GroupAnnouncementActivity.class).putExtra(Constant.GROUP_ID, vm.groupID).putExtra(Constant.OWNER_ID, vm.groupsInfo.getValue().get(0).getOwnerUserID())
                            .putExtra(Constant.GROUP_NAME,vm.groupsInfo.getValue().get(0).getGroupName())
                            .putExtra(Constant.GROUP_FACE_URL,vm.groupsInfo.getValue().get(0).getFaceURL())
                            .putExtra(Constant.GROUP_ANNOUNCEMENT_TIME,vm.groupsInfo.getValue().get(0).getNotificationUpdateTime())
                            .putExtra(Constant.GROUP_ANNOUNCEMENT, vm.groupsInfo.getValue().get(0).getNotification()));
                    }
                });
                String s = null;
                try {
                    s = new JSONObject(new JSONObject(new JSONObject(msg).getString("jsonDetail")).getString("opUser")).getString("faceURL");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                v.avatar2.load(s);
                try {
                    JSONObject json = new JSONObject(message.getContent());
                    JSONObject jsonObject = new JSONObject(json.getString("jsonDetail"));
                    if (jsonObject.getJSONObject("group").has("notification")) {
                        msg = jsonObject.getJSONObject("group").getString("notification");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                v.tvAnnouncement2.setText(msg);
            } else if (msgType == Constant.MsgType.OA_NOTIFICATION) {
                try {
                    JSONObject json = new JSONObject(message.getContent());
                    JSONObject jsonObject = new JSONObject(json.getString("jsonDetail"));
                    msg = jsonObject.getString("text");
                    v.tvAnnouncement2.setText(msg);
                    v.avatar2.setVisibility(View.INVISIBLE);
                    v.avatar2Bell.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                initCheckbox(v.selectLayout.select, position, message);
                // handle reply message
                if (message.getQuoteElem().getQuoteMessage() != null) {
                    String replyContent = "" ;
                    switch (message.getQuoteElem().getQuoteMessage().getContentType()){
                        case Constant.MsgType.TXT : replyContent = message.getQuoteElem().getQuoteMessage().getContent() ; break;
                        case Constant.MsgType.PICTURE: replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_picture_message);break;
                        case Constant.MsgType.VIDEO: replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_video_message); break;
                        case Constant.MsgType.FILE: replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_file_message);break;
                        case Constant.MsgType.VOICE: replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_voice_message);break;
                        case Constant.MsgType.MERGE: replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_merge_message);break;
                        case Constant.MsgType.CARD: replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_name_card);break;

                    }
                    replyMsg = replyContent;
                    msg = message.getQuoteElem().getText();
                    if (replyMsg.length() >= 30)
                        replyMsg = replyMsg.substring(0, 30) + "...";
                    v.rlReplyText.setVisibility(View.VISIBLE);
                    if(message.getQuoteElem().getQuoteMessage().getContentType() == Constant.MsgType.PICTURE) {
                        v.ivImage.setVisibility(View.VISIBLE);
                        v.flImage.setVisibility(View.VISIBLE);

                        v.lottieViewVoice.setVisibility(View.GONE);
                        v.duration2.setVisibility(View.GONE);
                        v.ivVideoPlayButton.setVisibility(View.GONE);
                        JSONObject jsonObjContent = null;
                        JSONObject jsonObjQuoteMessage = null;
                        JSONObject jsonObjQuoteMessageContent = null;
                        try {
                            jsonObjContent = new JSONObject(message.getContent());
                            jsonObjQuoteMessage = new JSONObject(String.valueOf(jsonObjContent.get("quoteMessage")));
                            jsonObjQuoteMessageContent = new JSONObject(String.valueOf(jsonObjQuoteMessage.get("content")));
//                            replyMsg = jsonObjQuoteMessageContent.getJSONObject("sourcePicture").get("uuid").toString();
                            replyMsg = "";
                            Glide.with(vm.getContext())
                                .load(jsonObjQuoteMessageContent.getJSONObject("sourcePicture").get("url"))
                                .centerCrop()
                                .into(v.ivImage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if(message.getQuoteElem().getQuoteMessage().getContentType() == Constant.MsgType.VIDEO) {
                        v.ivImage.setVisibility(View.VISIBLE);
                        v.flImage.setVisibility(View.VISIBLE);
                        v.ivVideoPlayButton.setVisibility(View.VISIBLE);
                        v.lottieViewVoice.setVisibility(View.GONE);
                        v.duration2.setVisibility(View.GONE);
                        JSONObject jsonObjContent = null;
                        try {
                            jsonObjContent = new JSONObject(message.getQuoteElem().getQuoteMessage().getContent());
//                            replyMsg = jsonObjContent.get("videoUUID").toString();
                            replyMsg = "";
                            Glide.with(vm.getContext())
                                .load(String.valueOf(jsonObjContent.get("snapshotUrl")))
                                .centerCrop()
                                .into(v.ivImage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(message.getQuoteElem().getQuoteMessage().getContentType() == Constant.MsgType.VOICE) { //handling reply to voice
                        v.ivImage.setVisibility(View.GONE);
                        v.flImage.setVisibility(View.VISIBLE);
                        v.ivVideoPlayButton.setVisibility(View.GONE);
                        v.lottieViewVoice.setVisibility(View.VISIBLE);
                        v.duration2.setVisibility(View.VISIBLE);

                        JSONObject jsonObjContent = null;
                        try {
                            jsonObjContent = new JSONObject(message.getQuoteElem().getQuoteMessage().getContent());
//                            replyMsg = jsonObjContent.get("videoUUID").toString();
                            v.duration2.setText(jsonObjContent.getString("duration"));
                            replyMsg = "";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if(message.getQuoteElem().getQuoteMessage().getContentType() == Constant.MsgType.FILE) {
                        v.ivImage.setVisibility(View.VISIBLE);
                        v.flImage.setVisibility(View.VISIBLE);
                        v.ivVideoPlayButton.setVisibility(View.GONE);
                        v.lottieViewVoice.setVisibility(View.GONE);
                        v.duration2.setVisibility(View.GONE);
                        replyMsg = message.getQuoteElem().getQuoteMessage().getFileElem().getFileName();
                        String fileName = "";
                        try {
                            fileName = java.net.URLDecoder.decode(message.getQuoteElem().getQuoteMessage().getFileElem().getFileName(), StandardCharsets.UTF_8.name());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        int index = fileName.lastIndexOf('.');
                        if(index > 0) {
                            String extension = fileName.substring(index + 1);
                            System.out.println(fileName + "\t" + extension);

                            switch(extension) {
                                case "pdf":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_ftp"))
                                        .into(v.ivImage);
                                    break;
                                case "txt":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_txt"))
                                        .into(v.ivImage);
                                    break;
                                case "ppt":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_ppt"))
                                        .into(v.ivImage);
                                    break;
                                case "jpg":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_image"))
                                        .into(v.ivImage);
                                    break;
                                case "jpeg":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_image"))
                                        .into(v.ivImage);
                                    break;
                                case "xls":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_excel"))
                                        .into(v.ivImage);
                                    break;
                                case "xlsx":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_excel"))
                                        .into(v.ivImage);
                                    break;
                                case "mp4":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_video"))
                                        .into(v.ivImage);
                                    break;
                                case "mp3":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_music"))
                                        .into(v.ivImage);
                                    break;
                                case "zip":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_zip"))
                                        .into(v.ivImage);
                                    break;
                                case "rar":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_zip"))
                                        .into(v.ivImage);
                                    break;
                                case "docx":
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_word"))
                                        .centerCrop()
                                        .into(v.ivImage);
                                    break;
                                default:
                                    Glide.with(vm.getContext())
                                        .load(getImage("ic_file_other"))
                                        .centerCrop()
                                        .into(v.ivImage);
                            }
                        }

                    } else {
                        v.ivImage.setVisibility(View.GONE);
                        v.flImage.setVisibility(View.GONE);
                    }
                    v.replyText.setText(message.getQuoteElem().getQuoteMessage().getSenderNickname()+" : "+replyMsg);
                } else {
                    msg = message.getContent();
                }

            }
            System.out.println("the message : " + message.getContent() + " => status is " + message.getStatus());
            v.sendState.setSendState(message.getStatus());
            if (message.getSenderFaceUrl() != null)
                v.avatar2.load(message.getSenderFaceUrl());
            v.content2.setText(msg);
            v.tvTime2.setText(TimeUtil.getTimeOnly(message.getSendTime()));

            v.selectLayout.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    selectMessageRight(message, b);
                }
            });


            final boolean[] test = {false};
            Message finalMessage = message;
            v.contentContainer2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.showDialog(finalMessage, Constant.MsgType.TXT, itemView);
                    return true;
                }
            });

            if (!vm.isSingleChat) {
                if (message.isRead()) {
                    if (message.getAttachedInfoElem().getGroupHasReadInfo().getGroupMemberCount() > 0) {
                        int readCount = message.getAttachedInfoElem().getGroupHasReadInfo().getGroupMemberCount() -
                            message.getAttachedInfoElem().getGroupHasReadInfo().getHasReadCount() - 1;
                        v.read.setVisibility(View.VISIBLE);
                        if(readCount <= 0) {
                            v.read.setText(io.openim.android.ouicore.R.string.read);
                        } else {
                            v.read.setText(readCount + " " + vm.getContext().getResources().getString(io.openim.android.ouicore.R.string.un_read));
                        }
                    }
                } else {
                    if (message.getAttachedInfoElem().getGroupHasReadInfo().getGroupMemberCount() > 0) {
                        int readCount = message.getAttachedInfoElem().getGroupHasReadInfo().getGroupMemberCount() -
                            message.getAttachedInfoElem().getGroupHasReadInfo().getHasReadCount() - 1;
                        v.read.setVisibility(View.VISIBLE);
                        if(readCount <= 0) {
                            v.read.setText(io.openim.android.ouicore.R.string.read);
                        } else {
                            v.read.setText(readCount + " " + vm.getContext().getResources().getString(io.openim.android.ouicore.R.string.un_read));
                        }
                    }
                }
            } else {
                if (message.isRead()) {
                    v.read.setVisibility(View.VISIBLE);
                    v.read.setText(io.openim.android.ouicore.R.string.read);
                } else {
                    v.read.setVisibility(View.VISIBLE);
                    v.read.setText(io.openim.android.ouicore.R.string.un_read);
                }

//                if(message.getStatus() == 2) {
//                    if (message.isRead()) {
//                        if (message.getAttachedInfoElem().isPrivateChat()) {
//                            v.timer.setVisibility(View.VISIBLE);
//                            timerCount = new CountDownTimer(30000, 1000) {
//                                public void onTick(long millisUntilFinished) {
//                                    v.timer.setText("secs " + millisUntilFinished / 1000);
//                                }
//
//                                public void onFinish() {
////                                  v.timer.setText("done!");
//                                    vm.deleteMessage(message);
//                                }
//                            }.start();
//                        } else {
//                            v.timer.setVisibility(View.GONE);
//                        }
//                    }
//                }
            }
        }
    }

    //文本消息
    public static class MentionView extends MessageViewHolder.MsgViewHolder {
        private static int CLICK_THRESHOLD = 100;

        public MentionView(ViewGroup parent, ChatVM vm) {
            super(parent, vm);
        }

        @Override
        int getLeftInflatedId() {
            return R.layout.layout_msg_txt_left;
        }

        @Override
        int getRightInflatedId() {
            return R.layout.layout_msg_txt_right;
        }

        @Override
        void bindLeft(View itemView, Message message, int position) {
            LayoutMsgTxtLeftBinding v = LayoutMsgTxtLeftBinding.bind(itemView);
            String replyMsg, msg = "";

            initCheckbox(v.selectLayout.select, position, message);

            v.selectLayout.select.setOnCheckedChangeListener((buttonView, isChecked) ->
                selectMessageLeft(message,isChecked));

            // handle reply message
            if (message.getQuoteElem().getQuoteMessage() != null) {
                replyMsg = message.getQuoteElem().getQuoteMessage().getContent();
                msg = message.getQuoteElem().getText();
                String replyContent = "";
                switch (message.getQuoteElem().getQuoteMessage().getContentType()) {
                    case Constant.MsgType.TXT:
                        replyContent = message.getQuoteElem().getQuoteMessage().getContent();
                        break;
                    case Constant.MsgType.PICTURE:
                        replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_picture_message);
                        break;
                    case Constant.MsgType.VIDEO:
                        replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_video_message);
                        break;
                    case Constant.MsgType.FILE:
                        replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_file_message);
                        break;
                    case Constant.MsgType.VOICE:
                        replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_voice_message);
                        break;
                    case Constant.MsgType.MERGE:
                        replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_merge_message);
                        break;

                }
                replyMsg = replyContent;
                if (replyMsg.length() >= 30)
                    replyMsg = replyMsg.substring(0, 30) + "...";
                v.replyText.setVisibility(View.VISIBLE);
                v.replyText.setText(message.getQuoteElem().getQuoteMessage().getSenderNickname() + ":" + replyMsg);
            }
            else {
                msg = message.getAtElem().getText();//  msg = message.getContent();
            }

            v.sendState.setSendState(message.getStatus());
            if (message.getSenderFaceUrl() != null)
                v.avatar.load(message.getSenderFaceUrl());
            v.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    try {
                        intent = new Intent(itemView.getContext(), Class.forName("io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity"))
                            .putExtra(ID, message.getSendID());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    itemView.getContext().startActivity(intent);
                }
            });

            //@mention handling of the received msg

            if(message!=null && message.getAtElem()!=null && message.getAtElem().getAtUsersInfo()!=null
                && message.getAtElem().getAtUsersInfo().size()>0 ) {
                AtElem atElem = message.getAtElem();

                for (int i = 0; i < atElem.getAtUsersInfo().size(); i++) {
                    Log.d(TAG, "BottomInputCote: First msg -> " + msg);
                    msg = msg.replaceFirst(atElem.getAtUsersInfo().get(i).getAtUserID(), atElem.getAtUsersInfo().get(i).getGroupNickname());
                    Log.d(TAG, "bindRight: after replace msg->" + msg);
                }
                SpannableString ss = new SpannableString(msg);

                for (int i = 0; i < atElem.getAtUsersInfo().size(); i++) {
                    AtUserInfo atUserInfo = atElem.getAtUsersInfo().get(i);
                    Log.d(TAG, "BottomInputCote: First msg -> " + msg);
                    int startIndex = msg.indexOf(atUserInfo.getGroupNickname());

                    Log.d(TAG, "bindRight: startindex -> " + startIndex);
                    int i2 = atUserInfo.getGroupNickname().length();
                    int endIndex = startIndex + i2;
                    if(startIndex>0)
                        startIndex--;
                    Log.d(TAG, "bindRight: end index -> " + endIndex);

                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            Log.d(TAG, "onClick: clickableSpan ");
                            openPersonalDetails(atUserInfo.getAtUserID());
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(itemView.getContext().getResources().getColor(io.openim.android.ouicore.R.color.purple_500));
                            ds.setUnderlineText(false);
                        }
                    };
                    if(startIndex<0)
                        startIndex=0;
                    if(endIndex>=ss.length())
                        endIndex=ss.length();

                 //   if(startIndex>=0 && endIndex<ss.length())
                        ss.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    else
//                        ss.setSpan(clickableSpan, startIndex, endIndex-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                v.content.setText(ss);
            }

            v.content.setMovementMethod(LinkMovementMethod.getInstance());
            //v.content.setText(msg);
            v.tvTime.setText(TimeUtil.getTimeOnly(message.getSendTime()));
            v.selectLayout.select.setOnCheckedChangeListener((compoundButton, b) -> {
                selectMessageLeft(message,b);
            });

            final boolean[] test = {false};
            Message finalMessage = message;
            v.contentContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.showDialog(finalMessage, Constant.MsgType.TXT, itemView);
                    return true;
                }
            });
            if (message.getSessionType() == io.openim.android.ouicore.utils.Constant.SessionType.GROUP_CHAT) {
                v.nickName.setVisibility(View.VISIBLE);
                String nickname = message.getSenderNickname();
                if (nickname != null && nickname.length() > 12)
                    nickname = nickname.substring(0, 12) + "...";
                v.nickName.setText(nickname);
            } else
                v.nickName.setVisibility(View.GONE);
        }

        @Override
        void bindRight(View itemView, Message message, int position) {
            LayoutMsgTxtRightBinding v = LayoutMsgTxtRightBinding.bind(itemView);
            String replyMsg = null, msg = "";

            initCheckbox(v.selectLayout.select, position, message);

            v.selectLayout.select.setOnCheckedChangeListener((buttonView, isChecked) ->
                selectMessageRight(message, isChecked));
            // handle reply message
            if (message.getQuoteElem().getQuoteMessage() != null) {
                String replyContent = "";
                switch (message.getQuoteElem().getQuoteMessage().getContentType()) {
                    case Constant.MsgType.TXT:
                        replyContent = message.getQuoteElem().getQuoteMessage().getContent();
                        break;
                    case Constant.MsgType.PICTURE:
                        replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_picture_message);
                        break;
                    case Constant.MsgType.VIDEO:
                        replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_video_message);
                        break;
                    case Constant.MsgType.FILE:
                        replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_file_message);
                        break;
                    case Constant.MsgType.VOICE:
                        replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_voice_message);
                        break;
                    case Constant.MsgType.MERGE:
                        replyContent = itemView.getContext().getString(io.openim.android.ouicore.R.string.sent_a_merge_message);
                        break;
                }
                replyMsg = replyContent;
                msg = message.getQuoteElem().getText();
                if (replyMsg.length() >= 30)
                    replyMsg = replyMsg.substring(0, 30) + "...";
                v.replyText.setVisibility(View.VISIBLE);
                v.replyText.setText(message.getQuoteElem().getQuoteMessage().getSenderNickname() + " " + replyMsg);
            } else {
                msg = message.getAtElem().getText();
            }

            System.out.println("the message : " + message.getContent() + " => status is " + message.getStatus());
            v.sendState.setSendState(message.getStatus());
            if (message.getSenderFaceUrl() != null)
                v.avatar2.load(message.getSenderFaceUrl());

            //@mention handling of the received msg

            if(message!=null && message.getAtElem()!=null && message.getAtElem().getAtUsersInfo()!=null
               && message.getAtElem().getAtUsersInfo().size()>0 ) {
                AtElem atElem = message.getAtElem();

                for (int i = 0; i < atElem.getAtUsersInfo().size(); i++) {
                    Log.d(TAG, "BottomInputCote: First msg -> " + msg);
                    msg = msg.replaceFirst(atElem.getAtUsersInfo().get(i).getAtUserID(), atElem.getAtUsersInfo().get(i).getGroupNickname());
                    Log.d(TAG, "bindRight: after replace msg->" + msg);
                }
                SpannableString ss = new SpannableString(msg);

                for (int i = 0; i < atElem.getAtUsersInfo().size(); i++) {
                    AtUserInfo atUserInfo = atElem.getAtUsersInfo().get(i);
                    Log.d(TAG, "BottomInputCote: First msg -> " + msg);
                    int startIndex = msg.indexOf(atUserInfo.getGroupNickname());
                    int i2 = atUserInfo.getGroupNickname().length();
                    int endIndex = startIndex + i2;
                    if(startIndex>0)
                        startIndex--;
                    Log.d(TAG, "bindRight: end index -> " + endIndex);
                    Log.d(TAG, "bindRight: startindex -> " + startIndex);

                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            Log.d(TAG, "onClick: clickableSpan ");
                            openPersonalDetails(atUserInfo.getAtUserID());
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(itemView.getContext().getResources().getColor(io.openim.android.ouicore.R.color.purple_500));
                            ds.setUnderlineText(false);
                        }
                    };
                    if(startIndex<0)
                        startIndex=0;
                    if(endIndex>=ss.length())
                        endIndex=ss.length();
                    ss.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                v.content2.setText(ss);
            }

            v.content2.setMovementMethod(LinkMovementMethod.getInstance());
            v.content2.setHighlightColor(Color.RED);
            v.content2.setMovementMethod(LinkMovementMethod.getInstance());
            v.tvTime2.setText(TimeUtil.getTimeOnly(message.getSendTime()));

            v.selectLayout.select.setOnCheckedChangeListener((compoundButton, b) -> {
                selectMessageRight(message,b);
            });

            final boolean[] test = {false};
            Message finalMessage = message;
            v.contentContainer2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.showDialog(finalMessage, Constant.MsgType.TXT, itemView);
                    return true;
                }
            });

            if (!vm.isSingleChat) {
                if (message.isRead()) {
                    if (message.getAttachedInfoElem().getGroupHasReadInfo().getGroupMemberCount() > 0) {
                        int readCount = message.getAttachedInfoElem().getGroupHasReadInfo().getGroupMemberCount() -
                            message.getAttachedInfoElem().getGroupHasReadInfo().getHasReadCount() - 1;
                        v.read.setText(String.valueOf(readCount) + " people " + vm.getContext().getResources().getString(io.openim.android.ouicore.R.string.un_read));
                    }
                } else {
                    if (message.getAttachedInfoElem().getGroupHasReadInfo().getGroupMemberCount() > 0) {
                        int readCount = message.getAttachedInfoElem().getGroupHasReadInfo().getGroupMemberCount() -
                            message.getAttachedInfoElem().getGroupHasReadInfo().getHasReadCount() - 1;
                        v.read.setText(String.valueOf(readCount) + " people " + vm.getContext().getResources().getString(io.openim.android.ouicore.R.string.un_read));
                    }
                }
            } else {
                if (message.isRead()) {
                    v.read.setText(io.openim.android.ouicore.R.string.read);
                } else {
                    v.read.setText(io.openim.android.ouicore.R.string.un_read);
                }
            }
        }
    }

    public static class RecallView extends MessageViewHolder.MsgViewHolder {
        private static int CLICK_THRESHOLD = 100;
        ChatVM vm ;
        public RecallView(ViewGroup parent,ChatVM vm) {
            super(parent);
            this.vm = vm ;
        }

        @Override
        int getLeftInflatedId() {
            return R.layout.recall_layout;
        }

        @Override
        int getRightInflatedId() {
            return R.layout.recall_layout;
        }

        @SuppressLint("SetTextI18n")
        @Override
        void bindLeft(View itemView, Message message, int position) {
            RecallLayoutBinding v = RecallLayoutBinding.bind(itemView);
            v.msg.setText(""+message.getSenderNickname() + itemView.getContext().getString(io.openim.android.ouicore.R.string.someone_recalled_a_message));

        }

        @Override
        void bindRight(View itemView, Message message, int position) {
            RecallLayoutBinding v = RecallLayoutBinding.bind(itemView);
            v.msg.setText( vm.getContext().getString(io.openim.android.ouicore.R.string.you_recall_a_message));
        }

    }

    public static class DateView extends MessageViewHolder.MsgViewHolder {
        private static int CLICK_THRESHOLD = 100;
        ChatVM vm ;
        public DateView(ViewGroup parent,ChatVM vm) {
            super(parent);
            this.vm = vm ;
        }

        @Override
        int getLeftInflatedId() {
            return R.layout.group_notification_layout;
        }

        @Override
        int getRightInflatedId() {
            return R.layout.group_notification_layout;
        }

        @SuppressLint("SetTextI18n")
        @Override
        void bindLeft(View itemView, Message message, int position) {
//            RecallLayoutBinding v = RecallLayoutBinding.bind(itemView);
//            v.msg.setText(""+message.getContent());
            bindRight(itemView,message,position);
        }

        @Override
        void bindRight(View itemView, Message message, int position) {
           // RecallLayoutBinding v = RecallLayoutBinding.bind(itemView);
            GroupNotificationLayoutBinding v=GroupNotificationLayoutBinding.bind(itemView);
            v.dateText.setText(""+message.getContent());
           // bindLeft(itemView,message,position);
        }

    }

    public static class MergeView extends MessageViewHolder.MsgViewHolder {
        private static int CLICK_THRESHOLD = 100;
        ChatVM vm;

        public MergeView(ViewGroup parent, ChatVM vm) {
            super(parent,vm);
            this.vm = vm;
        }

        @Override
        int getLeftInflatedId() {
            return R.layout.left_merge_message;
        }

        @Override
        int getRightInflatedId() {
            return R.layout.right_merge_message;
        }

        @Override
        void bindLeft(View itemView, Message message, int position) {
            LeftMergeMessageBinding v = LeftMergeMessageBinding.bind(itemView);
            v.title.setText(message.getMergeElem().getTitle());
            v.avater.load(message.getSenderFaceUrl());
            v.avater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    try {
                        intent = new Intent(itemView.getContext(), Class.forName("io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity"))
                            .putExtra(ID,message.getSendID());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    itemView.getContext().startActivity(intent);
                }
            });

            MessagesSummaryAdapter adapter = new MessagesSummaryAdapter(vm.getContext(), vm, message, dialog, itemView);
            adapter.setSummaryList(message.getMergeElem().getAbstractList());
            v.recyclerView.setLayoutManager(new LinearLayoutManager(vm.getContext()));
            v.recyclerView.setAdapter(adapter);
            v.recyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatHistoryMergeMessagesActivity.messages = message.getMergeElem().getMultiMessage();
                    ChatHistoryMergeMessagesActivity.title = message.getMergeElem().getTitle() ;
                    vm.getContext().startActivity(new Intent(vm.getContext() , ChatHistoryMergeMessagesActivity.class));
                }
            });
            v.messageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatHistoryMergeMessagesActivity.messages = message.getMergeElem().getMultiMessage();
                    ChatHistoryMergeMessagesActivity.title = message.getMergeElem().getTitle() ;
                    vm.getContext().startActivity(new Intent(vm.getContext() , ChatHistoryMergeMessagesActivity.class));
                }
            });

            v.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dialog.showDialog(message, Constant.MsgType.MERGE, itemView);
                    return true;
                }
            });
            v.recyclerView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dialog.showDialog(message, Constant.MsgType.MERGE, itemView);
                    return true;
                }
            });
            v.tvTime.setText(TimeUtil.getTimeOnly(message.getSendTime()));
        }

        @Override
        void bindRight(View itemView, Message message, int position) {
            RightMergeMessageBinding v = RightMergeMessageBinding.bind(itemView);
            v.title.setText(message.getMergeElem().getTitle());
            v.avater.load(message.getSenderFaceUrl());
            MessagesSummaryAdapter adapter = new MessagesSummaryAdapter(vm.getContext(), vm, message, dialog, itemView);
            adapter.setSummaryList(message.getMergeElem().getAbstractList());
            v.recyclerView.setLayoutManager(new LinearLayoutManager(vm.getContext()));
            v.recyclerView.setAdapter(adapter);
            v.recyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatHistoryMergeMessagesActivity.messages = message.getMergeElem().getMultiMessage();
                    ChatHistoryMergeMessagesActivity.title = message.getMergeElem().getTitle() ;
                    vm.getContext().startActivity(new Intent(vm.getContext() , ChatHistoryMergeMessagesActivity.class));
                }
            });
            v.messageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatHistoryMergeMessagesActivity.messages = message.getMergeElem().getMultiMessage();
                    ChatHistoryMergeMessagesActivity.title = message.getMergeElem().getTitle();
                    vm.getContext().startActivity(new Intent(vm.getContext() , ChatHistoryMergeMessagesActivity.class));                }
            });
            v.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dialog.showDialog(message, Constant.MsgType.MERGE, itemView);
                    return true;
                }
            });
            v.tvTime2.setText(TimeUtil.getTimeOnly(message.getSendTime()));
        }

    }

    public static class IMGView extends MessageViewHolder.MsgViewHolder {

        public IMGView(ViewGroup itemView , ChatVM vm) {
            super(itemView,vm);
        }

        public IMGView(ViewGroup itemView) {
            super(itemView);
        }

        @Override
        int getLeftInflatedId() {
            return R.layout.layout_msg_img_left;
        }

        @Override
        int getRightInflatedId() {
            return R.layout.layout_msg_img_right;
        }

        @Override
        void bindLeft(View itemView, Message message, int position) {
            LayoutMsgImgLeftBinding v = LayoutMsgImgLeftBinding.bind(itemView);
            String url = message.getPictureElem().getSnapshotPicture().getUrl();
            initCheckbox(v.selectLayout.select, position, message);
            v.tvTime.setText(TimeUtil.getTimeOnly(message.getSendTime()));
            v.download.setImageResource(io.openim.android.ouicore.R.mipmap.icon_update);
            String imgName =message.getPictureElem().getSourcePicture().getUuid()+"."+message.getPictureElem().getSourcePicture().getType().substring(6,message.getPictureElem().getSourcePicture().getType().length());
            String path = FolderHelper.isPicturePresent(itemView.getContext() ,  imgName, 0);
            System.out.println("imgName : " + imgName + "\n path" + path);
            if(path.isEmpty()){
                v.progressBar.bringToFront();
                v.download.bringToFront();

                v.download.setVisibility(View.VISIBLE);
                v.progressBar.setVisibility(View.INVISIBLE);
                Glide.with(itemView.getContext()).asBitmap()
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                    .into(v.content2);
                v.progressBar.setVisibility(View.VISIBLE);
                v.download.setImageResource(io.openim.android.ouicore.R.drawable.ic_baseline_close_48);
                DownloadMedia.downloadPicturesWithLibrary(itemView.getContext(), url,
                    imgName
                    ,new DownloadMedia.PercentAction() {
                        @Override
                        public void onPercentUpdate(int percent) {
                        }

                        @Override
                        public void onCompleted(String completed) {
                            System.out.println(" progressBar.getVisibility " +v.progressBar.getVisibility() +  " " + View.INVISIBLE + " " + View.VISIBLE);
                            if(v.progressBar.getVisibility() == View.INVISIBLE){
                                return;
                            }
                            v.download.setVisibility(View.INVISIBLE);
                            v.progressBar.setVisibility(View.INVISIBLE);
                            String path = FolderHelper.isPicturePresent(itemView.getContext() ,  imgName , 0);
                            Glide.with(itemView.getContext()).asBitmap()
                                .load(path)
                                .into(v.content2);
                        }
                    });
                v.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    if(v.progressBar.getVisibility() == View.VISIBLE){
                        v.progressBar.setVisibility(View.INVISIBLE);
                        v.download.setImageResource(io.openim.android.ouicore.R.mipmap.icon_update);
                        return;
                    }
                        v.progressBar.setVisibility(View.VISIBLE);
                        v.download.setImageResource(io.openim.android.ouicore.R.drawable.ic_baseline_close_48);
                        DownloadMedia.downloadPicturesWithLibrary(itemView.getContext(), url,
                            imgName
                            ,new DownloadMedia.PercentAction() {
                            @Override
                            public void onPercentUpdate(int percent) {
                            }

                            @Override
                            public void onCompleted(String completed) {
                                System.out.println(" progressBar.getVisibility " +v.progressBar.getVisibility() +  " " + View.INVISIBLE + " " + View.VISIBLE);
                                if(v.progressBar.getVisibility() == View.INVISIBLE){
                                    return;
                                }
                                v.download.setVisibility(View.INVISIBLE);
                                v.progressBar.setVisibility(View.INVISIBLE);
                                String path = FolderHelper.isPicturePresent(itemView.getContext() ,  imgName , 0);
                                Glide.with(itemView.getContext()).asBitmap()
                                    .load(path)
                                    .into(v.content2);
                            }
                        });
                    }
                });
            }else{
                v.download.setVisibility(View.GONE);
                v.progressBar.setVisibility(View.GONE);
                Glide.with(itemView.getContext()).asBitmap()
                    .load(path)
                    .into(v.content2);
            }
            if(message.getSenderFaceUrl()!=null)
                v.avatar2.load(message.getSenderFaceUrl());
            v.avatar2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    try {
                        intent = new Intent(itemView.getContext(), Class.forName("io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity"))
                            .putExtra(ID,message.getSendID());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    itemView.getContext().startActivity(intent);
                }
            });

            v.content2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.showDialog(message,Constant.MsgType.PICTURE,itemView);
                    return false;
                }
            });
            v.selectLayout.select.setOnCheckedChangeListener((buttonView, isChecked) -> selectMessageLeft(message, isChecked));
            v.sendState2.setSendState(message.getStatus());
            toPreview(v.content2, message.getClientMsgID());
        }

        @Override
        void bindRight(View itemView, Message message, int position) {
            LayoutMsgImgRightBinding v = LayoutMsgImgRightBinding.bind(itemView);
            String url = message.getPictureElem().getSourcePicture().getUrl();
            initCheckbox(v.selectLayout.select, position, message);

            if (messageAdapter.hasStorage) {
                String filePath = message.getPictureElem().getSourcePath();
                if(filePath != null) {
                    if (new File(filePath).exists())
                        url = filePath;
                }
            }
            v.tvTime.setText(TimeUtil.getTimeOnly(message.getSendTime()));
            v.download.setImageResource(io.openim.android.ouicore.R.mipmap.icon_update);
            String imgName =message.getPictureElem().getSourcePicture().getUuid()+"."+message.getPictureElem().getSourcePicture().getType().substring(6,message.getPictureElem().getSourcePicture().getType().length());
            String path = FolderHelper.isPicturePresent(itemView.getContext() ,  imgName, 0);

            if(path.isEmpty()){
                v.progressBar.bringToFront();
                v.download.bringToFront();

                v.download.setVisibility(View.VISIBLE);
                v.progressBar.setVisibility(View.INVISIBLE);
                Glide.with(itemView.getContext()).asBitmap()
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                    .into(v.content);
                v.progressBar.setVisibility(View.VISIBLE);
                v.download.setImageResource(io.openim.android.ouicore.R.drawable.ic_baseline_close_48);
                if(url != null) {
                    DownloadMedia.downloadPicturesWithLibrary(itemView.getContext(), url,
                        imgName
                        , new DownloadMedia.PercentAction() {
                            @Override
                            public void onPercentUpdate(int percent) {
                            }

                            @Override
                            public void onCompleted(String completed) {
                                System.out.println(" progressBar.getVisibility " + v.progressBar.getVisibility() + " " + View.INVISIBLE + " " + View.VISIBLE);
                                if (v.progressBar.getVisibility() == View.INVISIBLE) {
                                    return;
                                }
                                v.download.setVisibility(View.INVISIBLE);
                                v.progressBar.setVisibility(View.INVISIBLE);
                                String path = FolderHelper.isPicturePresent(itemView.getContext(), imgName, 0);
                                Glide.with(itemView.getContext()).asBitmap()
                                    .load(path)
                                    .into(v.content);
                            }
                        });
                }
            }else{
                v.download.setVisibility(View.GONE);
                v.progressBar.setVisibility(View.GONE);
                Glide.with(itemView.getContext()).asBitmap()
                    .load(path)
                    .into(v.content);
            }

            v.content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.showDialog(message,Constant.MsgType.PICTURE,itemView);
                    return false;
                }
            });

            v.selectLayout.select.setOnCheckedChangeListener(((buttonView, isChecked) -> selectMessageRight(message, isChecked)));
            if(message.getSenderFaceUrl()!=null)
            v.avatar.load(message.getSenderFaceUrl());

            v.sendState.setSendState(message.getStatus());
            toPreview(v.content, message.getClientMsgID());
        }


    }

    public static class BusinessCardView extends MessageViewHolder.MsgViewHolder {

        public BusinessCardView(ViewGroup itemView,ChatVM vm) {
            super(itemView,vm);
        }

        @Override
        int getLeftInflatedId() {
            return R.layout.layout_msg_card_left;
        }

        @Override
        int getRightInflatedId() {
            return R.layout.layout_msg_card_right;
        }

        @Override
        void bindLeft(View itemView, Message message, int position) {
            Gson gson = new Gson();
            BusinessCard businessCard = gson.fromJson(message.getContent(), BusinessCard.class);

            LayoutMsgCardLeftBinding v = LayoutMsgCardLeftBinding.bind(itemView);
            initCheckbox(v.selectLayout.select, position,message);
            v.tvTime.setText(TimeUtil.getTimeOnly(message.getSendTime()));
            v.cardAvatar.load(businessCard.getFaceURL());
            String name = businessCard.getNickname() ;
            if(name.length() > 12)
                name = name.substring(0,12)+"...";
            v.cardNickname.setText(name);
            v.cardName.setText(businessCard.getUserID());
            if(message.getSenderFaceUrl()!=null)
                v.avatar.load(message.getSenderFaceUrl());
            v.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    try {
                        intent = new Intent(itemView.getContext(), Class.forName("io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity"))
                            .putExtra(ID,message.getSendID());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    itemView.getContext().startActivity(intent);
                }
            });

            v.sendState.setSendState(message.getStatus());
            v.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    try {
                        intent = new Intent(itemView.getContext(), Class.forName("io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity"))
                            .putExtra(ID,businessCard.getUserID());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    itemView.getContext().startActivity(intent);
                }
            });
            v.content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dialog.showDialog(message, message.getContentType(), itemView);
                    return true;
                }
            });
            v.selectLayout.select.setOnCheckedChangeListener(((buttonView, isChecked) ->
                selectMessageLeft(message,isChecked)));

        }

        @Override
        void bindRight(View itemView, Message message, int position) {
            Gson gson = new Gson();
            BusinessCard businessCard = gson.fromJson(message.getContent(),
                BusinessCard.class);

            LayoutMsgCardRightBinding v = LayoutMsgCardRightBinding.bind(itemView);
            initCheckbox(v.selectLayout.select, position, message);
            v.tvTime2.setText(TimeUtil.getTimeOnly(message.getSendTime()));
            v.cardAvatar.load(businessCard.getFaceURL());
            String name = businessCard.getNickname() ;
            if(name.length() > 12)
                name = name.substring(0,12)+"...";
            v.cardNickname.setText(name);
            v.cardName.setText(businessCard.getUserID());
            if(message.getSenderFaceUrl()!=null)
                v.avatar.load(message.getSenderFaceUrl());

            v.sendState.setSendState(message.getStatus());
            v.content.setOnClickListener(view -> {
                Intent intent = null;
                try {
                    intent = new Intent(itemView.getContext(), Class.forName("io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity"))
                        .putExtra(ID,businessCard.getUserID());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                itemView.getContext().startActivity(intent);
            });
            v.content.setOnLongClickListener(v1 -> {
                dialog.showDialog(message, message.getContentType(), itemView);
                return true;
            });

            v.selectLayout.select.setOnCheckedChangeListener((buttonView, isChecked) ->
                selectMessageRight(message, isChecked));
        }
    }

    public static class AudioView extends MessageViewHolder.MsgViewHolder {
        private Message playingMessage;

        public AudioView(ViewGroup itemView, ChatVM vm) {
            super(itemView,vm);
        }

        @Override
        public void bindRecyclerView(RecyclerView recyclerView) {
            super.bindRecyclerView(recyclerView);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (null != playingMessage) {
                        int index = messageAdapter.getMessages().indexOf(playingMessage);
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        int firstVisiblePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                        int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
//                        L.e("--------firstVisiblePosition-------="+firstVisiblePosition);
//                        L.e("--------lastVisiblePosition--------="+lastVisiblePosition);
//                        L.e("--------index--------="+index);

                        if (index < firstVisiblePosition || index > lastVisiblePosition) {
                            SPlayer.instance().stop();
                            playingMessage = null;
                        }
                    }

                }
            });
        }

        @Override
        int getLeftInflatedId() {
            return R.layout.layout_msg_audio_left;
        }

        @Override
        int getRightInflatedId() {
            return R.layout.layout_msg_audio_right;
        }

        @Override
        void bindLeft(View itemView, Message message, int position) {
            final LayoutMsgAudioLeftBinding view = LayoutMsgAudioLeftBinding.bind(itemView);
            initCheckbox(view.selectLayout.select,position, message);
            view.sendState.setSendState(message.getStatus());
            view.duration.setText(message.getSoundElem().getDuration() + "\"");
            view.avatar.load(message.getSenderFaceUrl());
            view.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    try {
                        intent = new Intent(itemView.getContext(), Class.forName("io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity"))
                            .putExtra(ID,message.getSendID());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    itemView.getContext().startActivity(intent);
                }
            });

            view.tvTime.setText(TimeUtil.getTimeOnly(message.getSendTime()));
            view.content.setOnClickListener(v -> extracted(message, view.lottieView));
            view.content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.showDialog(message,Constant.MsgType.VIDEO,itemView);
                    return true;
                }
            });
            view.selectLayout.select.setOnCheckedChangeListener(((buttonView, isChecked) -> selectMessageLeft(message, isChecked)));
        }

        @Override
        void bindRight(View itemView, Message message, int position) {
            final LayoutMsgAudioRightBinding view = LayoutMsgAudioRightBinding.bind(itemView);

            initCheckbox(view.selectLayout.select, position, message);
            view.sendState2.setSendState(message.getStatus());
            view.duration2.setText(message.getSoundElem().getDuration() + "\"");
            view.avatar2.load(message.getSenderFaceUrl());
            view.tvTime2.setText(TimeUtil.getTimeOnly(message.getSendTime()));
            view.content2.setOnClickListener(v -> extracted(message, view.lottieView2));
            view.content2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.showDialog(message,Constant.MsgType.VIDEO,itemView);
                    return true;
                }
            });
            view.selectLayout.select.setOnCheckedChangeListener(((buttonView, isChecked) ->
                selectMessageRight(message, isChecked)));
        }

        private void extracted(Message message, LottieAnimationView lottieView) {
            SPlayer.instance().getMediaPlayer();
            SPlayer.instance().stop();
            SPlayer.instance().playByUrl(message.getSoundElem().getSourceUrl(), new PlayerListener() {
                @Override
                public void LoadSuccess(SMediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }

                @Override
                public void Loading(SMediaPlayer mediaPlayer, int i) {

                }

                @Override
                public void onCompletion(SMediaPlayer mediaPlayer) {
                    mediaPlayer.stop();
                }

                @Override
                public void onError(Exception e) {
                    lottieView.cancelAnimation();
                    lottieView.setProgress(1);
                }
            });

            SPlayer.instance().getMediaPlayer().setOnPlayStateListener(new SMediaPlayer.OnPlayStateListener() {
                @Override
                public void started() {
                    playingMessage = message;
                    lottieView.playAnimation();
                }

                @Override
                public void paused() {

                }

                @Override
                public void stopped() {
                    lottieView.cancelAnimation();
                    lottieView.setProgress(1);
                }

                @Override
                public void completed() {

                }
            });
        }
    }

    public static class VideoView extends MessageViewHolder.MsgViewHolder {

        public VideoView(ViewGroup itemView, ChatVM vm) {
            super(itemView, vm);
        }

        @Override
        int getLeftInflatedId() {
            return R.layout.layout_msg_video_left;
        }

        @Override
        int getRightInflatedId() {
            return R.layout.layout_msg_video_right;
        }

        @Override
        void bindLeft(View itemView, Message message, int position) {
            LayoutMsgVideoLeftBinding view = LayoutMsgVideoLeftBinding.bind(itemView);
            initCheckbox(view.selectLayout.select, position, message);
            view.sendState2.setSendState(message.getStatus());
            view.videoPlay2.setVisibility(View.VISIBLE);
            view.avatar2.load(message.getSenderFaceUrl());
            DownloadMedia.downloadVideosWithLibrary(vm.getContext(), message.getVideoElem().getVideoUrl(), message.getVideoElem().getVideoUUID(), new DownloadMedia.PercentAction() {
                @Override
                public void onPercentUpdate(int percent) {

                }

                @Override
                public void onCompleted(String completed) {
                    System.out.println(" progressBar.getVisibility " +view.progressBar.getVisibility() +  " " + View.INVISIBLE + " " + View.VISIBLE);
                    if(view.progressBar.getVisibility() == View.INVISIBLE){
                        return;
                    }
                    view.download.setVisibility(View.INVISIBLE);
                    view.progressBar.setVisibility(View.INVISIBLE);
                }
            });
            view.avatar2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    try {
                        intent = new Intent(itemView.getContext(), Class.forName("io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity"))
                            .putExtra(ID, message.getSendID());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    itemView.getContext().startActivity(intent);
                }
            });
            view.tvTime.setText(TimeUtil.getTimeOnly(message.getSendTime()));

            view.contentGroup2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.showDialog(message,Constant.MsgType.VIDEO,itemView);
                    return true;
                }
            });
            Glide.with(itemView.getContext())
                .load(message.getVideoElem().getSnapshotUrl())
                .into(view.content2);
            toPreview(view.contentGroup2, message.getClientMsgID());
            view.selectLayout.select.setOnCheckedChangeListener(((buttonView, isChecked) ->
                selectMessageLeft(message,isChecked)));
        }

        @Override
        void bindRight(View itemView, Message message, int position) {
            LayoutMsgVideoRightBinding view = LayoutMsgVideoRightBinding.bind(itemView);
            initCheckbox(view.selectLayout.select, position, message);
            view.sendState.setSendState(message.getStatus());
            view.videoPlay.setVisibility(View.VISIBLE);
            view.avatar.load(message.getSenderFaceUrl());
            DownloadMedia.downloadVideosWithLibrary(vm.getContext(), message.getVideoElem().getVideoUrl(), message.getVideoElem().getVideoUUID(), new DownloadMedia.PercentAction() {
                @Override
                public void onPercentUpdate(int percent) {

                }

                @Override
                public void onCompleted(String completed) {
                    System.out.println(" progressBar.getVisibility " +view.progressBar.getVisibility() +  " " + View.INVISIBLE + " " + View.VISIBLE);
                    if(view.progressBar.getVisibility() == View.INVISIBLE){
                        return;
                    }
                    view.download.setVisibility(View.INVISIBLE);
                    view.progressBar.setVisibility(View.INVISIBLE);
                }
            });

            String snapshotUrl = message.getVideoElem().getSnapshotUrl();
            if (messageAdapter.hasStorage || null == snapshotUrl) {
                String filePath = message.getVideoElem().getSnapshotPath();
                if (new File(filePath).exists())
                    snapshotUrl = filePath;
            }

            Glide.with(itemView.getContext())
                .load(snapshotUrl)
                .into(view.content);
            view.contentGroup.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.showDialog(message,Constant.MsgType.VIDEO, itemView);
                    return true;
                }
            });
            toPreview(view.contentGroup, message.getClientMsgID());
            view.tvTime.setText(TimeUtil.getTimeOnly(message.getSendTime()));
            view.selectLayout.select.setOnCheckedChangeListener((buttonView, isChecked) ->
                selectMessageRight(message,isChecked));
        }

    }

    public static class FileView extends MessageViewHolder.MsgViewHolder {

        public FileView(ViewGroup itemView, ChatVM vm) {
            super(itemView, vm);
        }

        @Override
        int getLeftInflatedId() {
            return R.layout.layout_msg_file_left;
        }

        @Override
        int getRightInflatedId() {
            return R.layout.layout_msg_file_right;
        }

        @Override
        void bindLeft(View itemView, Message message, int position) {
            LayoutMsgFileLeftBinding view = LayoutMsgFileLeftBinding.bind(itemView);

            initCheckbox(view.selectLayout.select, position,message);
            view.selectLayout.select.setOnCheckedChangeListener((buttonView, isChecked) ->
                selectMessageLeft(message, isChecked));

            view.tvTime.setText(TimeUtil.getTimeOnly(message.getSendTime()));
            view.avatar.load(message.getSenderFaceUrl());
            view.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    try {
                        intent = new Intent(itemView.getContext(), Class.forName("io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity"))
                            .putExtra(ID, message.getSendID());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    itemView.getContext().startActivity(intent);
                }
            });

//            String fileName = message.getFileElem().getFileName().replaceAll("%20", " ");
            String fileName = "";
            try {
                fileName = java.net.URLDecoder.decode(message.getFileElem().getFileName(), StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int index = fileName.lastIndexOf('.');
            if(index > 0) {
                String extension = fileName.substring(index + 1);
                System.out.println(fileName + "\t" + extension);

                view.ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DownloadImpl.getInstance(vm.getContext()).cancel(message.getFileElem().getSourceUrl());
                    }
                });

                DownloadMedia.downloadFilesWithLibrary(vm.getContext(), message.getFileElem().getSourceUrl(), fileName, new DownloadMedia.PercentAction() {
                    @Override
                    public void onPercentUpdate(int percent) {
                        view.progressBar.setProgress(percent);
                    }

                    @Override
                    public void onCompleted(String completed) {

                        view.ivImage.setVisibility(View.VISIBLE);
                        view.flProgress.setVisibility(View.GONE);

                        switch(extension) {
                            case "pdf":
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_ftp"))
                                    .into(view.ivImage);
                                break;
                            case "txt":
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_txt"))
                                    .into(view.ivImage);
                                break;
                            case "ppt":
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_ppt"))
                                    .into(view.ivImage);
                                break;
                            case "jpg":
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_image"))
                                    .into(view.ivImage);
                                break;
                            case "jpeg":
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_image"))
                                    .into(view.ivImage);
                                break;
                            case "xls":
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_excel"))
                                    .into(view.ivImage);
                                break;
                            case "xlsx":
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_excel"))
                                    .into(view.ivImage);
                                break;
                            case "mp4":
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_video"))
                                    .into(view.ivImage);
                                break;
                            case "mp3":
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_music"))
                                    .into(view.ivImage);
                                break;
                            case "zip":
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_zip"))
                                    .into(view.ivImage);
                                break;
                            case "rar":
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_zip"))
                                    .into(view.ivImage);
                                break;
                            case "docx":
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_word"))
                                    .centerCrop()
                                    .into(view.ivImage);
                                break;
                            default:
                                Glide.with(vm.getContext())
                                    .load(getImage("ic_file_other"))
                                    .centerCrop()
                                    .into(view.ivImage);
                        }
                    }
                });

                switch(extension) {
                    case "pdf":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_ftp"))
                            .into(view.ivImage);
                        break;
                    case "txt":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_txt"))
                            .into(view.ivImage);
                        break;
                    case "ppt":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_ppt"))
                            .into(view.ivImage);
                        break;
                    case "jpg":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_image"))
                            .into(view.ivImage);
                        break;
                    case "jpeg":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_image"))
                            .into(view.ivImage);
                        break;
                    case "xls":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_excel"))
                            .into(view.ivImage);
                        break;
                    case "xlsx":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_excel"))
                            .into(view.ivImage);
                        break;
                    case "mp4":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_video"))
                            .into(view.ivImage);
                        break;
                    case "mp3":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_music"))
                            .into(view.ivImage);
                        break;
                    case "zip":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_zip"))
                            .into(view.ivImage);
                        break;
                    case "rar":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_zip"))
                            .into(view.ivImage);
                        break;
                    case "docx":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_word"))
                            .centerCrop()
                            .into(view.ivImage);
                        break;
                    default:
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_other"))
                            .centerCrop()
                            .into(view.ivImage);
                }
            }
            view.title.setText(fileName);
            Long size = message.getFileElem().getFileSize();
            view.size.setText(ByteUtil.bytes2kb(size) + "");
            view.content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.showDialog(message, Constant.MsgType.FILE, itemView);
                    return false;
                }
            });
            view.sendState.setSendState(message.getStatus());
            view.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(message.getFileElem().getSourceUrl())));
                    } catch (Exception e) {
                        e.getStackTrace();
                    }

                }
            });
        }

        @Override
        void bindRight(View itemView, Message message, int position) {
            LayoutMsgFileRightBinding view = LayoutMsgFileRightBinding.bind(itemView);
            initCheckbox(view.selectLayout.select, position, message);

            view.selectLayout.select.setOnCheckedChangeListener(((buttonView, isChecked) ->
                selectMessageRight(message,isChecked)));
            view.tvTime.setText(TimeUtil.getTimeOnly(message.getSendTime()));
            view.avatar2.load(message.getSenderFaceUrl());

            Long size = message.getFileElem().getFileSize();
            view.size2.setText(ByteUtil.bytes2kb(size) + "");
            String fileName = "";
            try {
                fileName = java.net.URLDecoder.decode(message.getFileElem().getFileName(), StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int index = fileName.lastIndexOf('.');
            if(index > 0) {
                String extension = fileName.substring(index + 1);
                System.out.println(fileName + "\t" + extension);

                DownloadMedia.downloadFilesWithLibrary(vm.getContext(), message.getFileElem().getSourceUrl(), fileName, new DownloadMedia.PercentAction() {
                    @Override
                    public void onPercentUpdate(int percent) {

                    }

                    @Override
                    public void onCompleted(String completed) {
//                        System.out.println(" progressBar.getVisibility " +view.progressBar.getVisibility() +  " " + View.INVISIBLE + " " + View.VISIBLE);
//                        if(view.progressBar.getVisibility() == View.INVISIBLE){
//                            return;
//                        }
//                        view.download.setVisibility(View.INVISIBLE);
//                        view.progressBar.setVisibility(View.INVISIBLE);
                    }
                });

                switch(extension) {
                    case "pdf":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_ftp"))
                            .into(view.ivImage);
                        break;
                    case "txt":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_txt"))
                            .into(view.ivImage);
                        break;
                    case "ppt":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_ppt"))
                            .into(view.ivImage);
                        break;
                    case "jpg":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_image"))
                            .into(view.ivImage);
                        break;
                    case "jpeg":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_image"))
                            .into(view.ivImage);
                        break;
                    case "xls":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_excel"))
                            .into(view.ivImage);
                        break;
                    case "xlsx":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_excel"))
                            .into(view.ivImage);
                        break;
                    case "mp4":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_video"))
                            .into(view.ivImage);
                        break;
                    case "mp3":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_music"))
                            .into(view.ivImage);
                        break;
                    case "zip":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_zip"))
                            .into(view.ivImage);
                        break;
                    case "rar":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_zip"))
                            .into(view.ivImage);
                        break;
                    case "docx":
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_word"))
                            .centerCrop()
                            .into(view.ivImage);
                        break;
                    default:
                        Glide.with(vm.getContext())
                            .load(getImage("ic_file_other"))
                            .centerCrop()
                            .into(view.ivImage);
                }
            }
            view.title2.setText(fileName);
            view.content2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.showDialog(message, Constant.MsgType.FILE, itemView);
                    return false;
                }
            });
            view.sendState2.setSendState(message.getStatus());

            view.content2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(message.getFileElem().getSourceUrl())));
                    } catch (Exception e) {
                        e.getStackTrace();
                    }

                }
            });
        }
    }

}
