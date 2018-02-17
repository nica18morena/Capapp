package com.example.jessi.pictovent;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    //TODO: add runtime permissions
    private int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR, Manifest.permission.CAMERA};
    private static final String TAG = MainActivity.class.getSimpleName();
    private final int CLICK_PHOTO = 1;
    private long CAL_ID;
    private Scheduler scheduler;
    private static final String FILE_LOCATION = Environment.getExternalStorageDirectory().toString() + "/Pictovent/";
    private static int scaleFactor;
    private ImageView mImageView;
    private TextView mTextView;
    private Button mSnapButton;
    private String errorMsg;
    private Uri fileUri;
    Mat src, srcOrig;

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    //DO YOUR WORK/STUFF HERE
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        //Calendar instance
        Context context = this;
        scheduler = new Scheduler(context);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mSnapButton = (Button) findViewById(R.id.snap_image);
        mSnapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                errorMsg = null;
                File imageFolder = new File(FILE_LOCATION);
                imageFolder.mkdirs();
                File image = new File (imageFolder, "image_pictoevent.jpg");
                fileUri = Uri.fromFile(image);
                Log.d(TAG, "File URI = " + fileUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                //Start the image capture intent
                startActivityForResult(intent, CLICK_PHOTO);
            }
        });
        mTextView = (TextView) findViewById(R.id.ocr_textview);
    }
    private boolean hasPermissions(Context _context, String... _permissions){
        if (_context != null && _permissions != null) {
            for (String permission : _permissions) {
                if (ActivityCompat.checkSelfPermission(_context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private void checkPermissions(){
        if(!this.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent){
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Log.d(TAG, requestCode + " " + CLICK_PHOTO + " " + resultCode + " " + RESULT_OK);

        if(resultCode == RESULT_OK){
            try {
                Log.d(TAG, fileUri.toString());
                final InputStream imageStream = getContentResolver().openInputStream(fileUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                srcOrig = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);
                src = new Mat();
                Utils.bitmapToMat(selectedImage, srcOrig);

                scaleFactor = calcScaleFactor(srcOrig.rows(), srcOrig.cols());
                Imgproc.resize(srcOrig, src, new Size(srcOrig.cols()/scaleFactor, srcOrig.rows()/scaleFactor));

                new GetPage().execute();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public class GetPage extends AsyncTask<Void, Void, String> {
            ProgressDialog dialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(MainActivity.this, "Creating calendar event", "Please Wait");
            }
            @Override
            protected String/*Bitmap*/ doInBackground(Void... params) {

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
                OCRProcessing mOcr = new OCRProcessing(MainActivity.this);
                mOcr.prepareTesseract();

                String text = mOcr.extractText(bitmap_binary);
                //Temp code to continue programming and testing
                String temp_text = "Friday, January 26, 2018 2:00pm (30 minutes)";

                //Call CalDictionary class
                CalDictionary dictionary = new CalDictionary(temp_text);
                dictionary.parseText();
                //Call to create an event
                this.checkCalendars();
                this.createCalendar(dictionary);
                return text;
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
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "An event was successfully created", Toast.LENGTH_LONG).show();
                if(_text!=null) {
                    mTextView.setText(_text);
                } else if (errorMsg != null){
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
            private Bitmap createMatToBitmap(Mat _mat){
                Bitmap bitmap = Bitmap.createBitmap(_mat.cols(), _mat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(_mat, bitmap);

                return bitmap;
            }
            private void storeImage(Bitmap _bitmap, String _fileName){
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
            private void checkCalendars(){
                Cursor calCursor;
                String[] projection =
                        new String[]{
                                CalendarContract.Calendars._ID,
                                CalendarContract.Calendars.NAME,
                                CalendarContract.Calendars.ACCOUNT_NAME,
                                CalendarContract.Calendars.ACCOUNT_TYPE};
                try {
                    calCursor = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI,
                            projection,
                            CalendarContract.Calendars.VISIBLE + " = 1",
                            null,
                            CalendarContract.Calendars._ID + " ASC");
                    if (calCursor.moveToFirst()) {
                        do {
                            long id = calCursor.getLong(0);
                            String displayName = calCursor.getString(1);
                            Log.i(TAG, "Calendar names: " + displayName + "," + id);

                            if(!displayName.isEmpty() && displayName.equals("Pictoevent events")){
                                CAL_ID = id;
                                scheduler.setCalId(CAL_ID);
                            }

                        } while (calCursor.moveToNext());
                    }
                }
                catch (SecurityException e){
                    Log.e(TAG, "Error occurred: " + e.getStackTrace());
                }
            }
            private void createCalendar(CalDictionary _dictionary){
                //Create a calendar if no ID exists
                if(CAL_ID == 0){

                    CAL_ID = scheduler.createCalendar();
                    scheduler.setCalId(CAL_ID);
                }
                scheduler.createEvent(_dictionary);
            }
    }
    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this,
                mOpenCVCallBack);
    }
    static double calcWhiteDist(double r, double g, double b){
        return Math.sqrt(Math.pow(255 - r, 2) + Math.pow(255 - g, 2) + Math.pow(255 - b, 2));
    }
    static Point findIntersection(double[] line1, double[] line2) {
        double start_x1 = line1[0], start_y1 = line1[1], end_x1 = line1[2], end_y1 = line1[3], start_x2 = line2[0], start_y2 = line2[1], end_x2 = line2[2], end_y2 = line2[3];
        double denominator = ((start_x1 - end_x1) * (start_y2 - end_y2)) - ((start_y1 - end_y1) * (start_x2 - end_x2));

        if (denominator!=0)
        {
            Point pt = new Point();
            pt.x = ((start_x1 * end_y1 - start_y1 * end_x1) * (start_x2 - end_x2) - (start_x1 - end_x1) * (start_x2 * end_y2 - start_y2 * end_x2)) / denominator;
            pt.y = ((start_x1 * end_y1 - start_y1 * end_x1) * (start_y2 - end_y2) - (start_y1 - end_y1) * (start_x2 * end_y2 - start_y2 * end_x2)) / denominator;
            return pt;
        }
        else
            return new Point(-1, -1);
    }
    static boolean exists(ArrayList<Point> corners, Point pt){
        for(int i=0; i<corners.size(); i++){
            if(Math.sqrt(Math.pow(corners.get(i).x-pt.x, 2)+Math.pow(corners.get(i).y-pt.y, 2)) < 10){
                return true;
            }
        }
        return false;
    }
    static void sortCorners(ArrayList<Point> corners)
    {
        ArrayList<Point> top, bottom;

        top = new ArrayList<Point>();
        bottom = new ArrayList<Point>();

        Point center = new Point();

        for(int i=0; i<corners.size(); i++){
            center.x += corners.get(i).x/corners.size();
            center.y += corners.get(i).y/corners.size();
        }

        for (int i = 0; i < corners.size(); i++)
        {
            if (corners.get(i).y < center.y)
                top.add(corners.get(i));
            else
                bottom.add(corners.get(i));
        }
        corners.clear();

        if (top.size() == 2 && bottom.size() == 2){
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
    private static int calcScaleFactor(int rows, int cols){
        int idealRow, idealCol;
        if(rows<cols){
            idealRow = 240;
            idealCol = 320;
        } else {
            idealCol = 240;
            idealRow = 320;
        }
        int val = Math.min(rows / idealRow, cols / idealCol);
        if(val<=0){
            return 1;
        } else {
            return val;
        }
    }
}
