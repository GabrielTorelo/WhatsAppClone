package com.gabrieltorelo.whatsappclone.service;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataTimeService {

    public static String getCurrentDate(){
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        return formatter.format(date);
    }

    public static String getCurrentTime(){
        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");

        return df.format(currentDateTime.getTime());
    }

    public static String getBeforeDate(){
        Date date = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        return formatter.format(calendar.getTime());
    }

    public static String getNextDate(int day){
        Date date = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, + day);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        return formatter.format(calendar.getTime());
    }

    public static String getNextHour(int hour){
        Calendar currentDateTime = Calendar.getInstance();
        currentDateTime.add(Calendar.HOUR_OF_DAY, + hour);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        return formatter.format(currentDateTime.getTime());
    }
}
