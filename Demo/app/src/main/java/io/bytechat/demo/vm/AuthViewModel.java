package io.bytechat.demo.vm;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

import java.io.File;
import java.io.FileOutputStream;

import io.bytechat.demo.R;
import io.bytechat.demo.oldrelease.vm.LoginVM;
import io.bytechat.demo.ui.auth.BottomSheetAvatarFragment;
import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.base.IView;

public class AuthViewModel extends BaseViewModel {
    public MutableLiveData<Uri>  profileURI = new MutableLiveData<>();
    public MutableLiveData<Boolean>  openAvatar = new MutableLiveData<>();
    public MutableLiveData<String>  phoneNumber = new MutableLiveData<>();
    public MutableLiveData<String>  areaCode = new MutableLiveData<>();
    public MutableLiveData<String> verificationCode = new MutableLiveData<>();
    public MutableLiveData<Integer> usedFor = new MutableLiveData<>();
    public MutableLiveData<String> userId = new MutableLiveData<>();
    public MutableLiveData<String> token = new MutableLiveData<>();
    public MutableLiveData<String> faceURL = new MutableLiveData<>();
    public MutableLiveData<String> nickname = new MutableLiveData<>();
    public MutableLiveData<File> avatar = new MutableLiveData<>();
    public MutableLiveData<String> search = new MutableLiveData<>("");
    public MutableLiveData<String> deleteRes = new MutableLiveData<>("");
    public MutableLiveData<String> saveRes = new MutableLiveData<>("");

    public void checkAndSendDefaultImage(Context context){

        if(authVM.profileURI.getValue() == null){
            try {
                System.out.println("checkAndSendDefaultImage");
                Bitmap bm = BitmapFactory.decodeResource( context.getResources(), R.mipmap.default_profilephoto);
//                File outputDir = this.getContext().getCacheDir(); // context being the Activity pointer
//                File outputFile = File.createTempFile("ic_avatar_1", ".png", outputDir);
//
//                File file = outputFile;
//                FileOutputStream outStream = new FileOutputStream(file);
//                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//                outStream.flush();
//                outStream.close();
                String extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                File file = new File(extStorageDirectory, "ic_avatar_2.jpg");
                FileOutputStream outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
                authVM.profileURI.setValue(Uri.fromFile(file));
                authVM.avatar.setValue(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
