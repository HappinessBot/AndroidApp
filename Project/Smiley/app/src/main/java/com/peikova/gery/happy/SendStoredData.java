package com.peikova.gery.happy;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;


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


public class SendStoredData extends Service {

    private SQLiteDatabase database;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        database = openOrCreateDatabase("offlineStorage",MODE_PRIVATE,null);

        Cursor resultSet = database.rawQuery("Select * from Votes",null);
        if (resultSet != null && resultSet.getCount() > 0) {
            resultSet.moveToFirst();

            while (resultSet.isLast()) {
                VoteData vate = new VoteData(resultSet.getInt(resultSet.getColumnIndex("Vote")),
                        resultSet.getString(resultSet.getColumnIndex("Time")));

                //sendRequest(resultSet.getInt(resultSet.getColumnIndex("Vote")),
                 //       resultSet.getString(resultSet.getColumnIndex("Time")));
                resultSet.moveToNext();
                Log.d("CountHTTP:",String.valueOf(resultSet.getPosition()));
            }

            if (resultSet.getCount() == 0) {
                database.execSQL("delete from Votes");
                Log.d("Count: ", String.valueOf(resultSet.getCount()));
            }
            resultSet.close();
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }


    }

    interface API {
        @POST("vote")
        @Headers("Content-Type: application/json")
        Call<Void> sendVoteData(@Body VoteData voteData);
    }

    private class VoteData {
        private int vote;
        private String time;

        public VoteData(int vote, String time) {
            this.vote = vote;
            this.time = time;
        }

        public int getVote() {
            return vote;
        }

        public String getTime() {
            return time;
        }
    }

    private void sendRequest(VoteData vote) {
        String url = "http://192.168.1.203:8080/";

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.d(this.getClass().getSimpleName(),"Message: " + message);
                    }
                }))
                .build();

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
                    //addToDatabase(vote);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            //    addToDatabase(vote);
            }
        });
    }


    private void addToDatabase(VoteData data) {
        ContentValues value = new ContentValues();
        value.put("Vote", data.getVote());
        value.put("Time",  data.getTime());
        database.insert("Votes",null,value);

        Log.d(this.getClass().getSimpleName(), "Saved to database");
    }
}