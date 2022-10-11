package io.openim.android.ouicore.vm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileOutputStream;

import io.openim.android.ouicore.base.BaseViewModel;

public class AuthViewModel extends BaseViewModel {

    public MutableLiveData<Integer> counterContact = new MutableLiveData<>(0);
    public MutableLiveData<Integer> friendDotNum = new MutableLiveData<>(0);
    public MutableLiveData<Integer> groupDotNum = new MutableLiveData<>(0);

}
