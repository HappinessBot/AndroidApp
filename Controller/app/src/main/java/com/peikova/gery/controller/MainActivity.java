package com.peikova.gery.controller;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private int[] IDs = new int[]{R.id.farrow, R.id.rarrow, R.id.larrow};
    private int[] dIDs = new int[]{R.id.farrow1, R.id.rarrow1, R.id.larrow1};
    private String[] command = new String[]{"f", "r", "l"};
    List<ImageButton> buttons = new ArrayList<>();
    List<ImageView> dbuttons = new ArrayList<>();
    private int darker = R.id.rarrow1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setButtons();
    }

    public void onBackPressed() {
        finish();
    }

    private void setButtons() {
        ImageButton arrow;
        ImageView darkArrow;

        for (int i = 0; i < 3; i++) {
            arrow = (ImageButton) findViewById(IDs[i]);
            darkArrow = (ImageView) findViewById((dIDs[i]));
            buttons.add(arrow);
            dbuttons.add(darkArrow);
            darkArrow.setVisibility(View.INVISIBLE);
        }


        for (int i = 0; i < 3; i++) {
            arrow = buttons.get(i);
            darkArrow = dbuttons.get(i);
            final int finalI = i;


            final ImageButton finalArrow = arrow;
            final ImageView finalDarkArrow = darkArrow;
            arrow.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    JSONObject test = new JSONObject();
                    try {
                        test.put("result", command[finalI]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Pressed", test.toString());
                    finalDarkArrow.setVisibility(View.VISIBLE);
                    allButtonsDisabled(buttons);
                    finalDarkArrow.postDelayed(new Runnable() {
                        public void run() {
                            finalDarkArrow.setVisibility(View.INVISIBLE);
                            allButtonsEnabled(buttons);
                        }
                    }, 2000);

                }
            });
        }
    }
    private void allButtonsDisabled(List<ImageButton> buttons){

        for(int i=0; i<3; i++){
            buttons.get(i).setEnabled(false);
        }
    }

    private void allButtonsEnabled(List<ImageButton> buttons){

        for(int i=0; i<3; i++){
            buttons.get(i).setEnabled(true);
        }
    }




}
