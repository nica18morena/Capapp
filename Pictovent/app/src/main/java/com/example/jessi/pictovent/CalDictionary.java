package com.example.jessi.pictovent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jessi on 1/1/2018.
 */

public class CalDictionary {

    //private String title;
    //private String location;
    private String start_date;// 1/2/2018
    private String start_month;//Jan
    private String start_week;//Mon
    private String start_day;//1
    private String start_time;
    private String end_date;
    private String end_month;
    private String end_week;
    private String end_day;
    private String end_time;
    private String year;
    private String duration;
    private String text;

    private Set<String> daysSet;
    private Set<String> durationSet;
    private Set<String> monthSet;
    private Set<String> weekSet;

    public CalDictionary(String _text){

        this.text = _text;
        //this.title = "New Event";
        //this.location = null;
        //this.start_monthName= null;
        this.start_date = null;//1/2/2018
        this.start_month = null;//Jan
        this.start_week = null;//Monday
        this.start_day = null;// 23
        this.start_time =  null;//3:45
        this.end_date = null;
        this.end_month = null;
        this.end_week = null;
        this.end_day = null;
        this.end_time = null;
        this.year = null;//2018
        this.duration = null;
    }

    private void initializeWeeksSet(){
        weekSet = new HashSet<String>();
        weekSet.add("monday");
        weekSet.add("tuesday");
        weekSet.add("wednesday");
        weekSet.add("thursday");
        weekSet.add("friday");
        weekSet.add("saturday");
        weekSet.add("sunday");
    }
    private void initializeDurationSet(){
        durationSet = new HashSet<String>();
        durationSet.add("hours");
        durationSet.add("hrs");
        durationSet.add("minutes");
        durationSet.add("min");
        durationSet.add("seconds");
        durationSet.add("sec");
        durationSet.add("weekly");
        durationSet.add("biweekly");
        durationSet.add("monthly");
        durationSet.add("bimonthly");
        durationSet.add("yearly");
        durationSet.add("biyearly");
    }
    private void initializeMonthsSet(){
        monthSet = new HashSet<String>();
        monthSet.add("january");    monthSet.add("jan");
        monthSet.add("february");   monthSet.add("feb");
        monthSet.add("march");      monthSet.add("mar");
        monthSet.add("april");      monthSet.add("apr");
        monthSet.add("may");
        monthSet.add("june");       monthSet.add("jun");
        monthSet.add("july");       monthSet.add("jul");
        monthSet.add("august");     monthSet.add("aug");
        monthSet.add("september");  monthSet.add("sep");
        monthSet.add("october");    monthSet.add("oct");
        monthSet.add("november");   monthSet.add("nov");
        monthSet.add("december");   monthSet.add("dec");
    }
    private void initializeDaySet(){
        daysSet = new HashSet<String>();
        daysSet.add("1"); daysSet.add("01");
        daysSet.add("2"); daysSet.add("02");
        daysSet.add("3"); daysSet.add("03");
        daysSet.add("4"); daysSet.add("04");
        daysSet.add("5"); daysSet.add("05");
        daysSet.add("6"); daysSet.add("06");
        daysSet.add("7"); daysSet.add("07");
        daysSet.add("8"); daysSet.add("08");
        daysSet.add("9"); daysSet.add("09");
        daysSet.add("10");
        daysSet.add("11");
        daysSet.add("12");
        daysSet.add("13");
        daysSet.add("14");
        daysSet.add("15");
        daysSet.add("16");
        daysSet.add("17");
        daysSet.add("18");
        daysSet.add("19");
        daysSet.add("20");
        daysSet.add("21");
        daysSet.add("22");
        daysSet.add("23");
        daysSet.add("24");
        daysSet.add("25");
        daysSet.add("26");
        daysSet.add("27");
        daysSet.add("28");
        daysSet.add("29");
        daysSet.add("30");
        daysSet.add("31");
    }
    /*private void setTitle(String _title){
        this.title = _title;
    }
    private void setLocation(String _location){
        this.location = _location;
    }*/
    private void setStart_date(String _date){
        this.start_date = _date;
    }
    private void setStart_week(String _week){
        this.start_week = _week;
    }
    private void setStart_month(String _month){
        this.start_month = _month;
    }
    private void setStart_day(String _day){
        this.start_day = _day;
    }
    private void setStart_time(String _time){
        this.start_time = _time;
    }
    private void setEnd_date(String _date){
        this.end_date = _date;
    }
    private void setEnd_week(String _week){
        this.end_week = _week;
    }
    private void setEnd_month(String _month){
        this.end_month = _month;
    }
    private void setEnd_day(String _day){
        this.end_day = _day;
    }
    private void setEnd_time(String _time){
        this.end_time = _time;
    }
    private void setYear(String _year){
        this.year = _year;
    }
    private void setDuration(String _duration){
        this.duration = _duration;
    }
    /*public String getTitle(){
        return title;
    }
    public String getLocation(){
        return location;
    }*/
    public String getStart_date(){
        return start_date;
    }
    public String getStart_week(){
        return start_week;
    }
    public String getStart_month(){
        return start_month;
    }
    public String getStart_day(){
        return start_day;
    }
    public String getStart_time(){
        return start_time;
    }
    public String getEnd_date(){
        return end_date;
    }
    public String getEnd_week(){
        return end_week;
    }
    public String getEnd_month(){
        return end_month;
    }
    public String getEnd_day(){
        return end_day;
    }
    public String getEnd_time() {
        return end_time;
    }
    public String getYear(){
        return year;
    }
    public String getDuration(){
        return duration;
    }
    public void parseText(){

        String date_pattern = "\\d{1,2}[-/]\\d{1,2}[-/]\\d{2,4}([a-zA-z]{2})?";
        String time_pattern = "\\d{1,2}[:]\\d{1,2}";
        String year_pattern = "20[1-9][0-9]";
        String day_pattern = "[0-9]{1,2}";

        Pattern regEx_date = Pattern.compile(date_pattern);
        Pattern regEx_time = Pattern.compile(time_pattern);
        Pattern regEx_year = Pattern.compile(year_pattern);
        Pattern regEx_day = Pattern.compile(day_pattern);

        //Initialize sets
        this.initializeWeeksSet();
        this.initializeDurationSet();
        this.initializeMonthsSet();
        this.initializeDaySet();

        Iterator<String> weekSetIter = weekSet.iterator();
        Iterator<String> durationSetIter = durationSet.iterator();
        Iterator<String> monthSetIter = monthSet.iterator();
        Iterator<String> daysSetIter = daysSet.iterator();

        for (String word: text.split("\\s")){
            String[] wordtemp = word.split(",");

            word = wordtemp[0];

            boolean match_date_pattern = false;
            boolean match_time_pattern = false;
            boolean match_year_pattern = false;
            boolean match_days_pattern = false;
            boolean isWeek = false;
            boolean isDuration = false;
            boolean isMonth = false;
            //Check patterns
            Matcher match_date = regEx_date.matcher(word);
            Matcher match_time = regEx_time.matcher(word);
            Matcher match_year = regEx_year.matcher(word);
            Matcher match_days = regEx_day.matcher(word);

            match_date_pattern = this.foundMatch(match_date);

            if (!match_date_pattern){
                match_time_pattern = this.foundMatch(match_time);
            }

            if (!match_date_pattern && !match_time_pattern){
                match_days_pattern = this.foundMatch(match_days);
                if (match_days_pattern && word.length() > 2){
                    match_days_pattern = false;
                }
            }

            if (!match_date_pattern && !match_time_pattern && !match_days_pattern){
                match_year_pattern = this.foundMatch(match_year);
            }
            //If no patterns have been found, then it must be a day, duration, month, or irrelevant
            if (!match_date_pattern && !match_time_pattern &&
                    !match_year_pattern && !match_days_pattern) {

                //String[] wordtemp = word.split(",");
                //Check week set
                isWeek = weekSet.contains(wordtemp[0].toLowerCase());//M-Su
                //Check duration set
                isDuration = durationSet.contains(wordtemp[0].toLowerCase());//hrs,days, etc
                //Check month set
                isMonth = monthSet.contains(wordtemp[0].toLowerCase());//Jan

                //word = wordtemp[0];
            }

            if (match_date_pattern){
                if(this.getStart_date() == null){
                    this.setStart_date(word);
                }
            }

            if (match_time_pattern){

                if(this.getStart_time() == null){
                    this.setStart_time(word);
                }
            }

            if(match_year_pattern){
                if(this.getYear() == null){
                    this.setYear(word);
                }
            }

            if(match_days_pattern){
                if(this.getStart_day() == null && this.getStart_month() != null){
                    this.setStart_day(word);
                }
            }
            if(isWeek){
                if(this.getStart_week() == null){
                    this.setStart_week(word);
                }
            }
            if(isDuration){
                if(this.getDuration() == null){
                    this.setDuration(word);
                }
            }
            if(isMonth){
                if(this.getStart_month() == null){
                    this.setStart_month(word);
                }
            }
        }//Close for loop
    }
    private boolean foundMatch(Matcher _matcher){
        boolean found_match = false;

        while(_matcher.find()){
            if(_matcher.group().length() != 0){
                found_match = true;
            }
        }
        return found_match;
    }
    private Set createPossibleMatches(Set _set, Iterator<WordObject> _iter, int _size){
        while(_iter.hasNext()){
            WordObject tmp = _iter.next();
            if(_size == tmp.getSize()){
                _set.add(tmp);
            }
        }
        return _set;
    }
}
