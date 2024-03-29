package com.yyzh.updateapplibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;


import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class AppUtils {
    public static int getVersionCode(Context context){
        PackageManager packageManager = context.getPackageManager();
        try{
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            return packageInfo.versionCode;
//            return packageInfo.getLongVersionCode();
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 安装app
     * @param activity
     * @param apkFile
     */
    public static void installApk(Activity activity , File apkFile){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(activity, activity.getPackageName()+".fileprovider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        activity.startActivity(intent);

    }

    /**
     * 获取文件的md5
     * @param targetFile
     * @return
     */
    public static String getFileMd5(File targetFile){
        if(targetFile == null || !targetFile.isFile()){
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len = 0 ;
        try{
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(targetFile);
            while((len = in.read(buffer)) != -1){
                digest.update(buffer, 0 , len);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            if(in != null){
                try {
                    in.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        byte[] result = digest.digest();
        BigInteger bigInt = new BigInteger(1, result);
        return bigInt.toString(16);
    }
}
