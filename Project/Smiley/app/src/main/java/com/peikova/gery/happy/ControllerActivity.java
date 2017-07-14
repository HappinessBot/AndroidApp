package com.peikova.gery.happy;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class ControllerActivity extends AppCompatActivity {

    private int[] IDs = new int[]{R.id.farrow, R.id.rarrow, R.id.larrow};
    private int[] dIDs = new int[]{R.id.farrow1, R.id.rarrow1, R.id.larrow1};
    private String[] command = new String[]{"ff", "r", "l"};
    List<ImageButton> buttons = new ArrayList<>();
    List<ImageView> dbuttons = new ArrayList<>();
    private int darker = R.id.rarrow1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setButtons();
    }

    interface API {
        @POST("command_robot")
        @Headers("Content-Type: application/json")
        Call<Void> sendCommandData(@Body CommandData commandData);
    }

    private class CommandData {
        private final String command;

        public CommandData(String command) {
            this.command = command;
        }
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

                    sendCommandData(new CommandData(command[finalI]));

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

    private void sendCommandData(CommandData data) {
        String url = "http://192.168.1.203:8080/";


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.e(this.getClass().getSimpleName(),"Message: " + message);
                    }
                }).setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();


        /*
        votesClient.send(vote);

         */

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API api = retrofit.create(API.class);

        api.sendCommandData(data).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.e("Error", response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Error", t.toString());
            }
        });
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
