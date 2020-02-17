package com.nordicnerds.irlswitch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Math.toIntExact;


@SuppressLint("Registered")
public class MainActivity extends Activity
{
    private ViewPager viewPager;

    private SlideAdapter PCAdapter;

    private DatabaseReference myRef;

    private ImageView left_arrow;
    private ImageView right_arrow;

    public int addData;
    public int savedPlacement;

    private boolean edit = false;
    private boolean fbLoadedSwitch = false;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(MainActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Jw7H3kLo91f");

        left_arrow = findViewById(R.id.imageView_left_arrow);
        right_arrow = findViewById(R.id.imageView_right_arrow);

        viewPager = findViewById(R.id.viewpager);
        PCAdapter = new SlideAdapter(this);

        System.out.println("Started!");

        loadFirebase();
        onPageListener();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (edit)
        {
            viewPager.setCurrentItem(savedPlacement);
            savedPlacement = 0;
            edit = false;
        }
    }

    private void onPageListener()
    {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            public void onPageScrollStateChanged(int state){}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}
            public void onPageSelected(int position)
            {
                if (viewPager.getCurrentItem()+1 == 1)
                {
                    left_arrow.setVisibility(View.INVISIBLE);
                    right_arrow.setVisibility(View.VISIBLE);
                }
                if (viewPager.getCurrentItem()+1 > 1 && viewPager.getCurrentItem()+1 < PCAdapter.lst_key.length)
                {
                    left_arrow.setVisibility(View.VISIBLE);
                    right_arrow.setVisibility(View.VISIBLE);
                }
                if (viewPager.getCurrentItem()+1 == PCAdapter.lst_key.length)
                {
                    left_arrow.setVisibility(View.VISIBLE);
                    right_arrow.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    void loadFirebase()
    {
        System.out.println("Firebase loaded");
        myRef.child("AuthenticatedPCS").addValueEventListener(new ValueEventListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                PCAdapter.lst_title = new String[(int) dataSnapshot.getChildrenCount()+1];
                PCAdapter.lst_key = new String[(int) dataSnapshot.getChildrenCount()+1];
                PCAdapter.lst_images = new int[(int) dataSnapshot.getChildrenCount()+1];

                addData = 0;
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren())
                {
                    System.out.println("Child snapsshot"+childDataSnapshot.child("name").getValue());
                    PCAdapter.lst_title[addData] = (String) childDataSnapshot.child("name").getValue();
                    PCAdapter.lst_key[addData] = childDataSnapshot.getKey();
                    System.out.println("addData = "+addData);
                    long imgInt = (long) childDataSnapshot.child("icon").getValue();
                    PCAdapter.lst_images[addData] = R.drawable.custom_icon_0+toIntExact(imgInt);
                    addData++;
                }

                PCAdapter.lst_title[addData] = "Add new";
                PCAdapter.lst_key[addData] = "null";
                PCAdapter.lst_images[addData] = R.drawable.add_button;

                viewPager.setAdapter(PCAdapter);
                if (PCAdapter.lst_key.length > 1)
                {
                    left_arrow.setVisibility(View.INVISIBLE);
                    right_arrow.setVisibility(View.VISIBLE);
                }
                else
                {
                    left_arrow.setVisibility(View.INVISIBLE);
                    right_arrow.setVisibility(View.INVISIBLE);
                }

                if (!fbLoadedSwitch)
                {
                    System.out.println("Enabling buttons");
                    findViewById(R.id.button_lock).setClickable(true);
                    findViewById(R.id.button_edit).setClickable(true);
                    findViewById(R.id.button_message).setClickable(true);
                    findViewById(R.id.button_shutdown).setClickable(true);
                    fbLoadedSwitch = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                System.out.println("Failed to read value.");
            }
        });
    }

    private void startCommand(int seekBar, String command)
    {
        Intent intent_command = new Intent(this, CommandActivity.class);
        Bundle bundle_CommandInfo = new Bundle();
        bundle_CommandInfo.putString("title", PCAdapter.lst_title[viewPager.getCurrentItem()]);
        bundle_CommandInfo.putInt("icon", PCAdapter.lst_images[viewPager.getCurrentItem()]);
        bundle_CommandInfo.putString("key", PCAdapter.lst_key[viewPager.getCurrentItem()]);
        bundle_CommandInfo.putInt("seekBar", seekBar);
        bundle_CommandInfo.putString("command", command);
        intent_command.putExtras(bundle_CommandInfo);
        startActivity(intent_command);
    }

    private void craftCommand(int drawable, String command)
    {
        if (!PCAdapter.lst_key[viewPager.getCurrentItem()].equals("null"))
        {
            startCommand(drawable, command);
        }
        else
        {
            Toast.makeText(this, "Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public void button_shutdown(View view)
    {
        craftCommand(R.drawable.shutdown_button, "shutdownCommand");
    }

    public void button_message(View view)
    {
        craftCommand(R.drawable.popup_button, "messageCommand");
    }

    @SuppressLint("SetTextI18n")
    public void button_lock(View view)
    {
        craftCommand(R.drawable.lock_button, "lockCommand");
    }

    @SuppressLint("SetTextI18n")
    public void button_edit(View view)
    {
        if (!PCAdapter.lst_key[viewPager.getCurrentItem()].equals("null"))
        {
            Intent intent_edit = new Intent(this, EditActivity.class);
            Bundle bundle_EditInfo = new Bundle();
            bundle_EditInfo.putString("title", PCAdapter.lst_title[viewPager.getCurrentItem()]);
            bundle_EditInfo.putInt("icon", PCAdapter.lst_images[viewPager.getCurrentItem()]);
            bundle_EditInfo.putString("key", PCAdapter.lst_key[viewPager.getCurrentItem()]);
            bundle_EditInfo.putInt("current", 0);
            intent_edit.putExtras(bundle_EditInfo);
            startActivity(intent_edit);
            edit = true;
            savedPlacement = viewPager.getCurrentItem();
        }
    }

    public void internet(View view)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nordic-nerds.com/"));
        startActivity(browserIntent);
    }

    public void button_settings(View view)
    {
        System.out.println("Settings");
    }

    public void button_add(View view)
    {
        if (viewPager.getCurrentItem()+1 == PCAdapter.lst_title.length)
        {
            Intent intent_qrscanner = new Intent(this, QRSyncActivity.class);
            startActivity(intent_qrscanner);
        }
    }
}