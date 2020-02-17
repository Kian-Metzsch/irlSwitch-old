package com.nordicnerds.irlswitch;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.materialrangebar.RangeBar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class CommandActivity extends Activity
{
    private SeekBar seekBar_Activate;

    private EditText editText_PopupMessage;

    private Switch switch_Timer;

    private FirebaseDatabase database;

    private DatabaseReference myRef;

    private RangeBar timeRangeBar;

    private String key;
    private String command;

    private ImageView pcIconView;

    private TextView pcNameView;

    boolean stopSwitch = true;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        FirebaseApp.initializeApp(CommandActivity.this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Jw7H3kLo91f");

        seekBar_Activate = findViewById(R.id.seekBar_Activate);

        pcIconView = findViewById(R.id.imageView_PcIcon);
        pcNameView = findViewById(R.id.textView_PcName);

        editText_PopupMessage = findViewById(R.id.editText_PopupMessage);

        switch_Timer = findViewById(R.id.switch_Timer);
        timeRangeBar = findViewById(R.id.timeRangeBar);

        Bundle b = getIntent().getExtras();
        String title = Objects.requireNonNull(b).getString("title");
        int icon = Objects.requireNonNull(b).getInt("icon");
        key = Objects.requireNonNull(b).getString("key");
        int seekBar = Objects.requireNonNull(b).getInt("seekBar");
        command = Objects.requireNonNull(b).getString("command");

        System.out.println(seekBar);
        seekBarImage(seekBar);
        setIconNTitle(icon, title);
        showElements(command);

        seekBar_Activate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            boolean seekBarNoresetBool = false;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                int reset = 7;
                if (progress > 91)
                {
                    if (stopSwitch)
                    {
                        Handler finishDelay = new Handler();

                        craft_command(UUID.randomUUID().toString().substring(0,4));

                        seekBarNoresetBool = true;
                        stopSwitch = false;
                        seekBar_Activate.setEnabled(false);
                        //Toast.makeText(CommandActivity.this, "Command has been sent!", Toast.LENGTH_SHORT).show();

                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);

                        ObjectAnimator.ofFloat(seekBar_Activate, "translationX", size.x).setDuration(350).start();
                        finishDelay.postDelayed(()->finish(), 500);
                    }
                }
                else if (progress < reset)
                {
                    seekBar_Activate.setProgress(reset);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                if (!seekBarNoresetBool)
                {
                    seekBar_Activate.setProgress(3);
                }
            }
        });
    }

    private void showElements(String command)
    {
        switch (command)
        {
            case "lockCommand":
                //findViewById(R.id.switch_Timer).setVisibility(View.INVISIBLE);
                timeRangeBar.setVisibility(View.INVISIBLE);
                initRangerBarListener();
                break;
            case "messageCommand":
                findViewById(R.id.switch_Timer).setVisibility(View.INVISIBLE);
                editText_PopupMessage.setVisibility(View.VISIBLE);
                editText_PopupMessage.setEnabled(true);
                break;
            case "shutdownCommand":
                //findViewById(R.id.switch_Timer).setVisibility(View.INVISIBLE);
                timeRangeBar.setVisibility(View.INVISIBLE);
                initRangerBarListener();
                break;
        }
    }

    String leftThumbString;
    String rightThumbString;

    @SuppressLint("SetTextI18n")
    private void initRangerBarListener()
    {
        timeRangeBar.setOnRangeBarChangeListener((rangeBar, leftPinIndex, rightPinIndex, leftPinValue, rightPinValue) ->
        {
            int leftThumb = Integer.parseInt(leftPinValue);
            int rightThumb = Integer.parseInt(rightPinValue);

            leftThumbString = leftPinValue+":00";
            rightThumbString = rightPinValue+":00";

            TextView timerTextAllowed = findViewById(R.id.textView_EnabledTimer);

            if (leftThumb < 10)
                leftThumbString = "0"+leftThumbString;

            if (rightThumb < 10)
                rightThumbString = "0"+rightThumbString;

            timerTextAllowed.setText("Allowed game time\nfrom: "+leftThumbString+" to: "+rightThumbString);

            if (leftThumb >= 0 && rightThumb <= 23) // make switchbox invisible
            {
                switch_Timer.setAlpha(0.5f);
                switch_Timer.setEnabled(false);
            }

            if (leftThumb <= 0 && rightThumb >= 23) // revisible the switchbox
            {
                timerTextAllowed.setText("");
                switch_Timer.setAlpha(1.0f);
                switch_Timer.setEnabled(true);
            }
        });
    }

    private void seekBarImage(int seekBarImage)
    {
        Drawable thumb = getResources().getDrawable(seekBarImage);
        Bitmap bitmap = ((BitmapDrawable) thumb).getBitmap();
        Drawable draw = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        SeekBar mSeekBar = findViewById(R.id.seekBar_Activate);
        mSeekBar.setThumb(draw);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setIconNTitle(int pcIcon, String title)
    {
        pcIconView.setImageResource(pcIcon);
        pcNameView.setText(title);
    }

    private void craft_command(final String context)
    {
        new Thread()
        {
            public void run ()
            {
                myRef = database.getReference("Jw7H3kLo91f");

                if (switch_Timer.isEnabled() && !timeRangeBar.isEnabled())
                {
                    myRef.child("COMMANDS").child(key).child(command).setValue(timeStamp+"/"+context);
                }
                else if (timeRangeBar.isEnabled() && !switch_Timer.isEnabled())
                {
                    myRef.child("COMMANDS").child(key).child(command).setValue(leftThumbString+"-"+rightThumbString+"/"+context);
                }
                else if (editText_PopupMessage.isEnabled())
                {
                    myRef.child("COMMANDS").child(key).child(command).setValue(editText_PopupMessage.getText()+"/"+context);
                }
                else
                {
                    myRef.child("COMMANDS").child(key).child(command).setValue("/"+context);
                }
            }
        }.start();
        Toast.makeText(this, "Command has been sent!", Toast.LENGTH_SHORT).show();
    }

    public void back(View view)
    {
        this.finish();
    }

    String timeStamp;
    @SuppressLint("SetTextI18n")
    public void timerClick(View view)
    {
        if (switch_Timer.isChecked())
        {
            timeRangeBar.setEnabled(false);
            timeRangeBar.setAlpha(0.5f);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) ->
            {
                String selectedHourString = ""+selectedHour;
                String selectedMinuteString = ""+selectedMinute;
                if (selectedHour < 10)
                {
                    selectedHourString = "0"+selectedHour;
                }
                if (selectedMinute < 10)
                {
                    selectedMinuteString = "0"+selectedMinute;
                }

                timeStamp = selectedHourString + ":" + selectedMinuteString;
                System.out.println(timeStamp);
                switch_Timer.setText(timeStamp);
            }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
            mTimePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) ->
            {
                timeRangeBar.setEnabled(true);
                timeRangeBar.setAlpha(1.0f);
                switch_Timer.setChecked(false);
                switch_Timer.setText("Timer");
            });
            mTimePicker.setTitle("Select Timer");
            mTimePicker.show();
        }
        else
        {
            timeRangeBar.setEnabled(true);
            timeRangeBar.setAlpha(1.0f);
            switch_Timer.setText("Timer");
        }
    }
}