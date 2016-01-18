package com.rori.zenvo.dragscalecircleviewexample.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by yangdw on 2016/01/18.
 */
public class ExternalUtil {

    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            return true;
        }
        return false;
    }

    public static File getAlbumStorageDir(String albumName){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if(!file.mkdirs()){
        }
        return file;
    }

}
