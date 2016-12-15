package com.mohan.location.ydm.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mohan.location.ydm.R;
import com.mohan.location.ydm.RestClient;
import com.mohan.location.ydm.model.Event;
import com.mohan.location.ydm.model.NetworkCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EventsActivity extends AppCompatActivity {

    EventRecyclerAdapter eventsAdapter;
    RecyclerView recyclerView;
    private static final String TAG = "EventsActivity";
    String userName;
    ProgressBar progressBar;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Events list");
        setSupportActionBar(toolbar);
        userName= getIntent().getStringExtra("userName");
        if(userName==null){
            throw new IllegalArgumentException("Must provide userName");
        }
        progressBar= (ProgressBar) findViewById(R.id.pbSync);
        recyclerView= (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        eventsAdapter=new EventRecyclerAdapter(new ArrayList<Event>(0));
        recyclerView.setAdapter(eventsAdapter);
        fetchEvents();




    }


    public void fetchEvents() {

        Retrofit retrofit = RestClient.getClient();

        NetworkCall networkCall = retrofit.create(NetworkCall.class);
        networkCall.getUserEvents(userName).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {


                progressBar.setVisibility(View.GONE);
                if(response.code()==404){
                    Toast.makeText(EventsActivity.this, "User Not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                if(response.code()!=200){
                    Toast.makeText(EventsActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }


                try {
                    JSONArray jsonArray=new JSONArray(response.body());
                    Log.d(TAG, "onResponse: "+jsonArray);;

                    ArrayList<Event> eventArrayList=new ArrayList<Event>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Event event=new Event();

                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        event.setId(Long.valueOf(jsonObject.getString("id")));
                        event.setEventType(jsonObject.getString("type"));
                        event.setDateTime(jsonObject.getString("created_at"));
                        JSONObject userObj=jsonObject.getJSONObject("actor");
                        event.setAuthor(userObj.getString("display_login"));
                        event.setAvatarUrl(userObj.getString("avatar_url"));
                        JSONObject payloadObj=jsonObject.optJSONObject("payload");
                        if(payloadObj!=null){
                            event.setAction(payloadObj.optString("action"));
                        }

                        JSONObject repoObj=jsonObject.optJSONObject("repo");
                        event.setRepoName(repoObj.getString("name"));
                        event.setRepoUrl(repoObj.optString("url"));
                        eventArrayList.add(event);
                    }
                    if(eventArrayList.size()>0) {
                        eventsAdapter.updateEventList(eventArrayList);
                    }else {
                        Toast.makeText(EventsActivity.this, "No data found for this user", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Retrofit retrofitError;
                if (t instanceof IOException) {
                    Toast.makeText(EventsActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
