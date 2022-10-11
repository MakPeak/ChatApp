package io.openim.android.ouiconversation.widget;

import static io.openim.android.ouiconversation.utils.Constant.MsgType.CARD;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.FILE;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.MERGE;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.PICTURE;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.TXT;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.VIDEO;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.VOICE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.helper.widget.Flow;
import androidx.core.content.FileProvider;

import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.Extra;
import com.tonyodev.fetch2.Fetch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.adapter.MessageAdapter;
import io.openim.android.ouiconversation.ui.ForwardMessageActivity;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.utils.DownloadMedia;
import io.openim.android.ouicore.utils.FolderHelper;
import io.openim.android.sdk.models.Message;

public class MessageHoldDialog {

    Dialog dialog;
    ChatVM vm;
    boolean isChatHistory;
    String shareType ;
    MessageAdapter messageAdapter ;
    View itemView ;
    boolean test = false ;
    private Fetch fetch;
    private static String dirPath;

    public void setMessageAdapter(MessageAdapter messageAdapter) {
        this.messageAdapter = messageAdapter;
    }

    public MessageHoldDialog(ViewGroup parent,ChatVM vm,boolean isChatHistory){
        this.isChatHistory = isChatHistory ;
        AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        dialog.getWindow().setLayout(100, 100);
        this.vm = vm;
        itemView = parent ;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public void showDialog(Message msg, int viewType, View itemView) {
        if(messageAdapter.isSelecting)
            return;
        if (isChatHistory)
            return;
        if(!test){
            test = true ;
            dialog.show();
            dialog.dismiss();
        }
        dialog.setContentView(R.layout.message_options);
        Flow flow = dialog.getWindow().findViewById(R.id.flow);
        if(msg.getGroupID() != null){
            flow.setMaxElementsWrap(4);
        }
        TextView copyTV = dialog.getWindow().findViewById(R.id.copy);
        TextView revokeTV = dialog.getWindow().findViewById(R.id.revoke);
        TextView deleteTV = dialog.getWindow().findViewById(R.id.delete);
        TextView replyTV = dialog.getWindow().findViewById(R.id.reply);
        TextView forwardTV = dialog.getWindow().findViewById(R.id.forward);
        TextView selectTV = dialog.getWindow().findViewById(R.id.select);
        TextView shareTV = dialog.getWindow().findViewById(R.id.share);

        // handling if this message does not belong to me
        if(!msg.getSendID().equals(LoginCertificate.getCache(itemView.getContext()).userID)) {
            revokeTV.setVisibility(View.GONE);
            ((LinearLayout) dialog.getWindow().findViewById(R.id.outer_container)).setGravity(Gravity.START | Gravity.BOTTOM);
        }else{
            ((LinearLayout)dialog.getWindow().findViewById(R.id.outer_container)).setGravity(Gravity.END|Gravity.BOTTOM);
        }

        switch (msg.getContentType()){
            case TXT :
                shareTV.setVisibility(View.GONE);
                // handling revoke
                Date currentTime = Calendar.getInstance().getTime();
                long res = currentTime.getTime()-msg.getSendTime();
                res = res/1000;
                res = res/60;
                System.out.println("res : " + res);
                if(res > 5)
                    revokeTV.setVisibility(View.GONE);
                break;
            case PICTURE:
                copyTV.setVisibility(View.GONE);
                break;
            case VIDEO:
                copyTV.setVisibility(View.GONE);
                break;
            case FILE:
                copyTV.setVisibility(View.GONE);
                break;
            case MERGE:
                copyTV.setVisibility(View.GONE);
                forwardTV.setVisibility(View.GONE);
                selectTV.setVisibility(View.GONE);
                shareTV.setVisibility(View.GONE);
                break;
            case VOICE:
                copyTV.setVisibility(View.GONE);
                shareTV.setVisibility(View.GONE);
                break;
            case CARD:
                copyTV.setVisibility(View.GONE);
                shareTV.setVisibility(View.GONE);
                break;
        }

        copyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Message", msg.getContent());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(itemView.getContext(), io.openim.android.ouicore.R.string.message_copied, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        revokeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg.setSessionType(1);
                if(msg.getGroupID() != null)
                    msg.setSessionType(2);
                System.out.println("123213123" + msg.getSessionType());
                vm.revokeMessage(msg);
                dialog.dismiss();
            }
        });

        deleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vm.deleteMessage(msg);
                dialog.dismiss();
            }
        });

        replyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (msg.getQuoteElem().getQuoteMessage() != null) {
                    if (msg.getQuoteElem().getQuoteMessage().getContent().length() >= 10)
                        msg.setContent(msg.getQuoteElem().getQuoteMessage().getContent().substring(0, 9) + "...");
                }
                vm.replyMessage.setValue(msg);
                dialog.dismiss();
            }
        });

        forwardTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForwardMessageActivity.forwardMsg = msg;
                ForwardMessageActivity.chatVM = vm;
                itemView.getContext().startActivity(new Intent(itemView.getContext(), ForwardMessageActivity.class));
                dialog.dismiss();
            }
        });

        selectTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (!messageAdapter.isSelecting)
                    messageAdapter.notifyItemRangeChanged(0, messageAdapter.getItemCount());
                System.out.println(" message : " + msg.getContent());
