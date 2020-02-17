package com.nordicnerds.irlswitch;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

public class LoadingActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Handler handler = new Handler();
        handler.postDelayed(this::validate_this, 2500);
    }

    private void validate_this()
    {
        finish();
        startActivity(new Intent(LoadingActivity.this,MainActivity.class));
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }
}
