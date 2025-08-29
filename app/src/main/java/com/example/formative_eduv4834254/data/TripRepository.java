package com.example.formative_eduv4834254.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TripRepository {
    private static final String PREF = "trip_repo";
    private static final String KEY_TRIPS = "trips";
    private static final String KEY_MEMORIES = "memories";
    private static final Gson gson = new Gson();
    private static final Type LIST_TYPE = new TypeToken<ArrayList<Trip>>(){}.getType();
    private static final Type MEM_LIST_TYPE = new TypeToken<ArrayList<Memory>>(){}.getType();

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static List<Trip> getTrips(Context c) {
        String json = prefs(c).getString(KEY_TRIPS, "[]");
        return gson.fromJson(json, LIST_TYPE);
    }

    public static void saveTrip(Context c, Trip trip) {
        List<Trip> all = getTrips(c);
        all.add(0, trip);
        prefs(c).edit().putString(KEY_TRIPS, gson.toJson(all)).apply();
    }

    public static int getTripCount(Context c) {
        return getTrips(c).size();
    }

    // Memories
    public static List<Memory> getMemories(Context c) {
        String json = prefs(c).getString(KEY_MEMORIES, "[]");
        return gson.fromJson(json, MEM_LIST_TYPE);
    }

    public static void addMemory(Context c, Memory m) {
        List<Memory> all = getMemories(c);
        all.add(0, m);
        prefs(c).edit().putString(KEY_MEMORIES, gson.toJson(all)).apply();
    }
}
