package io.openim.android.ouicore.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.Extra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadMedia {

    public static void downloadPicturesWithLibrary(Context context, String URL, String UUID, PercentAction percentAction) {

        final long begin = SystemClock.elapsedRealtime();
        DownloadImpl.with(context.getApplicationContext())
            .url(URL)
            .enqueue(new DownloadListenerAdapter() {
                @Override
                public void onProgress(String url, long downloaded, long length, long usedTime) {
                    super.onProgress(url, downloaded, length, usedTime);
                    int completed = 0;
                    completed = (int) (downloaded * 100 / length);
                    percentAction.onPercentUpdate(completed);
                }

                @Override
                public boolean onResult(Throwable throwable, Uri uri, String url, Extra extra) {
                    Log.i("TAG", " uri:" + uri + " url:" + url + " length:" + new File(uri.getPath()).length());
                    Log.i("TAG", " DownloadImpl time:" + (SystemClock.elapsedRealtime() - begin) + " length:" + new File(uri.getPath()).length());

                    File fileInput = new File(uri.getPath());
                    File fileOutput = new File(FolderHelper.getPicturesStoragePath(context), UUID);
                    if(fileInput.exists()){
                        InputStream inputStream = null;
                        OutputStream outputStream = null;
                        long totalBytesRead = 0;
                        int percentCompleted = 0;
                        long fileSize = 0;
                        int len = 0;
                        byte[] buf = new byte[1024];
                        try {
                            inputStream = new FileInputStream(fileInput);
                            outputStream = new FileOutputStream(fileOutput);
                            fileSize = inputStream.available();
                            while (true) {
                                assert inputStream != null;
                                if (!((len = inputStream.read(buf)) > 0)) break;
                                assert outputStream != null;
                                outputStream.write(buf, 0, len);
                                totalBytesRead += len;
                                percentCompleted = (int) (totalBytesRead * 100 / fileSize);
//                                percentAction.onPercentUpdate(percentCompleted);
//                                if(percentCompleted == 100) {
//                                    percentAction.onCompleted("Completed");
//                                }
//                    System.out.println("PERCENT COMPLETED: " + percentCompleted + " TOTAL BYTES READ: " + totalBytesRead + " FILE SIZE: " + fileSize);
                            }
                            assert outputStream != null;
                            outputStream.flush();
                            inputStream.close();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

//                        Toast.makeText(context.getApplicationContext(),"Picture saved successfully",Toast.LENGTH_LONG).show();

                    } else{
//                        Toast.makeText(context.getApplicationContext(),"Saving picture failed",Toast.LENGTH_LONG).show();
                    }

                    System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());
                    percentAction.onCompleted("completed");
                    return super.onResult(throwable, uri, url, extra);
                }
            });
    }

    public static void downloadVideosWithLibrary(Context context, String URL, String UUID, PercentAction percentAction) {

        final long begin = SystemClock.elapsedRealtime();

        DownloadImpl.with(context.getApplicationContext())
            .url(URL)
            .enqueue(new DownloadListenerAdapter() {
                @Override
                public void onProgress(String url, long downloaded, long length, long usedTime) {
                    super.onProgress(url, downloaded, length, usedTime);
                    int completed = 0;
                    completed = (int) (downloaded * 100 / length);
                    percentAction.onPercentUpdate(completed);
                }

                @Override
                public boolean onResult(Throwable throwable, Uri uri, String url, Extra extra) {
                    Log.i("TAG", " uri:" + uri + " url:" + url + " length:" + new File(uri.getPath()).length());
                    Log.i("TAG", " DownloadImpl time:" + (SystemClock.elapsedRealtime() - begin) + " length:" + new File(uri.getPath()).length());

                    File fileInput = new File(uri.getPath());
                    File fileOutput = new File(FolderHelper.getVideosStoragePath(context), UUID);
                    if(fileInput.exists()){
                        InputStream inputStream = null;
                        OutputStream outputStream = null;
                        long totalBytesRead = 0;
                        int percentCompleted = 0;
                        long fileSize = 0;
                        int len = 0;
                        byte[] buf = new byte[1024];
                        try {
                            inputStream = new FileInputStream(fileInput);
                            outputStream = new FileOutputStream(fileOutput);
                            fileSize = inputStream.available();
                            while (true) {
                                assert inputStream != null;
                                if (!((len = inputStream.read(buf)) > 0)) break;
                                assert outputStream != null;
                                outputStream.write(buf, 0, len);
                                totalBytesRead += len;
                                percentCompleted = (int) (totalBytesRead * 100 / fileSize);
//                                percentAction.onPercentUpdate(percentCompleted);
//                                if(percentCompleted == 100) {
//                                    percentAction.onCompleted("Completed");
//                                }
//                    System.out.println("PERCENT COMPLETED: " + percentCompleted + " TOTAL BYTES READ: " + totalBytesRead + " FILE SIZE: " + fileSize);
                            }
                            assert outputStream != null;
                            outputStream.flush();
                            inputStream.close();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(context.getApplicationContext(),"Video saved successfully",Toast.LENGTH_LONG).show();

                    } else{
                        Toast.makeText(context.getApplicationContext(),"Saving video failed",Toast.LENGTH_LONG).show();
                    }
                    percentAction.onCompleted("completed");

                    System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());
                    return super.onResult(throwable, uri, url, extra);
                }
            });
    }

    public static void downloadFilesWithLibrary(Context context, String URL, String UUID, PercentAction percentAction) {

        final long begin = SystemClock.elapsedRealtime();
        DownloadImpl.with(context.getApplicationContext())
            .url(URL)
            .enqueue(new DownloadListenerAdapter() {
                @Override
                public void onProgress(String url, long downloaded, long length, long usedTime) {
                    super.onProgress(url, downloaded, length, usedTime);
                    int completed = 0;
                    completed = (int) (downloaded * 100 / length);
                    percentAction.onPercentUpdate(completed);
                }

                @Override
                public boolean onResult(Throwable throwable, Uri uri, String url, Extra extra) {
                    Log.i("TAG", " uri:" + uri + " url:" + url + " length:" + new File(uri.getPath()).length());
                    Log.i("TAG", " DownloadImpl time:" + (SystemClock.elapsedRealtime() - begin) + " length:" + new File(uri.getPath()).length());

                    File fileInput = new File(uri.getPath());
                    File fileOutput = new File(FolderHelper.getFilesStoragePath(context), UUID);
                    if(fileInput.exists()){
                        InputStream inputStream = null;
                        OutputStream outputStream = null;
                        long totalBytesRead = 0;
                        int percentCompleted = 0;
                        long fileSize = 0;
                        int len = 0;
                        byte[] buf = new byte[1024];
                        try {
                            inputStream = new FileInputStream(fileInput);
                            outputStream = new FileOutputStream(fileOutput);
                            fileSize = inputStream.available();
                            while (true) {
                                assert inputStream != null;
                                if (!((len = inputStream.read(buf)) > 0)) break;
                                assert outputStream != null;
                                outputStream.write(buf, 0, len);
                                totalBytesRead += len;
                                percentCompleted = (int) (totalBytesRead * 100 / fileSize);
//                                percentAction.onPercentUpdate(percentCompleted);
//                                if(percentCompleted == 100) {
//                                    percentAction.onCompleted("Completed");
//                                }
//                    System.out.println("PERCENT COMPLETED: " + percentCompleted + " TOTAL BYTES READ: " + totalBytesRead + " FILE SIZE: " + fileSize);
                            }
                            assert outputStream != null;
                            outputStream.flush();
                            inputStream.close();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(context.getApplicationContext(),"File saved successfully",Toast.LENGTH_LONG).show();

                    } else{
                        Toast.makeText(context.getApplicationContext(),"Saving file failed",Toast.LENGTH_LONG).show();
                    }

                    System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());
                    percentAction.onCompleted("completed");
                    return super.onResult(throwable, uri, url, extra);
                }
            });
    }

    public static void downloadFiles(Context context, Uri uri, String UUID, PercentAction percentAction) {

        File fileInput = new File(uri.getPath());
        File fileOutput = new File(FolderHelper.getFilesStoragePath(context), UUID);
        if(fileInput.exists()){
            InputStream inputStream = null;
            OutputStream outputStream = null;
            long totalBytesRead = 0;
            int percentCompleted = 0;
            long fileSize = 0;
            int len = 0;
            byte[] buf = new byte[1024];
            try {
                inputStream = new FileInputStream(fileInput);
                outputStream = new FileOutputStream(fileOutput);
                fileSize = inputStream.available();
                while (true) {
                    assert inputStream != null;
                    if (!((len = inputStream.read(buf)) > 0)) break;
                    assert outputStream != null;
                    outputStream.write(buf, 0, len);
                    totalBytesRead += len;
                    percentCompleted = (int) (totalBytesRead * 100 / fileSize);
                    percentAction.onPercentUpdate(percentCompleted);
                    if(percentCompleted == 100) {
                        percentAction.onCompleted("Completed");
                    }
//                    System.out.println("PERCENT COMPLETED: " + percentCompleted + " TOTAL BYTES READ: " + totalBytesRead + " FILE SIZE: " + fileSize);
                }
                assert outputStream != null;
                outputStream.flush();
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(context.getApplicationContext(),"File saved successfully",Toast.LENGTH_LONG).show();

        } else{
            Toast.makeText(context.getApplicationContext(),"Saving file failed",Toast.LENGTH_LONG).show();
        }

        System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());

    }

    public static void downloadVideos(Context context, Uri uri, String UUID, PercentAction percentAction) {

        File fileInput = new File(uri.getPath());
        File fileOutput = new File(FolderHelper.getVideosStoragePath(context), UUID);
        if(fileInput.exists()){
            InputStream inputStream = null;
            OutputStream outputStream = null;
            long totalBytesRead = 0;
            int percentCompleted = 0;
            long fileSize = 0;
            int len = 0;
            byte[] buf = new byte[1024];
            try {
                inputStream = new FileInputStream(fileInput);
                outputStream = new FileOutputStream(fileOutput);
                fileSize = inputStream.available();
                while (true) {
                    assert inputStream != null;
                    if (!((len = inputStream.read(buf)) > 0)) break;
                    assert outputStream != null;
                    outputStream.write(buf, 0, len);
                    totalBytesRead += len;
                    percentCompleted = (int) (totalBytesRead * 100 / fileSize);
                    percentAction.onPercentUpdate(percentCompleted);
                    if(percentCompleted == 100) {
                        percentAction.onCompleted("Completed");
                    }
//                    System.out.println("PERCENT COMPLETED: " + percentCompleted + " TOTAL BYTES READ: " + totalBytesRead + " FILE SIZE: " + fileSize);
                }
                assert outputStream != null;
                outputStream.flush();
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(context.getApplicationContext(),"Video saved successfully",Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(context.getApplicationContext(),"Saving video failed",Toast.LENGTH_LONG).show();
        }
        System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    public static void downloadPictures(Context context, Uri uri, String UUID, PercentAction percentAction) {

        File fileInput = new File(uri.getPath());
        File fileOutput = new File(FolderHelper.getPicturesStoragePath(context), UUID);
        if(fileInput.exists()){
            InputStream inputStream = null;
            OutputStream outputStream = null;
            long totalBytesRead = 0;
            int percentCompleted = 0;
            long fileSize = 0;
            int len = 0;
            byte[] buf = new byte[1024];
            try {
                inputStream = new FileInputStream(fileInput);
                outputStream = new FileOutputStream(fileOutput);
                fileSize = inputStream.available();
                while (true) {
                    assert inputStream != null;
                    if (!((len = inputStream.read(buf)) > 0)) break;
                    assert outputStream != null;
                    outputStream.write(buf, 0, len);
                    totalBytesRead += len;
                    percentCompleted = (int) (totalBytesRead * 100 / fileSize);
                    percentAction.onPercentUpdate(percentCompleted);
                    if(percentCompleted == 100) {
                        percentAction.onCompleted("Completed");
                    }
//                    System.out.println("PERCENT COMPLETED: " + percentCompleted + " TOTAL BYTES READ: " + totalBytesRead + " FILE SIZE: " + fileSize);
                }
                assert outputStream != null;
                outputStream.flush();
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(context.getApplicationContext(),"Picture saved successfully",Toast.LENGTH_LONG).show();

        } else{
            Toast.makeText(context.getApplicationContext(),"Saving picture failed",Toast.LENGTH_LONG).show();
        }

        System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());

    }

    public interface PercentAction {
        void onPercentUpdate(int percent);
        void onCompleted(String completed);
    }

}
