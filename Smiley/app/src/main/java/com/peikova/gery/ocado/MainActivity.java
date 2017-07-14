package com.peikova.gery.ocado;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import static com.peikova.gery.ocado.R.id.Smiley1;
import static com.peikova.gery.ocado.R.id.Smiley2;
import static com.peikova.gery.ocado.R.id.Smiley3;
import static com.peikova.gery.ocado.R.id.Smiley4;
import static com.peikova.gery.ocado.R.id.Smiley5;


public class MainActivity extends AppCompatActivity {
    private int bt = R.id.transparent;
    private int confrm = R.id.confirm;
    private int[] IDs = new int[]{Smiley1, R.id.Smiley2, R.id.Smiley3, Smiley4, Smiley5};
    private int ID = R.id.thick;
    List<ImageButton> buttons = new ArrayList<>();
    private String[] results = new String[]{"Excellent", "Good", "Normal", "Bad", "Very bad"};
    private short[] Xs = new short[]{0,-220,-130,107,190};
    private short[] Ys = new short[]{90,43,-155,-157,33};

    //Used functions:
    //  *setButtons();
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

    //Every button disappears but the one chosen,
    // Used functions:
    //  *fadeOutAndHideImage();
    private void fadeRestButtons(int id) {
        for(int count = 0; count < 5; count++){
            if(count != id ){
                fadeOutAndHideImage(buttons.get(count));
            }

        }
    }

    //When button gets clicked it goes to the center of the screen
    //Than u change scales or return in the beginning
    //Using functions:
    //  *changeScales
    //  *clickButThatButton();
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

    //Button disappears
    private void fadeOutAndHideImage(final ImageButton imageButton) {
            imageButton.setOnClickListener(null);
            imageButton.setVisibility(View.GONE);

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


}

