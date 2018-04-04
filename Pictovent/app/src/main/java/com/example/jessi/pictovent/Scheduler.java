package com.example.jessi.pictovent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by jessi on 1/13/2018.
 */

public class Scheduler {

    private static final String TAG = "Scheduler";
    private static int PROJECTION_ID_INDEX = 0;
    private static int PROJECTION_ACCOUNT_NAME_INDEX = 0;
    private static int PROJECTION_DISPLAY_NAME_INDEX = 0;
    private static int PROJECTION_OWNER_ACCOUNT_INDEX = 0;
    /**The main/basic URI for the android calendars table*/
    private static Uri CAL_URI = null;
    private static String ACCOUNT_NAME = "com.example.jessi.pictoevent";
    private static String CALENDAR_NAME = null;
    private static long CAL_ID;
    private static long eventID;
    /**The main/basic URI for the android events table*/
     private static Uri EVENT_URI = null;
     private static Scheduler mInstance = null;
     private static ContentResolver contentResolver;

     public Scheduler(Context ctx){
         PROJECTION_ID_INDEX = 0;
         PROJECTION_ACCOUNT_NAME_INDEX = 1;
         PROJECTION_DISPLAY_NAME_INDEX = 2;
         PROJECTION_OWNER_ACCOUNT_INDEX = 3;
         /**The main/basic URI for the android calendars table*/
         CAL_URI = CalendarContract.Calendars.CONTENT_URI;
         ACCOUNT_NAME = "Pictoevent";
         CALENDAR_NAME = "Pictoevent events";
         /**The main/basic URI for the android events table*/
         EVENT_URI = CalendarContract.Events.CONTENT_URI;
         contentResolver = ctx.getContentResolver();
     }
    /**Builds the Uri for your Calendar in android database (as a Sync Adapter)*/
    private static Uri buildCalUri() {
        return CAL_URI
                .buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                        CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();
    }
    /**Creates the values the new calendar will have*/
    private static ContentValues buildNewCalContentValues() {

        final ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME);
        cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        cv.put(CalendarContract.Calendars.NAME, CALENDAR_NAME);
        cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_NAME);
        cv.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xEA8561);
        //user can only read the calendar
        cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, ACCOUNT_NAME);
        cv.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, "America/Los_Angeles");
        cv.put(CalendarContract.Calendars.VISIBLE, 1);
        cv.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        return cv;
    }
    public long createCalendar() {
        final ContentValues cv = buildNewCalContentValues();

        Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME);
        builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true");

        Uri uri = contentResolver.insert(builder.build(), cv);
        CAL_ID = Long.parseLong(uri.getLastPathSegment());

        return CAL_ID;
    }
    public void deleteCalendar_del(long _calId){//TODO:Remove this method
        //Convert long to string
        String id = Long.toString(_calId);

        Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
        //builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "com.example.jessi.pictoevent");
        builder.appendQueryParameter(CalendarContract.Calendars._ID, id);
        builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true");

        Uri uri = builder.build();
        if(uri != null){
          int numdeleted =  contentResolver.delete(uri, null, null);
        }

    }
    public void setCalId(long _calId){
        CAL_ID = _calId;
    }
    private void setEvent(String _dStart){

        //Calendar calendar = new GregorianCalendar(2017, 3, 14);
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long start;
        if(_dStart.isEmpty()){
             start  = calendar.getTimeInMillis();
        }
        else{
             start = Long.valueOf(_dStart);
            //start = calendar.getTimeInMillis();
        }
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, calendar.getTimeInMillis());//testing if time is issue- og value: start
        values.put(CalendarContract.Events.DTEND, calendar.getTimeInMillis());
        //values.put(CalendarContract.Events.RRULE, _freq);
        values.put(CalendarContract.Events.TITLE, "Event");
        values.put(CalendarContract.Events.EVENT_LOCATION, " ");
        values.put(CalendarContract.Events.CALENDAR_ID, CAL_ID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
        values.put(CalendarContract.Events.DESCRIPTION, " ");
        values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        values.put(CalendarContract.Events.SELF_ATTENDEE_STATUS, CalendarContract.Events.STATUS_CONFIRMED);
        //values.put(CalendarContract.Events.ALL_DAY, 1);
        values.put(CalendarContract.Events.ORGANIZER, "Pictoevent");
        values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 1);
        values.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        //values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        //values.put(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME);
        //values.put(CalendarContract.Calendars.ACCOUNT_TYPE,CalendarContract.ACCOUNT_TYPE_LOCAL);
        try{
            Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);//Todo:failing here
            eventID = Long.parseLong(uri.getLastPathSegment());
        }
        catch (SecurityException e){
            Log.e(TAG, "Error occurred: " + e.getStackTrace());
        }

        this.setEventID(eventID);
    }
    public void setEventID(long _eventID){
        eventID = _eventID;
    }
    public long getEventID(){
        return eventID;
    }
    private Map tokenizeTime(String _time){

        String[] temp = _time.split(":|\\s");
        Map time = new HashMap();
        time.put("Hr", temp[0]);
        time.put("Min", temp[1]);
        time.put("AM/PM", temp[2]);

        return time;
    }
    /**Builds the Uri for events (as a Sync Adapter)*/
    public static Uri buildEventUri() {
        return EVENT_URI
                .buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                        CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();
    }
    /**Finds an event based on the ID
     * @param ctx The context (e.g. activity)
     * @param id The id of the event to be found
     */
    public static void getEventByID(Context ctx, long id) {
        ContentResolver cr = ctx.getContentResolver();
        //Projection array for query (the values you want)
        final String[] PROJECTION = new String[] {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
        };
        final int ID_INDEX = 0, TITLE_INDEX = 1, DESC_INDEX = 2, LOCATION_INDEX = 3,
                START_INDEX = 4, END_INDEX = 5;
        long start_millis=0, end_millis=0;
        String title=null, description=null, location=null;
        final String selection = "("+ CalendarContract.Events.OWNER_ACCOUNT+" = ? AND "+ CalendarContract.Events._ID+" = ?)";
        final String[] selectionArgs = new String[] {ACCOUNT_NAME, id+""};
        Cursor cursor = cr.query(buildEventUri(), PROJECTION, selection, selectionArgs, null);
        //at most one event will be returned because event ids are unique in the table
        if (cursor.moveToFirst()) {
            id = cursor.getLong(ID_INDEX);
            title = cursor.getString(TITLE_INDEX);
            description = cursor.getString(DESC_INDEX);
            location = cursor.getString(LOCATION_INDEX);
            start_millis = cursor.getLong(START_INDEX);
            end_millis = cursor.getLong(END_INDEX);

            //do something with the values...

        }
        cursor.close();
    }
    public void createEvent(CalDictionary _dictionary){
        String formattedDate = "";
        String formattedMonth = "";
        String formattedDay = "";

        //Format date- YYYYMMDD
        String date = _dictionary.getStart_date();
        if(date != null){
            String[] tempDate = date.split("[/-]");
            if(tempDate.length == 3){
                formattedDate = "20" + tempDate[2] + tempDate[0] + tempDate[1];
            }
        }
        //Build a formatted date
        String month = _dictionary.getStart_month();
        String day = _dictionary.getStart_day();
        String year = _dictionary.getYear();
        //TODO: if any of these values are null- default today's date?

        if(month != null && day != null && year != null){
            switch(month) {
                case "january":
                case "jan":
                    formattedMonth = "01";
                    break;
                case "february":
                case "feb":
                    formattedMonth = "02";
                    break;
                case "march":
                case "mar":
                    formattedMonth = "03";
                    break;
                case "april":
                case "apr":
                    formattedMonth = "04";
                    break;
                case "may":
                    formattedMonth = "05";
                    break;
                case "june":
                case "jun":
                    formattedMonth = "06";
                    break;
                case "july":
                case "jul":
                    formattedMonth = "07";
                    break;
                case "august":
                case "aug":
                    formattedMonth = "08";
                    break;
                case "september":
                case "sep":
                    formattedMonth = "09";
                    break;
                case "october":
                case "oct":
                    formattedMonth = "10";
                    break;
                case "november":
                case "nov":
                    formattedMonth = "11";
                    break;
                case "december":
                case "dec":
                    formattedMonth = "12";
                    break;
            }

            switch(day){
                case "1":
                    formattedDate = "01";
                    break;
                case "2":
                    formattedDate = "02";
                    break;
                case "3":
                    formattedDate = "03";
                    break;
                case "4":
                    formattedDate = "04";
                    break;
                case "5":
                    formattedDate = "05";
                    break;
                case "6":
                    formattedDate = "06";
                    break;
                case "7":
                    formattedDate = "07";
                    break;
                case "8":
                    formattedDate = "08";
                    break;
                case "9":
                    formattedDate = "09";
                    break;
                default:
                    formattedDate = day;
                    break;
            }

            formattedDate = year + formattedMonth + formattedDay;
        }
        else{
            formattedDate = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        }

        //Format date - time (if time provided) date "T" time: 19980118T230000
        String formattedDateTime = "";
        //Format time to 24hrs
        String time = _dictionary.getStart_time();

        if(time != null){
            String[] ampm = time.split("[^a-zA-Z]");
            String[] timeTokens = time.split("[:a-zA-Z]");

            if(ampm.length > 0 ){
                switch(ampm[ampm.length - 1]){
                    case "am":
                    case "AM":
                    case "Am":
                        formattedDateTime = String.format("%sT%s%s00",formattedDate, timeTokens[0], timeTokens[1]);
                        break;
                    case "pm":
                    case "PM":
                    case "Pm":
                        int intHrs = Integer.parseInt(timeTokens[0]);
                        //formattedDateTime = String.format("%sT%d%s00",formattedDate, (intHrs + 12), timeTokens[1]);
                        formattedDateTime = String.format("%s%d%s00",formattedDate, (intHrs + 12), timeTokens[1]);
                }
            }
            else{
                //formattedDateTime = String.format("%sT%s%s00",formattedDate, timeTokens[0], timeTokens[1]);
                formattedDateTime = String.format("%s%s%s00",formattedDate, timeTokens[0], timeTokens[1]);
            }

        }
        //Any additional?
        this.setEvent(formattedDateTime);
    }
}
