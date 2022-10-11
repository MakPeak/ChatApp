package io.openim.android.ouiconversation.ui;

import static io.openim.android.sdk.enums.MessageType.PICTURE;
import static io.openim.android.sdk.enums.MessageType.VIDEO;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.Extra;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.openim.android.ouiconversation.adapter.MediaPreviewAdapter;
import io.openim.android.ouiconversation.adapter.ThumbMediaAdapter;
import io.openim.android.ouiconversation.databinding.ActivityPreviewConvAllMediaBinding;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouiconversation.vm.ChatHistoryVM;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.MediaStoreUtils;
import io.openim.android.ouicore.widget.ConfirmationDialog;
import io.openim.android.ouicore.widget.SavedDialog;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.SearchResult;

public class PreviewConvAllMediaActivity extends BaseActivity<ChatHistoryVM, ActivityPreviewConvAllMediaBinding>
    implements ChatHistoryVM.ViewAction {

    // TODO This reference shouldn't be held in as static or as a second VM.
    public static ChatVM parentVM;

    public static final String CONVERSATION_ID = "conversation_id";
    public static final String CLICKED_MESSAGE_ID = "clicked_message_id";

    private MediaPreviewAdapter mediaAdapter;
    private ThumbMediaAdapter thumbMediaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityPreviewConvAllMediaBinding.inflate(getLayoutInflater()));

        bindVM(ChatHistoryVM.class);

        view.backBtn.setOnClickListener(v -> finish());
        view.backText.setOnClickListener(v -> finish());
        view.more.setOnClickListener(v -> {
            BottomSheetPreviewMedia bottomSheet = new BottomSheetPreviewMedia();

            bottomSheet.setOnSaveToGalleryClicked(() -> {
                Message message = mediaAdapter.getItem(view.viewPager.getCurrentItem());
                saveMediaToGallery(message);
            });

            bottomSheet.show(getSupportFragmentManager(), null);
        });
        view.ivForward.setOnClickListener(v -> {
            Message message = mediaAdapter.getItem(view.viewPager.getCurrentItem());
            ForwardMessageActivity.forwardMsg = message;
            ForwardMessageActivity.chatVM = parentVM;
            startActivity(new Intent(this, ForwardMessageActivity.class));
        });
        view.ivDelete.setOnClickListener(v -> {
            Message message = mediaAdapter.getItem(view.viewPager.getCurrentItem());

            String mediaText;
            if (message.getContentType() == Constant.MsgType.PICTURE)
                mediaText = getString(io.openim.android.ouicore.R.string.the_image);
            else
                mediaText = getString(io.openim.android.ouicore.R.string.the_video);

            ConfirmationDialog dialog = new ConfirmationDialog(
                PreviewConvAllMediaActivity.this,
                getString(io.openim.android.ouicore.R.string.delete_confirmation_message, mediaText),
                getString(io.openim.android.ouicore.R.string.cancel),
                getString(io.openim.android.ouicore.R.string.ok)
            );
            dialog.setOnPositiveClicked((d, which) -> {
                delete(message);
                d.dismiss();
            });
            dialog.setOnNegativeClicked((d, which) -> d.dismiss());
            dialog.show();
        });

        mediaAdapter = new MediaPreviewAdapter(getSupportFragmentManager(), getLifecycle());
        view.viewPager.setAdapter(mediaAdapter);
        view.viewPager.registerOnPageChangeCallback(
            new ViewPager2.OnPageChangeCallback() {

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    updateSelectionChange(position);
                }
            }
        );

        thumbMediaAdapter = new ThumbMediaAdapter();
        view.thumbRecyclerView.setAdapter(thumbMediaAdapter);
        thumbMediaAdapter.setOnItemClickedListener(position -> view.viewPager.setCurrentItem(position));

        String convId = getIntent().getStringExtra(CONVERSATION_ID);
        vm.ConvId.setValue(convId);

        vm.searchMessage(PICTURE, VIDEO);
    }

    private void delete(Message message) {
        parentVM.deleteMessage(message, new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
            }

            @Override
            public void onSuccess(String data) {
                mediaAdapter.removeItem(message);
                thumbMediaAdapter.removeItem(message);
                updateSelectionChange(view.viewPager.getCurrentItem());
            }
        });
    }

    private void updateSelectionChange(int position) {
        view.tvCount.setText(String.format(Locale.getDefault(), "%d/%d", position + 1, thumbMediaAdapter.getItemCount()));
        thumbMediaAdapter.setSelectedPosition(position);
    }

    @Override
    public void onSuccess(SearchResult body) {
        String clickedMessageId = getIntent().getStringExtra(CLICKED_MESSAGE_ID);
        int selectedPosition = 0;

        List<Message> remoteList = body.getSearchResultItems().get(0).getMessageList();
        List<Message> list = new LinkedList<>();
        for(int i = remoteList.size() - 1; i >= 0; i--) {
            Message o = remoteList.get(i);
            list.add(o);
            if (Objects.equals(o.getClientMsgID(), clickedMessageId))
                selectedPosition = list.size() - 1;
        }
        mediaAdapter.setItems(list);
        thumbMediaAdapter.setItems(list);
        view.viewPager.setCurrentItem(selectedPosition, false);
    }

    @Override
    public void onEmpty() {
        // TODO
    }

    private void saveMediaToGallery(Message message) {
        if (message.getContentType() == Constant.MsgType.PICTURE) {
            String url = message.getPictureElem().getSourcePicture().getUrl();
            // TODO This background task should be cleared when activity is closed.
            Glide.with(vm.getContext().getApplicationContext())
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            MediaStoreUtils.insertImage(
                                getContentResolver(), resource, message.getClientMsgID(), ""
                            );
                            SavedDialog newFragment = new SavedDialog(PreviewConvAllMediaActivity.this);
                            newFragment.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }
                });
        } else if (message.getContentType() == Constant.MsgType.VIDEO) {
            String url = message.getVideoElem().getVideoUrl();

            // TODO This background task should be cleared when activity is closed.
            DownloadImpl.with(vm.getContext().getApplicationContext())
                .url(url)
                .enqueue(new DownloadListenerAdapter() {
                    @Override
                    public void onProgress(String url, long downloaded, long length, long usedTime) {
                        super.onProgress(url, downloaded, length, usedTime);
                    }

                    @Override
                    public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
                        File file = new File(path.getPath());
                        if(file.exists()) {
                            try {
                                MediaStoreUtils.addVideo(getContentResolver(), file, message.getClientMsgID());
                                SavedDialog newFragment = new SavedDialog(PreviewConvAllMediaActivity.this);
                                newFragment.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("FILE DOES NOT EXISTS");
                        }
                        file.delete();
                        return super.onResult(throwable, path, url, extra);
                    }
                });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        parentVM = null;
    }
}
