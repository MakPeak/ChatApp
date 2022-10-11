package io.openim.android.ouicore.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class Extras {

    public static void glideDownloader(Context context, String URL){
        Glide.with(context.getApplicationContext())
            .asBitmap()
            .load(URL)
            .into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Share this image");
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), resource, "", null);
                    Log.i("path",  path);
                    Uri screenshotUri = Uri.parse(path);
                    Log.i("screenshotUri", String.valueOf(screenshotUri));
                    intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                    intent.setType("image/*");
                    context.startActivity(Intent.createChooser(intent, "Share image via..."));
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    Toast.makeText(context, "Starting", Toast.LENGTH_SHORT).show();
                    super.onLoadStarted(placeholder);
                }
            });
    }
}
