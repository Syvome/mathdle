package com.example.mathdle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score);
        Button shareButton = findViewById(R.id.shareButton);
        TextView highscoreText = findViewById(R.id.highscoreText);

        SharedPreferences prefs =
                getSharedPreferences("mathdle", MODE_PRIVATE);

        int highscore = prefs.getInt("highscore",0);

        highscoreText.setText("Highscore : " + highscore);
        shareButton.setOnClickListener(v -> {

            Intent shareIntent =
                    new Intent(Intent.ACTION_SEND);

            shareIntent.setType("text/plain");

            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "My score is " + highscore + " on Mathdle!"

            );

            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Partager via"
                    )
            );
        });
    }
}