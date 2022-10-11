package io.openim.android.ouicore.utils;

import android.content.Context;
import android.os.Build;

import java.io.File;

public class FolderHelper {

    public static String pictures = "/PICTURES";
    public static String videos = "/VIDEOS";
    public static String files = "/FILES";

    public static void createFoldersInInternalStorage(Context context) {

        String getPackagePathForPictures = getPackagePath(context);
        String getPackagePathForVideos = getPackagePath(context);
        String getPackagePathForFiles = getPackagePath(context);

//        /storage/emulated/0/Android/media/io.bytechat.demo/PICTURES
        File createPicturesDirectory = new File(getPackagePathForPictures + pictures);
        if (!createPicturesDirectory.exists()) {
            createPicturesDirectory.mkdirs();
        }

//        /storage/emulated/0/Android/media/io.bytechat.demo/VIDEOS
        File createVideosDirectory = new File(getPackagePathForVideos + videos);
        if (!createVideosDirectory.exists()) {
            createVideosDirectory.mkdirs();
        }

//        /storage/emulated/0/Android/media/io.bytechat.demo/FILES
        File createFilesDirectory = new File(getPackagePathForFiles + files);
        if (!createFilesDirectory.exists()) {
            createFilesDirectory.mkdirs();
        }

        System.out.println("FOLDERS CREATED: " + createPicturesDirectory.getAbsolutePath() + createVideosDirectory.getAbsolutePath() + createFilesDirectory.getAbsolutePath());
    }

    public static File getPicturesStoragePath(Context context) {
        String getPackagePathForPictures = getPackagePath(context);
        return new File(getPackagePathForPictures + pictures);
    }

    public static File getVideosStoragePath(Context context) {
        String getPackagePathForVideos = getPackagePath(context);
        return new File(getPackagePathForVideos + videos);
    }

    public static File getFilesStoragePath(Context context) {
        String getPackagePathForFiles = getPackagePath(context);
        return new File(getPackagePathForFiles + files);
    }

    public static String getPackagePath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            File[] directory = new File[0];
            directory = context.getExternalMediaDirs();
            for(int i = 0; i < directory.length; i++){
                if(directory[i].getName().contains(context.getPackageName())){
                    return directory[i].getAbsolutePath();
                }
            }
        }
        return null;
    }

    public static String isPicturePresent(Context context, String UUID, int TYPE) {
        String path = FolderHelper.getPicturesStoragePath(context) + "/" + UUID;
        File file = new File(path);
        if(file.exists()){
            return path;
        } else {
            return "";
        }
    }

    public static String isVideoPresent(Context context, String UUID, int TYPE) {
        String path = FolderHelper.getVideosStoragePath(context) + "/" + UUID;
        File file = new File(path);
        if(file.exists()){
            return path;
        } else {
            return "";
        }
    }

    public static String isFilePresent(Context context, String UUID, int TYPE) {
        String path = FolderHelper.getFilesStoragePath(context) + "/" + UUID;
        File file = new File(path);
        if(file.exists()){
            return path;
        } else {
            return "";
        }
    }
}