//                    checkBox.setChecked(true);
                messageAdapter.isSelecting = true;
//                System.out.println("position : " + position);
//                messageAdapter.position = position;
            }
        });

        shareTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(viewType == TXT) {
                    System.out.println("TEXT SHARE");
                } else if (viewType == PICTURE){
                    try {
                        final long begin = SystemClock.elapsedRealtime();
                        JSONObject jsonObj = new JSONObject(msg.getContent());
                        jsonObj.getJSONObject("sourcePicture").get("url");
                        String UUID = (String) jsonObj.getJSONObject("sourcePicture").get("uuid");
                        String filePathIfExists =  FolderHelper.isPicturePresent(vm.getContext(), UUID, PICTURE);
                        System.out.println("FILE CHECK: " + filePathIfExists);
                        DownloadImpl.with(vm.getContext().getApplicationContext())
                            // FOR PICTURE
                            .url((String) jsonObj.getJSONObject("sourcePicture").get("url"))
                            .enqueue(new DownloadListenerAdapter() {
                                @Override
                                public void onProgress(String url, long downloaded, long length, long usedTime) {
                                    super.onProgress(url, downloaded, length, usedTime);
                                }

                                @Override
                                public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
                                    Log.i("TAG", " path:" + path + " url:" + url + " length:" + new File(path.getPath()).length());
                                    Log.i("TAG", " DownloadImpl time:" + (SystemClock.elapsedRealtime() - begin) + " length:" + new File(path.getPath()).length());

                                    io.openim.android.ouicore.utils.DownloadMedia.downloadPictures(vm.getContext(), path, UUID, new DownloadMedia.PercentAction() {
                                        @Override
                                        public void onPercentUpdate(int percent) {
                                            System.out.println("PERCENT COMPLETED: " + percent);
                                        }

                                        @Override
                                        public void onCompleted(String completed) {
                                            System.out.println(completed);
                                        }
                                    });

                                    File file = new File(path.getPath());
                                    if(file.exists()){
                                        System.out.println("FILE EXISTS");
                                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                        StrictMode.setVmPolicy(builder.build());
                                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                        intentShareFile.setType("*/*");
                                        intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(vm.getContext(), "io.openim.android.ouiconversation.provider", file));
                                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
                                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                                        List<ResolveInfo> resInfoList = vm.getContext().getPackageManager().queryIntentActivities(intentShareFile, PackageManager.MATCH_DEFAULT_ONLY);
                                        for (ResolveInfo resolveInfo : resInfoList) {
                                            String packageName = resolveInfo.activityInfo.packageName;
                                            vm.getContext().grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        }
                                        vm.getContext().startActivity(Intent.createChooser(intentShareFile, "Share File"));
                                    }else{
                                        System.out.println("FILE DOES NOT EXISTS");
                                    }
                                    return super.onResult(throwable, path, url, extra);
                                }
                            });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (viewType == VIDEO){

                    try {
                        final long begin = SystemClock.elapsedRealtime();
                        JSONObject jsonObj = null;
                        jsonObj = new JSONObject(msg.getContent());
                        String UUID = (String) jsonObj.get("videoUUID");
                        String filePathIfExists =  FolderHelper.isVideoPresent(vm.getContext(), UUID, VIDEO);
                        System.out.println("FILE CHECK: " + filePathIfExists);
//                        io.openim.android.ouicore.utils.DownloadMedia.downloadVideosWithLibrary(vm.getContext(), (String) jsonObj.get("videoUrl"), UUID, new DownloadMedia.PercentAction() {
//                            @Override
//                            public void onPercentUpdate(int percent) {
//                                System.out.println("PERCENT COMPLETED: " + percent);
//                            }
//
//                            @Override
//                            public void onCompleted(String completed) {
//                                System.out.println(completed);
//                            }
//                        });
                        DownloadImpl.with(vm.getContext().getApplicationContext())
                            // FOR VIDEO
                        .url((String) jsonObj.get("videoUrl"))
                        .enqueue(new DownloadListenerAdapter() {
                            @Override
                            public void onProgress(String url, long downloaded, long length, long usedTime) {
                                super.onProgress(url, downloaded, length, usedTime);
                            }

                            @Override
                            public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
                                Log.i("TAG", " path:" + path + " url:" + url + " length:" + new File(path.getPath()).length());
                                Log.i("TAG", " DownloadImpl time:" + (SystemClock.elapsedRealtime() - begin) + " length:" + new File(path.getPath()).length());

                                io.openim.android.ouicore.utils.DownloadMedia.downloadVideos(vm.getContext(), path, UUID, new DownloadMedia.PercentAction() {
                                    @Override
                                    public void onPercentUpdate(int percent) {
                                        System.out.println("PERCENT COMPLETED: " + percent);
                                    }

                                    @Override
                                    public void onCompleted(String completed) {
                                        System.out.println(completed);
                                    }
                                });

                                File file = new File(path.getPath());
                                if(file.exists()){
                                    System.out.println("FILE EXISTS");
                                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                    StrictMode.setVmPolicy(builder.build());
                                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                    intentShareFile.setType("*/*");
                                    intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(vm.getContext(), "io.openim.android.ouiconversation.provider", file));
                                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
                                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                                    List<ResolveInfo> resInfoList = vm.getContext().getPackageManager().queryIntentActivities(intentShareFile, PackageManager.MATCH_DEFAULT_ONLY);
                                    for (ResolveInfo resolveInfo : resInfoList) {
                                        String packageName = resolveInfo.activityInfo.packageName;
                                        vm.getContext().grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    }
                                    vm.getContext().startActivity(Intent.createChooser(intentShareFile, "Share File"));
                                }else{
                                    System.out.println("FILE DOES NOT EXISTS");
                                }
                                return super.onResult(throwable, path, url, extra);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (viewType == FILE){

                    final long begin = SystemClock.elapsedRealtime();
                    String filePathIfExists =  FolderHelper.isFilePresent(vm.getContext(), msg.getFileElem().getUuid(), FILE);
                    System.out.println("FILE CHECK: " + filePathIfExists);
                    DownloadImpl.with(vm.getContext().getApplicationContext())
                        // FOR FILE
                        .url(msg.getFileElem().getSourceUrl())
                        .enqueue(new DownloadListenerAdapter() {
                            @Override
                            public void onProgress(String url, long downloaded, long length, long usedTime) {
                                super.onProgress(url, downloaded, length, usedTime);
                            }

                            @Override
                            public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
                                Log.i("TAG", " path:" + path + " url:" + url + " length:" + new File(path.getPath()).length());
                                Log.i("TAG", " DownloadImpl time:" + (SystemClock.elapsedRealtime() - begin) + " length:" + new File(path.getPath()).length());

                                io.openim.android.ouicore.utils.DownloadMedia.downloadFiles(vm.getContext(), path, msg.getFileElem().getUuid(), new DownloadMedia.PercentAction() {
                                    @Override
                                    public void onPercentUpdate(int percent) {
                                        System.out.println("PERCENT COMPLETED: " + percent);
                                    }

                                    @Override
                                    public void onCompleted(String completed) {
                                        System.out.println(completed);
                                    }
                                });

                                File fileInput = new File(path.getPath());
                                if(fileInput.exists()){
                                    System.out.println("FILE EXISTS");
                                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                    StrictMode.setVmPolicy(builder.build());
                                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                    intentShareFile.setType("application/pdf");
                                    intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(vm.getContext(),
                                        "io.openim.android.ouiconversation.provider", fileInput));
//                                    intentShareFile.putExtra(Intent.EXTRA_STREAM, path.getPath());
//                                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
                                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                                    List<ResolveInfo> resInfoList = vm.getContext().getPackageManager().queryIntentActivities(intentShareFile, PackageManager.MATCH_DEFAULT_ONLY);
                                    for (ResolveInfo resolveInfo : resInfoList) {
                                        String packageName = resolveInfo.activityInfo.packageName;
                                        vm.getContext().grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    }
                                    vm.getContext().startActivity(Intent.createChooser(intentShareFile, "Share File"));
                                }else{
                                    System.out.println("FILE DOES NOT EXISTS");
                                }
                                return super.onResult(throwable, path, url, extra);
                            }
                        });
                }
            }
        });

        animation(copyTV, revokeTV, deleteTV, replyTV, forwardTV, shareTV, selectTV);

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.CENTER;
        wmlp.x = (int) itemView.getX();   //x position
        wmlp.y = (int) itemView.getY() - 40;   //y position
        wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
    }

    private void animation(TextView copyTV, TextView revokeTV, TextView deleteTV, TextView replyTV, TextView forwardTV, TextView shareTV, TextView selectTV) {
        copyTV.setAlpha(0);
        forwardTV.setAlpha(0);
        deleteTV.setAlpha(0);
        replyTV.setAlpha(0);
        revokeTV.setAlpha(0);
        shareTV.setAlpha(0);
        selectTV.setAlpha(0);
        copyTV.animate().alpha(1).setDuration(100).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                deleteTV.animate().alpha(1).setDuration(100).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        replyTV.animate().alpha(1).setDuration(100).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                revokeTV.animate().alpha(1).setDuration(100).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        forwardTV.animate().alpha(1).setDuration(100).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                shareTV.animate().alpha(1).setDuration(100).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        selectTV.animate().alpha(1).setDuration(100).setInterpolator(new DecelerateInterpolator()).start();
                                                    }
                                                }).start();
                                            }
                                        }).start();
                                    }
                                }).start();
                            }
                        }).start();
                    }
                }).start();

            }
        }).start();
    }

    private static String getBytesToMBString(long bytes){
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }

}
