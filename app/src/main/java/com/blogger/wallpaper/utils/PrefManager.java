package com.blogger.wallpaper.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.blogger.wallpaper.Constants;

public class PrefManager {
    Context _context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    public PrefManager(Context context) {
        this._context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE_STATUS, 0);
        this.pref = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    public void setBoolean(String str, Boolean bool) {
        this.editor.putBoolean(str, bool.booleanValue());
        this.editor.apply();
    }

    public void setString(String str, String str2) {
        this.editor.putString(str, str2);
        this.editor.apply();
    }

    public void setInt(String str, int i) {
        this.editor.putInt(str, i);
        this.editor.apply();
    }

    public boolean getBoolean(String str) {
        return this.pref.getBoolean(str, true);
    }

    public void remove(String str) {
        if (this.pref.contains(str)) {
            this.editor.remove(str);
            this.editor.apply();
        }
    }

    public String getString(String str) {
        return this.pref.contains(str) ? this.pref.getString(str, null) : "";
    }

    public int getInt(String str) {
        return this.pref.getInt(str, 0);
    }

    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.PREF_NIGHT_MODE, state);
        editor.apply();
    }

    public Boolean loadNightModeState() {
        return pref.getBoolean(Constants.PREF_NIGHT_MODE, false);
    }

}
