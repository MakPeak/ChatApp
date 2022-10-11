package io.openim.android.ouiconversation.utils;

public class Constant {

    //加载中
    public static final int LOADING=201;

    public static class MsgType {
        //            * 101:文本消息<br/>
        public static final int TXT = 101;
        //            * 102:图片消息<br/>
        public static final int PICTURE = 102;
        //            * 103:语音消息<br/>
        public static final int VOICE = 103;
        //            * 104:视频消息<br/>
        public static final int VIDEO = 104;
        //            * 105:文件消息<br/>'
        public static final int FILE = 105;
        //            * 106:@消息<br/>
        public static final int MENTION = 106;
        //            * 107:合并消息<br/>
        public static final int MERGE = 107;
        //            * 108:转发消息<br/>
        public static final int CARD = 108;
        //            * 109:位置消息<br/>
        public static final int LOCATION = 109;
        //            * 110:自定义消息<br/>
        public static final int CUSTOMIZE = 110;
        //            * 111:撤回消息回执<br/>
        public static final int REVOKE = 111;
        //            * 112:C2C已读回执<br/>
        public static final int ALREADY_READ = 112;
        //            * 113:正在输入状态
        public static final int TYPING = 113;
        // reply message
        public static final int REPLY = 114;
        // Date message
        public static final int Date = 4000;

        //-----------GROUP ANNOUNCEMENTS-------------------
        //Group created
        public static final int GROUP_CREATED_NOTIFICATION = 1501;
        // Remove member
        public static final int MEMBER_KICKED_NOTIFICATION = 1508;
        //Change owner
        public static final int GROUP_OWNER_TRANSFERRED_NOTIFICATION = 1507;
        //Change any group related group information
        public static final int GROUP_INFO_SET_NOTIFICATION = 1502;
        //New Joinee - when new member is added using plus
        public static final int MEMBER_INVITED_NOTIFICATION = 1509;
        public static final int MEMBER_ENTER_NOTIFICATION = 1510;
        public static final int MEMBER_QUIT_NOTIFICATION = 1504;
        //Group notification
        public static final int GROUP_NOTIFICATION = 1519;
        //GROUP - ALL BAN
        public static final int GROUP_MUTED_NOTIFICATION = 1514;
        public static final int GROUP_CANCEL_MUTED_NOTIFICATION = 1515;

        public static final int GROUP_MEMBER_MUTED_NOTIFICATION = 1512;
        public static final int GROUP_MEMBER_CANCEL_MUTED_NOTIFICATION = 1513;


        //-----------PRIVATE ANNOUNCEMENTS-------------------
        public static final int FRIEND_ADDED_NOTIFICATION = 1204;
        public static final int BURN_AFTER_READING_NOTIFICATION = 1701;

        //-----------SYSTEM ANNOUNCEMENTS-------------------
        public static final int OA_NOTIFICATION = 1400;

        //1515

        //public static final int KEYCODE_AT = 77;

    }

    public static boolean IS_LOGIN = true;
    public static final String GROUP_ID ="ChatID";
    public static final String GROUP_ID_FULL="groupName";
    public static final String OWNER_ID="ownerID";
    public static final String GROUP_NAME="groupName";
    public static final String GROUP_DESC="groupDesc";
    public static final String GROUP_ANNOUNCEMENT_TIME="groupAnnouncementTime";
    public static final String GROUP_ANNOUNCEMENT="groupAnnouncement";
    public static final String GROUP_FACE_URL="groupURL";


    public static final int TRANSFER_OWNER = 11;
    public static final int REMOVE_MEMBER = 12;
    public static final int REMOVE_MEMBER_BAN=13;
    public static final int CLEAR_CHAT_HISTORY =14;
    public static final int MAKE_ADMIN = 15;
    public static final int REMOVE_ADMIN = 151;
    public static final int DISSOLVE_GROUP=16;
    public static final int EXIT_GROUP=17;
    public static final int MUTE_MEMBER_SECONDS=18;


    public static final int ROLE_LEVEL_MEMBER=1;
    public static final int ROLE_LEVEL_OWNER=2;
    public static final int ROLE_LEVEL_ADMIN=3;

}
