package com.android.watercolor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Evgeniy on 02.11.2016.
 */

public class SharedPreferencesStub {

    public static void saveData(Context con, String variable, int data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con);
        prefs.edit().putInt(variable, data).apply();
    }

    public static int getData(Context con, String variable, int defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con);
        return prefs.getInt(variable, defaultValue);
    }
}
