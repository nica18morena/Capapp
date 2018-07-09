package com.example.jessi.pictovent;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jessi on 1/31/2018.
 */

public class CustomOCRProcessing {
    public CustomOCRProcessing(){

    }
    public String cleanOCRString(String ocrText){

        if(ocrText == " " || ocrText.isEmpty()){
            return ocrText;
        }

        // Clean special chars
        ocrText = ocrText.replaceAll("=", ":");

        //Tokenize each word to catch some errors in OCR reading
        ArrayList<String> wordList = new ArrayList<String>();
        ArrayList<String> cleanWordsList = new ArrayList<String>();

        for(String word: ocrText.split(" ")) {
            wordList.add(word);
        }

        Iterator<String> iter = wordList.iterator();
        String tmpWord;
        while(iter.hasNext()){
            tmpWord = iter.next();
            if(tmpWord.length() == 2){
                //assuming 2 char word is supposed to be digits
                tmpWord = tmpWord.replaceAll("l", "1");
                tmpWord = tmpWord.replaceAll("Z|z", "2");
                tmpWord = tmpWord.replaceAll("S|s", "5");
            }
            cleanWordsList.add(tmpWord);
        }

        return TextUtils.join(" ", cleanWordsList);

    }
}
