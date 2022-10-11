package io.bytechat.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class UtilsFunctions {
    public static Phonenumber.PhoneNumber phoneNumber = null;
    public static PhoneNumberUtil phoneUtil;

    public static boolean isValidPhoneNumber( String phoneNumberString , String areaCode ) {
        try {
            phoneNumber = phoneUtil.parse( phoneNumberString, areaCode);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.getMessage());
        }
        if(phoneNumberString.charAt(0) == '0')
            return false ;
        boolean isValid = phoneUtil.isValidNumber(phoneNumber); // returns true
        return isValid;
    }
    public static void writeSharedPreferences(String name , String value , Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences("ByteChatApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(name, value);
        editor.commit();
    }
    public static String readSharedPreferences(String name ,  Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences("ByteChatApp", Context.MODE_PRIVATE);
        return sharedpreferences.getString(name,"");
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
