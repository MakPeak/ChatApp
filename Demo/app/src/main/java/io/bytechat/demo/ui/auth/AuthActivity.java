package io.bytechat.demo.ui.auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import io.bytechat.demo.R;
import io.bytechat.demo.utils.UtilsFunctions;
import io.bytechat.demo.vm.AuthViewModel;

public class AuthActivity extends AppCompatActivity {
    public static AuthViewModel authVM = new AuthViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        bindVM(AuthViewModel.class,true);
        super.onCreate(savedInstanceState);
//        bindViewDataBinding(ActivityAuthBinding.inflate(getLayoutInflater()));
        UtilsFunctions.phoneUtil = PhoneNumberUtil.getInstance(this);
        setContentView(R.layout.activity_auth);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            Uri uri =  data.getData() ;
//            imgProfile.setImageURI(fileUri);
            authVM.profileURI.setValue(data.getData()) ;
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
