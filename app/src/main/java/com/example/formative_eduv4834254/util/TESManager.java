package com.example.formative_eduv4834254.util;

import android.content.Context;

import com.example.formative_eduv4834254.data.SessionManager;

/**
 * TripBuddy Engagement Score (TES) helper. Stores counters in SharedPreferences via SessionManager.
 */
public class TESManager {

    public static final int MULTIPLIER_NEW_TRIP = 5;
    public static final int MULTIPLIER_MEMORY_WITH_PHOTO = 3;
    public static final int MULTIPLIER_GALLERY_INTERACTION = 1;
    public static final int MULTIPLIER_LOYALTY_USED = 4;

    private final Context appCtx;

    public TESManager(Context ctx) {
        this.appCtx = ctx.getApplicationContext();
    }

    public int getScore() {
        return SessionManager.getTesScore(appCtx);
    }

    public void addNewTrip() {
        SessionManager.addTes(appCtx, MULTIPLIER_NEW_TRIP);
    }

    public void addMemoryWithPhoto() {
        SessionManager.addTes(appCtx, MULTIPLIER_MEMORY_WITH_PHOTO);
    }

    public void addGalleryInteraction() {
        SessionManager.addTes(appCtx, MULTIPLIER_GALLERY_INTERACTION);
    }

    public void addLoyaltyUsed() {
        SessionManager.addTes(appCtx, MULTIPLIER_LOYALTY_USED);
    }
}
