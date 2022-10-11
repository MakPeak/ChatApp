package io.openim.android.ouiconversation.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.Settings;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class FloatBtnUtil {
 
    private static  int height = 0;
    private Activity mcontext;
    private ViewTreeObserver.OnGlobalLayoutListener listener;
    private View root;
    private ViewTreeObserver.OnGlobalLayoutListener mListener;
    private int distanceY;
    private ViewTreeObserver mTreeObserver;
    private ValueAnimator mValueAnimator;
 
    public FloatBtnUtil(Activity mcontext){
        this.mcontext = mcontext;
        if (height == 0){
            Display defaultDisplay = mcontext.getWindowManager().getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getSize(point);
            height = point.y;
        }
    }
 
    /**
     * @param root 视图根节点
     * @param floatview 需要显示在键盘上的View组件
     */
    public void setFloatView(View root,View floatview){
        this.root = root;	
        listener = () -> {
            Rect r = new Rect();
            mcontext.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            int heightDifference = height - (r.bottom - r.top);
            boolean isKeyboardShowing = heightDifference > height / 3;
            if(isKeyboardShowing){
                floatview.setVisibility(View.VISIBLE);
                root.scrollTo(0, heightDifference+floatview.getHeight());
            }else{
                root.scrollTo(0, 0);
                floatview.setVisibility(View.GONE);
            }
        };
        root.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }
 
    public void clearFloatView(){
        if (listener != null && root != null) {
            root.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    public boolean checkPermission(Activity activity, int request_code, View view) {
        int result = 0;
        switch (request_code) {
            case 1:
                result = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            default:
                break;
        }

        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermission(activity, request_code, view);
            return false;
        }
    }

    public void requestPermission(final Activity activity, int request_code, View view) {
        switch (request_code) {
            case 1:
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Storage permission allows us to save pdf.", Snackbar.LENGTH_LONG).setAction("Allow", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(activity, activity.getString(R.string.permission_request_message), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        }
                    }).show();

                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_code);
                }
                break;
            default:
                break;
        }
    }
 
}
