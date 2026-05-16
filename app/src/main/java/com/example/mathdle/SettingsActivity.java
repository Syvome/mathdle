package com.example.mathdle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    Switch soundSwitch;

    RadioGroup difficultyGroup;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        soundSwitch = findViewById(R.id.soundSwitch);

        difficultyGroup =
                findViewById(R.id.difficultyGroup);

        prefs = getSharedPreferences(
                "mathdle",
                MODE_PRIVATE
        );

        // ---------------- LOAD SETTINGS ----------------

        boolean soundEnabled =
                prefs.getBoolean("sound", true);

        soundSwitch.setChecked(soundEnabled);

        String difficulty =
                prefs.getString("difficulty", "medium");

        switch(difficulty){

            case "easy":
                ((RadioButton)findViewById(R.id.easyButton))
                        .setChecked(true);
                break;

            case "medium":
                ((RadioButton)findViewById(R.id.mediumButton))
                        .setChecked(true);
                break;

            case "hard":
                ((RadioButton)findViewById(R.id.hardButton))
                        .setChecked(true);
                break;
        }

        // ---------------- SAVE SOUND ----------------

        soundSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    prefs.edit()
                            .putBoolean("sound", isChecked)
                            .apply();
                });

        // ---------------- SAVE DIFFICULTY ----------------

        difficultyGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    String selected = "medium";

                    if(checkedId == R.id.easyButton)
                        selected = "easy";

                    if(checkedId == R.id.mediumButton)
                        selected = "medium";

                    if(checkedId == R.id.hardButton)
                        selected = "hard";

                    prefs.edit()
                            .putString("difficulty", selected)
                            .apply();
                });
    }
}