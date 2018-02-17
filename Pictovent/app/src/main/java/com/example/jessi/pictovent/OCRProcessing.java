package com.example.jessi.pictovent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jessi on 12/13/2017.
 */

public class OCRProcessing {
    private static final String TAG = OCRProcessing.class.getSimpleName();
    private TessBaseAPI tessBaseAPI;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/TesseractSample/";
    private static final String TESSDATA = "tessdata";
    private Context activityContext;
    private static final String lang = "eng";

    public OCRProcessing(Context _context){
        activityContext = _context;
    }

    public String extractText(Bitmap _bitmap){
        try {
            tessBaseAPI = new TessBaseAPI();
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
            if (tessBaseAPI == null){
                Log.e(TAG, "TessBaseAPI is null.");
            }
        }
        tessBaseAPI.init(DATA_PATH, lang);
        Log.d(TAG, "training file loaded");
        tessBaseAPI.setImage(_bitmap);
        String extractedText = "empty result";
        try{
            extractedText = tessBaseAPI.getUTF8Text();
        }catch (Exception e){
            Log.e(TAG, "Error in recognizing text.");
        }
        tessBaseAPI.end();
        return extractedText;
    }

    public void prepareTesseract(){
        try{
            prepareDirectory(DATA_PATH + TESSDATA);
        } catch (Exception e){
            e.printStackTrace();
        }
        copyTessDataFiles(TESSDATA);
    }

    private void prepareDirectory(String _path){
        File dir = new File(_path);
        if(!dir.exists()){
            if(!dir.mkdir()){
                Log.e(TAG, "ERROR: Creation of directory " + _path + " failed, check Android Manifest");
            }
        } else{
            Log.i(TAG, "Created directory " + _path);
        }
    }

    private void copyTessDataFiles(String _path) {
        try{
            String fileList[] = activityContext.getAssets().list(_path);

            for(String fileName : fileList){
                String pathToDataFile = DATA_PATH + _path + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()){
                    InputStream in = activityContext.getAssets().open(_path + "/" + fileName);

                    OutputStream out = new FileOutputStream(pathToDataFile);

                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    Log.d(TAG, "Copied " + fileName + "to tessdata");
                }
            }
        }catch (IOException e) {
            Log.e(TAG, "Unable to copy files to tessdata " + e.toString());
        }
    }


}
