package com.example.projet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefs";
    public static final String MUSIC_ENABLED = "music_enabled";
    public static final String NOTIF_PREF = "notif_pref";
    public static final int NOTIF_2_DAYS = 2;
    public static final int NOTIF_1_WEEK = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch switchMusic = findViewById(R.id.switchMusic);
        RadioGroup rgNotif = findViewById(R.id.rgNotificationPref);
        RadioButton rb2Days = findViewById(R.id.rbTwoDays);
        RadioButton rb1Week = findViewById(R.id.rbOneWeek);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Music logic
        boolean isMusicEnabled = prefs.getBoolean(MUSIC_ENABLED, true);
        switchMusic.setChecked(isMusicEnabled);
        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(MUSIC_ENABLED, isChecked).apply();
            Intent intent = new Intent(this, MusicService.class);
            if (isChecked) {
                startService(intent);
            } else {
                stopService(intent);
            }
        });

        // Notification logic
        int currentNotifPref = prefs.getInt(NOTIF_PREF, NOTIF_2_DAYS);
        if (currentNotifPref == NOTIF_1_WEEK) {
            rb1Week.setChecked(true);
        } else {
            rb2Days.setChecked(true);
        }

        rgNotif.setOnCheckedChangeListener((group, checkedId) -> {
            int days = (checkedId == R.id.rbOneWeek) ? NOTIF_1_WEEK : NOTIF_2_DAYS;
            prefs.edit().putInt(NOTIF_PREF, days).apply();
        });
    }
}
