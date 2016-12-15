package com.mohan.location.ydm.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mohan.location.ydm.R;

public class MainActivity extends AppCompatActivity {


    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b= (Button) findViewById(R.id.button);
    }


    public void second(View view){
        Intent intent = new Intent(this, DialogActivity.class);
        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(this, b,
                    getString(R.string.transition_designer_news_login));
            startActivity(intent, options.toBundle());
        }
        else {
            startActivity(intent);
        }
    }
}
