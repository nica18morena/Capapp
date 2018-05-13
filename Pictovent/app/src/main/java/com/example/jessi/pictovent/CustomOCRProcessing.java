package com.example.jessi.pictovent;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jessi on 1/31/2018.
 */

public class CustomOCRProcessing {
    String ocrString;
    List<String> wordList;
    Pattern matchYear_p, matchYearDigits_p;
    ArrayList<String> cleanWordsList;
    public CustomOCRProcessing(String ocr){
        this.ocrString = ocr;
        wordList = new ArrayList<String>();
        this.populateList();
        this.initiatePatterns();
    }
    private void initiatePatterns(){
        String matchYear = "[A-Za-z0-9]{3,4}";
        String matchDigits = "[0-9]{1,}";
        matchYear_p = Pattern.compile(matchYear);
        matchYearDigits_p = Pattern.compile(matchDigits);
    }
    private void populateList(){
        for(String word: ocrString.split(" ")){
            wordList.add(word);
        }
    }
    private String cleanStringDigitsYear(String word){

        Matcher potentialYear = matchYear_p.matcher(word);
        if(potentialYear.matches() && word.length() <= 4){
            Matcher containsDigits = matchYearDigits_p.matcher(word);
            if(containsDigits.matches()){
                word = word.replaceAll("o|O","0");
                word = word.replaceAll("B", "18");
            }
        }

        return word;
    }

    private String cleanSpecialCharacters(String word){

        word = word.replaceAll("=",":");

        return word;
    }
    public String getCleanedString(){
        cleanOCRString();
        String cleanedString = TextUtils.join(" ", cleanWordsList);
        return cleanedString;
    }
    private void cleanOCRString(){
        cleanWordsList = new ArrayList<>();
        Iterator<String> iter = wordList.iterator();
        String tmpWord;
        while(iter.hasNext()){
            tmpWord = cleanStringDigitsYear(iter.next());
            tmpWord = cleanSpecialCharacters(tmpWord);
            cleanWordsList.add(tmpWord);
        }
    }

}
