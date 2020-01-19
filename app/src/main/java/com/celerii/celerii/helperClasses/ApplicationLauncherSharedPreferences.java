package com.celerii.celerii.helperClasses;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DELL on 8/14/2018.
 */

public class ApplicationLauncherSharedPreferences {
    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    static final String launcher_activity = "SetupSchoolList";

    public ApplicationLauncherSharedPreferences(Context context) {
        this.context = context;
        prefs = this.context.getSharedPreferences("AltariiLauncherPreferences", 0);
        editor = prefs.edit();
    }

    public String getLauncherActivity() {
        return prefs.getString(launcher_activity, "Home");
    }

    public void setLauncherActivity(String launcherActivity) {
        editor.putString(launcher_activity, launcherActivity);
        editor.commit();
    }

    public void deleteLauncherActivity() {
        editor.remove(launcher_activity);
        editor.commit();
    }
}
