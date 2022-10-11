package io.openim.android.ouicore.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import io.openim.android.ouicore.R;

public class AvatarImage extends androidx.appcompat.widget.AppCompatImageView {
    public AvatarImage(@NonNull Context context) {
        super(context);
        setScaleType(ScaleType.CENTER_CROP);
    }

    public AvatarImage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.CENTER_CROP);
    }

    public AvatarImage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.CENTER_CROP);
    }

    public void load(String url) {
        load(url, false);
    }

    public void load(String url, boolean isGroup) {
        if (null == url || url.isEmpty() ) {
            if(!isGroup)
                this.setPadding(0,0,0,0);
            else
                this.setPadding(25,25,25,25);

            setBackgroundResource(R.drawable.background_gradient_user_chat_btn);
            setImageDrawable(getContext().getDrawable(isGroup ? R.mipmap.icon_message_group : R.mipmap.default_profilephoto));
        } else {
            this.setPadding(0,0,0,0);
            setBackgroundDrawable(null);
            Glide.with(getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.mipmap.ic_my_friend)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10))).into(this);
        }
    }
}
