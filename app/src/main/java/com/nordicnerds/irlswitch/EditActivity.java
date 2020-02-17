package com.nordicnerds.irlswitch;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class EditActivity extends Activity
{
    private int icon;
    private int imageListFirst[];
    private int current;

    private String key;
    private String title;

    private Button saveButton;
    private Button resetButton;

    private boolean iconClicked = false;
    private boolean nameEdited = false;

    private ImageButton[] buttonsIcon = new ImageButton[3];

    private ImageView currenctIcon;

    private EditText editText_CurrentName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        FirebaseApp.initializeApp(EditActivity.this);
        database = FirebaseDatabase.getInstance();
        
        imageListFirst = new int[] {
                R.drawable.custom_icon_0,
                R.drawable.custom_icon_1,
                R.drawable.custom_icon_2,
                R.drawable.custom_icon_3};

        saveButton = findViewById(R.id.button_Save);
        resetButton = findViewById(R.id.button_Reset);

        editText_CurrentName = findViewById(R.id.editText_CurrentName);

        ImageButton IconEdit1 = findViewById(R.id.imageButton_IconEdit1);
        buttonsIcon[0] = IconEdit1;
        ImageButton IconEdit2 = findViewById(R.id.imageButton_IconEdit2);
        buttonsIcon[1] = IconEdit2;
        ImageButton IconEdit3 = findViewById(R.id.imageButton_IconEdit3);
        buttonsIcon[2] = IconEdit3;

        IconEdit1.setImageResource(R.drawable.custom_icon_0);
        IconEdit2.setImageResource(R.drawable.custom_icon_1);
        IconEdit3.setImageResource(R.drawable.custom_icon_2);

        Bundle bundleEdit_Info = getIntent().getExtras();
        title = Objects.requireNonNull(bundleEdit_Info).getString("title");
        key = Objects.requireNonNull(bundleEdit_Info).getString("key");
        icon = Objects.requireNonNull(bundleEdit_Info).getInt("icon");
        current = Objects.requireNonNull(bundleEdit_Info).getInt("current");

        if (current == 1)
            findViewById(R.id.button_Back).setVisibility(View.INVISIBLE);

        currenctIcon = findViewById(R.id.imageView_CurrentIcon);
        editText_CurrentName.setText(title);
        currenctIcon.setImageResource(icon);

        System.out.println("icon = "+icon);

        initIcons();

        editText_CurrentName.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
                if (saveButton.getVisibility() == View.INVISIBLE)
                {
                    saveButton.setVisibility(View.VISIBLE);                  
                    resetButton.setVisibility(View.VISIBLE);
                    nameEdited = true;
                    System.out.println("nameEdited = "+nameEdited);
                }
                else
                {
                    nameEdited = true;
                    System.out.println("nameEdited = "+nameEdited);
                }
            }
        });
    }

    private void initIcons()
    {
        int i = 0;
        int i2 = 0;
        while(4 > i)
        {
            if (imageListFirst[i] != icon)
            {
                buttonsIcon[i2].setImageResource(imageListFirst[i]);
                buttonsIcon[i2].setTag(imageListFirst[i]);
                i2++;
            }
            i++;
        }
    }

    FirebaseDatabase database;
    int imageResource;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void editIcon(View view)
    {
        if (saveButton.getVisibility() == View.INVISIBLE)
        {
            saveButton.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.VISIBLE);
            iconClicked = true;
            System.out.println("iconClicked = "+iconClicked);
        }
        else
        {
            iconClicked = true;
            System.out.println("iconClicked = "+iconClicked);
        }
        Integer resource = (Integer)view.getTag();
        imageResource = resource-R.drawable.custom_icon_0;
        System.out.println("imageResource = "+imageResource);
        currenctIcon.setImageResource(resource);
    }

    public void save(View view)
    {
        DatabaseReference myRef = database.getReference("Jw7H3kLo91f").child("AuthenticatedPCS").child(key);

        System.out.println("nameEdited = "+nameEdited);
        System.out.println("iconClicked = "+iconClicked);

        if (nameEdited)
        {
            myRef.child("name").setValue(editText_CurrentName.getText().toString());
        }

        if (iconClicked)
        {
            myRef.child("icon").setValue(imageResource);
        }

        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void reset(View view)
    {
        currenctIcon.setImageResource(icon);
        editText_CurrentName.setText(title);
        saveButton.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);
        nameEdited = false;
        iconClicked = false;
    }

    public void back(View view)
    {
        this.finish();
    }
}
