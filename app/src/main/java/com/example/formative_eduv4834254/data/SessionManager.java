package com.example.formative_eduv4834254.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "tb_session";

    private static final String K_LOGGED = "logged_in";
    private static final String K_EMAIL = "email";
    private static final String K_TRIP_COUNT = "trip_count";

    // Last trip selections
    private static final String K_LAST_ACTIVITIES = "last_activities"; // comma-separated
    private static final String K_LAST_VISA = "last_visa";
    private static final String K_LAST_TRANSPORT = "last_transport";
    private static final String K_LAST_MEALS = "last_meals";
    private static final String K_LAST_CUSTOM = "last_custom";

    // Settings
    private static final String K_THEME_DARK = "theme_dark";
    private static final String K_MUSIC_ENABLED = "music_enabled";
    private static final String K_LANGUAGE = "language";

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    // Session
    public static void login(Context c, String email) {
        prefs(c).edit().putBoolean(K_LOGGED, true).putString(K_EMAIL, email).apply();
    }
    public static void logout(Context c) { prefs(c).edit().putBoolean(K_LOGGED, false).apply(); }
    public static boolean isLoggedIn(Context c) { return prefs(c).getBoolean(K_LOGGED, false); }
    public static String getEmail(Context c) { return prefs(c).getString(K_EMAIL, ""); }

    // Trip count
    public static void incrementTripCount(Context c) {
        int n = prefs(c).getInt(K_TRIP_COUNT, 0) + 1;
        prefs(c).edit().putInt(K_TRIP_COUNT, n).apply();
    }
    public static int getTripCount(Context c) { return prefs(c).getInt(K_TRIP_COUNT, 0); }

    // Last trip selections
    public static void saveLastTrip(Context c, String activitiesCsv, double visa, double transport, double meals, double custom) {
        prefs(c).edit()
                .putString(K_LAST_ACTIVITIES, activitiesCsv)
                .putFloat(K_LAST_VISA, (float) visa)
                .putFloat(K_LAST_TRANSPORT, (float) transport)
                .putFloat(K_LAST_MEALS, (float) meals)
                .putFloat(K_LAST_CUSTOM, (float) custom)
                .apply();
    }
    public static String getLastActivitiesCsv(Context c) { return prefs(c).getString(K_LAST_ACTIVITIES, ""); }
    public static float getLastVisa(Context c) { return prefs(c).getFloat(K_LAST_VISA, 0f); }
    public static float getLastTransport(Context c) { return prefs(c).getFloat(K_LAST_TRANSPORT, 0f); }
    public static float getLastMeals(Context c) { return prefs(c).getFloat(K_LAST_MEALS, 0f); }
    public static float getLastCustom(Context c) { return prefs(c).getFloat(K_LAST_CUSTOM, 0f); }

    // Settings
    public static void setDarkTheme(Context c, boolean dark) { prefs(c).edit().putBoolean(K_THEME_DARK, dark).apply(); }
    public static boolean isDarkTheme(Context c) { return prefs(c).getBoolean(K_THEME_DARK, false); }
    public static void setMusicEnabled(Context c, boolean on) { prefs(c).edit().putBoolean(K_MUSIC_ENABLED, on).apply(); }
    public static boolean isMusicEnabled(Context c) { return prefs(c).getBoolean(K_MUSIC_ENABLED, true); }
    public static void setLanguage(Context c, String lang) { prefs(c).edit().putString(K_LANGUAGE, lang).apply(); }
    public static String getLanguage(Context c) { return prefs(c).getString(K_LANGUAGE, "en"); }
}
