package com.example.jessi.pictovent;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by jessi on 1/13/2018.
 */

public class ImagePreProcess {

    private static int scaleFactor;
    private static final String FILE_LOCATION = Environment.getExternalStorageDirectory().toString() + "/Pictoevent/";
    private long CAL_ID;
    private Scheduler scheduler;
    Mat src, srcOrig;
    Context context;
    private Uri fileUri;
    private Bitmap selectedImage;

    public ImagePreProcess(Context _context){
        this.context = _context;
        scheduler = new Scheduler(context);
    }

    public class GetPage extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "Creating calendar event", "Please Wait");
        }

        @Override
        protected String/*Bitmap*/ doInBackground(Void... params) {
        /**
         * Tess API stuff here
         *ok- didnt reach here yet
         */
           /* TessBaseAPI baseAPI = new TessBaseAPI();
            ocrBitmap();
            baseAPI.init(fileUri.toString(), "eng");
            baseAPI.setImage(selectedImage);
            String text = baseAPI.getUTF8Text();
            baseAPI.end();
            //End TESS*/

                /*Equalize the image histogram*/
          Mat dst = new Mat();
            //Convert to grey
            Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
            //Save grey image
            Bitmap bitmap_grey = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(src, bitmap_grey);
            storeImage(bitmap_grey, "image_grey.jpg");

            //Apply histogram Equalization
            Imgproc.equalizeHist(src, dst);
            //Save histo'ed image
            Bitmap bitmap_histo = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, bitmap_histo);
            storeImage(bitmap_histo, "image_histo.jpg");

                /*Use adaptive threshold to create binary image*/
           Mat bin = new Mat();
            Imgproc.adaptiveThreshold(dst, bin, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                    Imgproc.THRESH_BINARY, 15, 40);
            //Save binary image
            Bitmap bitmap_binary = Bitmap.createBitmap(bin.cols(), bin.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(bin, bitmap_binary);
            storeImage(bitmap_binary, "image_binary.jpg");
            /*Fix image warp*/
            //TODO: Algorithm for image warp

                /*OCR steps*/
            OCRProcessing mOcr = new OCRProcessing(context);
            mOcr.prepareTesseract();

            String text = mOcr.extractText(bitmap_binary);
            //Temp code to continue programming and testing
            String temp_text = "Friday, January 26, 2018 2:00pm (30 minutes)";

            //Call CalDictionary class
            CalDictionary dictionary = new CalDictionary(text);
            dictionary.parseText();
            //Call to create an event
            this.checkCalendars();
            this.createCalendar();
            scheduler.createEvent(dictionary);
            return scheduler.getEvent();
        }

        @Override
        protected void onPostExecute(String _text)/*(Bitmap bitmap)*/ {
                /*super.onPostExecute(bitmap);
                dialog.dismiss();
                if(bitmap!=null) {
                    mImageView.setImageBitmap(bitmap);
                } else if (errorMsg != null){
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }*/
            super.onPostExecute(_text);
            final String  createdEvent = _text;
            dialog.dismiss();
            dialog = null;
            final Activity activity = (Activity) context;
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, createdEvent, Toast.LENGTH_LONG).show();
                    }
                });
            }



            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Create the new dialog
                        EventDialog eventDialog = new EventDialog();

                        //Create the bundle
                        Bundle bundle = new Bundle();
                        bundle.putString("event", createdEvent);
                        eventDialog.setArguments(bundle);

                        //Create the fragment manager and show the dialog
                        FragmentActivity fActivity = (FragmentActivity) context;
                        android.support.v4.app.FragmentManager fm = fActivity.getSupportFragmentManager();
                        //android.support.v4.app.FragmentManager fm = activity.getFragmentManager();
                        String tag = "EventDialog";
                        eventDialog.show(fm, tag);//TODO: figure out how to call to show the fragment
                    }
                });
            }
           /* new Handler(Looper.getMainLooper()).post(new Runnable(){
                public void run(){
                    Toast.makeText(context, "An event was successfully created", Toast.LENGTH_LONG).show();
                }
            });*/
            //Toast.makeText(context, "An event was successfully created", Toast.LENGTH_LONG).show();
                /*if(_text!=null) {
                    mTextView.setText(_text);
                } else if (errorMsg != null){
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }*///This block isnt needed since there is no text view anymore
        }

        private Bitmap createMatToBitmap(Mat _mat) {
            Bitmap bitmap = Bitmap.createBitmap(_mat.cols(), _mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(_mat, bitmap);

            return bitmap;
        }

        private void storeImage(Bitmap _bitmap, String _fileName) {
            File pictureFile = new File(FILE_LOCATION, _fileName);
            if (pictureFile == null) {
                Log.d(TAG,
                        "Error creating media file, check storage permissions: ");// e.getMessage());
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                _bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }

        private void checkCalendars() {
            Cursor calCursor;
            String[] projection =
                    new String[]{
                            CalendarContract.Calendars._ID,
                            CalendarContract.Calendars.NAME,
                            CalendarContract.Calendars.ACCOUNT_NAME,
                            CalendarContract.Calendars.ACCOUNT_TYPE};
            try {
                calCursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI,
                        projection,
                        CalendarContract.Calendars.VISIBLE + " = 1",
                        null,
                        CalendarContract.Calendars._ID + " ASC");
                if (calCursor.moveToFirst()) {
                    do {
                        long id = calCursor.getLong(0);
                        String displayName = calCursor.getString(1);
                        Log.i(TAG, "Calendar names: " + displayName + "," + id);

                        if (displayName != null && !displayName.isEmpty() && displayName.equals("Pictoevent events")) {
                            CAL_ID = id;
                            scheduler.setCalId(CAL_ID);
                        }

                    } while (calCursor.moveToNext());
                }
            } catch (SecurityException e) {
                Log.e(TAG, "Error occurred: " + e.getStackTrace());
            }
        }

        private void createCalendar() {
            //Create a calendar if no ID exists
            if (CAL_ID == 0) {

                CAL_ID = scheduler.createCalendar();
                scheduler.setCalId(CAL_ID);
            }
        }
    }


    private Uri getCaptureImageOutputUri(@NonNull Context context) {
        Uri outputFileUri = null;
        File getImage = Environment.getExternalStorageDirectory();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath() + "/pic.jpg"));
        }
        return outputFileUri;
    }

    public void reformatImage() {
        try {
            fileUri = getCaptureImageOutputUri(context);
            Log.d(TAG, fileUri.toString());
            final InputStream imageStream = context.getContentResolver().openInputStream(fileUri);
            /*final Bitmap */selectedImage = BitmapFactory.decodeStream(imageStream);
            srcOrig = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);
            src = new Mat();
            Utils.bitmapToMat(selectedImage, srcOrig);

            scaleFactor = calcScaleFactor(srcOrig.rows(), srcOrig.cols());
            Imgproc.resize(srcOrig, src, new Size(srcOrig.cols() / scaleFactor, srcOrig.rows() / scaleFactor));

            new GetPage().execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void ocrBitmap(){
        // _path = path to the image to be OCRed
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(fileUri.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        if (rotate != 0) {
            int w = selectedImage.getWidth();
            int h = selectedImage.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            selectedImage = Bitmap.createBitmap(selectedImage, 0, 0, w, h, mtx, false);
        }
        selectedImage = selectedImage.copy(Bitmap.Config.ARGB_8888, true);
    }
    static double calcWhiteDist(double r, double g, double b) {
        return Math.sqrt(Math.pow(255 - r, 2) + Math.pow(255 - g, 2) + Math.pow(255 - b, 2));
    }

    static Point findIntersection(double[] line1, double[] line2) {
        double start_x1 = line1[0], start_y1 = line1[1], end_x1 = line1[2], end_y1 = line1[3], start_x2 = line2[0], start_y2 = line2[1], end_x2 = line2[2], end_y2 = line2[3];
        double denominator = ((start_x1 - end_x1) * (start_y2 - end_y2)) - ((start_y1 - end_y1) * (start_x2 - end_x2));

        if (denominator != 0) {
            Point pt = new Point();
            pt.x = ((start_x1 * end_y1 - start_y1 * end_x1) * (start_x2 - end_x2) - (start_x1 - end_x1) * (start_x2 * end_y2 - start_y2 * end_x2)) / denominator;
            pt.y = ((start_x1 * end_y1 - start_y1 * end_x1) * (start_y2 - end_y2) - (start_y1 - end_y1) * (start_x2 * end_y2 - start_y2 * end_x2)) / denominator;
            return pt;
        } else
            return new Point(-1, -1);
    }

    static boolean exists(ArrayList<Point> corners, Point pt) {
        for (int i = 0; i < corners.size(); i++) {
            if (Math.sqrt(Math.pow(corners.get(i).x - pt.x, 2) + Math.pow(corners.get(i).y - pt.y, 2)) < 10) {
                return true;
            }
        }
        return false;
    }

    static void sortCorners(ArrayList<Point> corners) {
        ArrayList<Point> top, bottom;

        top = new ArrayList<Point>();
        bottom = new ArrayList<Point>();

        Point center = new Point();

        for (int i = 0; i < corners.size(); i++) {
            center.x += corners.get(i).x / corners.size();
            center.y += corners.get(i).y / corners.size();
        }

        for (int i = 0; i < corners.size(); i++) {
            if (corners.get(i).y < center.y)
                top.add(corners.get(i));
            else
                bottom.add(corners.get(i));
        }
        corners.clear();

        if (top.size() == 2 && bottom.size() == 2) {
            Point top_left = top.get(0).x > top.get(1).x ? top.get(1) : top.get(0);
            Point top_right = top.get(0).x > top.get(1).x ? top.get(0) : top.get(1);
            Point bottom_left = bottom.get(0).x > bottom.get(1).x ? bottom.get(1) : bottom.get(0);
            Point bottom_right = bottom.get(0).x > bottom.get(1).x ? bottom.get(0) : bottom.get(1);

            top_left.x *= scaleFactor;
            top_left.y *= scaleFactor;

            top_right.x *= scaleFactor;
            top_right.y *= scaleFactor;

            bottom_left.x *= scaleFactor;
            bottom_left.y *= scaleFactor;

            bottom_right.x *= scaleFactor;
            bottom_right.y *= scaleFactor;

            corners.add(top_left);
            corners.add(top_right);
            corners.add(bottom_right);
            corners.add(bottom_left);
        }
    }

    private static int calcScaleFactor(int rows, int cols) {
        int idealRow, idealCol;
        if (rows < cols) {
            idealRow = 240;
            idealCol = 320;
        } else {
            idealCol = 240;
            idealRow = 320;
        }
        int val = Math.min(rows / idealRow, cols / idealCol);
        if (val <= 0) {
            return 1;
        } else {
            return val;
        }
    }
}
