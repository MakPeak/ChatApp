package io.bytechat.demo.oldrelease.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

import io.openim.android.ouicore.utils.SharedPreferencesUtil;

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    // the method is used to set the language at runtime
    public static Context setLocale(Context context, String language) {
        persist(context, language);

        // updating the language for devices above android nougat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }
        // for devices having lower version of android os
        return updateResourcesLegacy(context, language);
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    public static void settingLanguage(Context context) {
        // Setting Language
        String language = SharedPreferencesUtil.get(context).getString("language");
        if(language.equalsIgnoreCase(context.getResources().getString(io.openim.android.ouicore.R.string.follow_the_system))) {
            if(Locale.getDefault().getLanguage().equalsIgnoreCase("en") || Locale.getDefault().getLanguage().equalsIgnoreCase("zh")){
                language = Locale.getDefault().getLanguage();
            } else{
                language = "en";
            }
            language = Locale.getDefault().getLanguage();
        } else if(language.equalsIgnoreCase(context.getResources().getString(io.openim.android.ouicore.R.string.english))){
            language = "en";
        } else if(language.equalsIgnoreCase(context.getResources().getString(io.openim.android.ouicore.R.string.simplified_chinese))){
            language = "zh";
        } else if(language.equalsIgnoreCase("")) {
            language = Locale.getDefault().getLanguage();
        }
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    // the method is used update the language of application by creating
    // object of inbuilt Locale class and passing language argument to it
    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }


    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}

