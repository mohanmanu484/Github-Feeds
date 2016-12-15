package com.mohan.location.ydm.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.mohan.location.ydm.R;
import com.mohan.location.ydm.RestClient;
import com.mohan.location.ydm.model.Event;
import com.mohan.location.ydm.model.NetworkCall;
import com.mohan.location.ydm.model.RepoDetails;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailsActivity extends AppCompatActivity {


    private RecyclerView repoEventList;
    private EventRecyclerAdapter eventRecyclerAdapter;
    private ImageView avatar;

    private RepoDetails repoDetails;

    private static final String TAG = "DetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        avatar = (ImageView) findViewById(R.id.ivAvatar);

        String repoURL = "";
        try {
            repoURL = getIntent().getStringExtra("repoURl");
        } catch (Exception e) {
            e.printStackTrace();
        }

        repoEventList = (RecyclerView) findViewById(R.id.rvRepoEventList);
        repoEventList.setHasFixedSize(true);
        repoEventList.setLayoutManager(new LinearLayoutManager(this));
        eventRecyclerAdapter = new EventRecyclerAdapter(new ArrayList<Event>(), true);
        repoEventList.setAdapter(eventRecyclerAdapter);
        getRepoDetails(repoURL);
    }

    public void makeNetworkCall(String url) {

        Retrofit retrofit = RestClient.getClient();

        NetworkCall networkCall = retrofit.create(NetworkCall.class);
        networkCall.getRepoEvents(url).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.code()!=200){
                    Toast.makeText(DetailsActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }


                try {
                    JSONArray jsonArray = new JSONArray(response.body());
                    Log.d(TAG, "onResponse: " + jsonArray);

                    ArrayList<Event> eventArrayList = new ArrayList<Event>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Event event = new Event();

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        event.setId(Long.valueOf(jsonObject.getString("id")));
                        event.setEventType(jsonObject.getString("type"));
                        event.setDateTime(jsonObject.getString("created_at"));
                        JSONObject userObj = jsonObject.getJSONObject("actor");
                        event.setAuthor(userObj.getString("display_login"));
                        event.setAvatarUrl(userObj.getString("avatar_url"));
                        JSONObject payloadObj = jsonObject.optJSONObject("payload");
                        if (payloadObj != null) {
                            event.setAction(payloadObj.optString("action"));
                        }
                        JSONObject repoObj = jsonObject.optJSONObject("repo");
                        event.setRepoName(repoObj.getString("name"));
                        event.setRepoUrl(repoObj.optString("url"));
                        eventArrayList.add(event);
                    }
                    eventRecyclerAdapter.updateEventList(eventArrayList, repoDetails);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(DetailsActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getRepoDetails(String URL) {
        Retrofit retrofit = RestClient.getClient();

        NetworkCall networkCall = retrofit.create(NetworkCall.class);
        networkCall.repoDetails(URL).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    Log.d(TAG, "onResponse: " + jsonObject);
                    JSONObject userObj = jsonObject.getJSONObject("owner");
                    String name = userObj.getString("login");
                    String avatarUrl = userObj.getString("avatar_url");
                    Picasso.with(DetailsActivity.this).load(avatarUrl).into(avatar);
                    repoDetails = new RepoDetails(jsonObject.getString("name"));
                    repoDetails.setDescription(jsonObject.getString("description"));
                    repoDetails.setLanguage(jsonObject.getString("language"));
                    repoDetails.setURL(jsonObject.getString("url"));
                    repoDetails.setStartCount(jsonObject.getInt("stargazers_count"));
                    repoDetails.setForkCount(jsonObject.getInt("forks_count"));
                    repoDetails.setWatchCount(jsonObject.getInt("subscribers_count"));

                    makeNetworkCall(jsonObject.getString("events_url"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
