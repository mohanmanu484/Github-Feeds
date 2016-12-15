package com.mohan.location.ydm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mohan.location.ydm.R;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class DialogActivity extends Activity implements Transition.TransitionListener, View.OnClickListener {

    private static final String TAG = "DialogActivity";

    private Button button;
    private ListView listView;
    private EditText userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        button= (Button) findViewById(R.id.button);
        userName= (EditText) findViewById(R.id.etUserName);
        button.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(getWindow().getSharedElementEnterTransition()!=null) {
                getWindow().getSharedElementEnterTransition().addListener(this);
            }
        }
    }

    public void scaleView() {
        Animation animation=AnimationUtils.loadAnimation(this,R.anim.scale_animation);
        button.startAnimation(animation);
    }

    public void dismiss(View view) {
        finish();
    }

    @Override
    public void onTransitionStart(Transition transition) {
        
    }

    @Override
    public void onTransitionEnd(Transition transition) {

        Log.d(TAG, "onTransitionEnd: ");
        button.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scaleView();
        }
    }

    @Override
    public void onTransitionCancel(Transition transition) {

    }

    @Override
    public void onTransitionPause(Transition transition) {

    }

    @Override
    public void onTransitionResume(Transition transition) {

    }

    @Override
    public void onClick(View view) {
        if(userName.getText().toString().isEmpty()){
            Toast.makeText(this, "Please provide username.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(userName.getText().toString().length()<4){
            Toast.makeText(this, "Username must be of minimum 4 character.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent=new Intent(this,EventsActivity.class);
        intent.putExtra("userName",userName.getText().toString());
        startActivity(intent);
        finish();
    }
}
