package io.zentechgh.dms.mobile.app.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SavedSharePreference {

    static final String PREF_UID = "uid";
    static final String PREF_EMAIL = "email";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUid(Context ctx, String uid)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_UID, uid);
        editor.apply();
    }

    public static String getUid(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_UID, "");
    }

    public static void setEmail(Context ctx, String email)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_EMAIL, email);
        editor.apply();
    }

    public static String getEmail(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_EMAIL, "");
    }

}
