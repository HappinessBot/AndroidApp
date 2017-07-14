package com.peikova.gery.happy;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONException;
import org.json.JSONObject;

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

import static com.peikova.gery.happy.R.id.Smiley1;
import static com.peikova.gery.happy.R.id.Smiley2;
import static com.peikova.gery.happy.R.id.Smiley3;
import static com.peikova.gery.happy.R.id.Smiley4;
import static com.peikova.gery.happy.R.id.Smiley5;

public class MainActivity extends AppCompatActivity {

    public static final String App = "App";
    public static final String QUESTION = "question";
    private int[] results = new int[]{5,3,0,-3,-5};
    private SQLiteDatabase database;
    private int bt = R.id.transparent;
    private int confrm = R.id.confirm;
    private int[] IDs = new int[]{Smiley1, R.id.Smiley2, R.id.Smiley3, Smiley4, Smiley5};
    private int ID = R.id.thick;
    List<ImageButton> buttons = new ArrayList<>();
    private short[] Xs = new short[]{0,-220,-130,107,190};
    private short[] Ys = new short[]{90,43,-155,-157,33};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        SharedPreferences sharedPref = getSharedPreferences(App,MODE_PRIVATE);
        String question = sharedPref.getString(QUESTION,"No question for today.");

        setTitle(question);

        //startService(new Intent(getApplicationContext(),SendStoredData.class));

        //stopService(new Intent(getApplicationContext(),SendStoredData.class));
        database = openOrCreateDatabase("OfflineStorage", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS Votes(Vote INTEGER,Time VARCHAR);");

        setButtons();
    }

    public void onBackPressed() {

        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_question:
                setQuestion();
                return true;
            case R.id.control_robot:
                controlRobot();
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void controlRobot() {
        startActivity(new Intent(this,ControllerActivity.class));
    }


    private void setQuestion() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);

        alert.setView(input);
        alert.setTitle("Set question for the day:");

        alert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPref = getSharedPreferences(App,MODE_PRIVATE);
                String question = input.getText().toString();
                sharedPref.edit().putString(QUESTION, question).apply();

                setTitle(question);
            }
        });

        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }

    }

    //Gets the buttons
    // Used functions:
    //  *moveSmiley();
    //  *fadeRestButtons();
    private void setButtons() {
        ImageButton tmp;
        final ImageView thck = (ImageView)findViewById(ID);
        final ImageButton trp = (ImageButton)findViewById(bt);
        final TextView confirm = (TextView)findViewById(confrm);
        thck.setVisibility(View.INVISIBLE);
        trp.setVisibility(View.INVISIBLE);
        confirm.setVisibility(View.INVISIBLE);





        for (int i = 0; i < 5; i++) {
            tmp = (ImageButton) findViewById(IDs[i]);
            buttons.add(tmp);
        }


        for (int i = 0; i < 5; i++) {
            tmp = buttons.get(i);
            final int finalI = i;
            tmp.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    sendRequest(new VoteData(results[finalI],new DateTime(DateTimeZone.UTC)));
                    JSONObject test = new JSONObject();


                    try {
                        test.put("result", results[finalI]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Pressed", test.toString());

                    moveSmiley(finalI,Xs[finalI],Ys[finalI],thck,trp,confirm);
                    fadeRestButtons(finalI);

                }
            });
        }
    }

    private void setTimer(){

    }


    private void fadeRestButtons(int id) {
        for(int count = 0; count < 5; count++){
            if(count != id ){
                fadeOutAndHideImage(buttons.get(count));
            }

        }
    }

    private void moveSmiley(final int id, short x, short y, final ImageView thck,final ImageButton trp,final TextView confirm) {
        final ImageButton btn = buttons.get(id);
        final float btn_x = btn.getX() + x;
        final float btn_y = btn.getY() + y;
        btn.setOnClickListener(null);
        Animation animation = new TranslateAnimation(0,x,0,y);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        btn.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                confirm.setVisibility(View.VISIBLE);
                clickButThatButton(thck,trp);
                btn.setX(btn_x);
                btn.setY(btn_y);
                btn.clearAnimation();
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirm.setVisibility(View.INVISIBLE);
                        changeScales(btn,thck,trp);

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //The thick appears with scale effect
    private void thickAnimation(ImageView btn){
        btn.setVisibility(View.VISIBLE);
        final Animation thScale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.thick_scale);
        btn.startAnimation(thScale);
    }
    //When background is touched you return to the beginning
    private void clickButThatButton(final ImageView img,ImageButton bt){
        bt.setVisibility(View.VISIBLE);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        MainActivity.class);

                startActivity(intent);
            }
        });
    }

    //Depending on which button u have clicked, u use different scale effect
    //Using functions:
    //  *animationScale();
    private void changeScales(final ImageButton btn,final ImageView thck,final ImageButton trp){
        switch (btn.getId()){

            case Smiley1:
                btn.setEnabled(false);
                Animation smileyScale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_smiley1);
                Animation rvScale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.reverse_scale1);
                animationScale(btn, thck, trp, smileyScale, rvScale);
                break;


            case Smiley2:
                btn.setEnabled(false);
                smileyScale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_smiley2);
                rvScale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.reverse_scale2);
                animationScale(btn, thck, trp, smileyScale, rvScale);
                break;


            case Smiley3:
                btn.setEnabled(false);
                smileyScale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_smiley3);
                rvScale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.reverse_scale3);
                animationScale(btn, thck, trp, smileyScale, rvScale);
                break;


            case Smiley4:
                btn.setEnabled(false);
                smileyScale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_smiley4);
                rvScale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.reverse_scale4);
                animationScale(btn, thck, trp, smileyScale, rvScale);
                break;



            case Smiley5:
                btn.setEnabled(false);
                smileyScale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_smiley5);
                rvScale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.reverse_scale5);
                animationScale(btn, thck, trp, smileyScale, rvScale);
                break;
        }


    }

    //Execute the chosen animations
    //Used functions:
    //  *thckAnimation();
    //  *clickButThatButton();
    private void animationScale(final ImageButton btn,final ImageView thck, final ImageButton trp, Animation smileyScale, final Animation rvScale ){
        btn.setEnabled(false);
        trp.setOnClickListener(null);
        btn.startAnimation(smileyScale);
        Log.d("Here","lalal");
        smileyScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                btn.startAnimation(rvScale);
                rvScale.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        btn.setVisibility(View.GONE);
                        thickAnimation(thck);
                        clickButThatButton(thck,trp);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void fadeOutAndHideImage(final ImageButton imageButton) {
            imageButton.setOnClickListener(null);
            imageButton.setVisibility(View.GONE);

    }

    interface API {

        @POST("vote")
        @Headers("Content-Type: application/json")
        Call<Void> sendVoteData(@Body VoteData voteData);
    }

    private class VoteData {
        private final int vote;
        private final String date;

        public VoteData(int vote, DateTime time) {
            this.vote = vote;
            this.date = time.toString();
        }

        public int getVote() {
            return vote;
        }

        public String getDate() {
            return date;
        }
    }

    private void sendRequest(final VoteData vote) {
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

        api.sendVoteData(vote).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    addToDatabase(vote);
                    Log.d("Code", String.valueOf(response.code()));
                }
                Log.e("Error", response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Error", t.toString());
                addToDatabase(vote);
            }
        });
    }

    private void addToDatabase(VoteData data) {
        ContentValues value = new ContentValues();
        value.put("Vote", data.getVote());
        value.put("Time",  data.getDate());
        database.insert("Votes",null,value);

        Log.d(this.getClass().getSimpleName(), "Saved to database");
    }

}

