package com.example.mathdle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    Button playButton, rulesButton;
    GridView gridView;
    MyCustomAdapter adapter;
    ArrayList<Choose> chooseArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gridView = findViewById(R.id.gridView);
        chooseArrayList = new ArrayList<Choose>();


        Choose c1 = new Choose(R.drawable.four_equals_10,"4=10");
        Choose c2= new Choose(R.drawable.dscore, "score");
        Choose c3= new Choose(R.drawable.setting,"setting");


        chooseArrayList.add(c1);
        chooseArrayList.add(c2);
        chooseArrayList.add(c3);


        adapter = new MyCustomAdapter(chooseArrayList, getApplicationContext());
        gridView.setAdapter(adapter);
        gridView.setNumColumns(2);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){ Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);}
                if (position==1){ Intent i = new Intent(getApplicationContext(), ScoreActivity.class);
                    startActivity(i);}
                //if (position == 1) { i = new Intent(getApplicationContext(), Cube.class);}
                if (position==2){ Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(i);}

            }
        });


    }
}