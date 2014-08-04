package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.HashMap;

public class SettingsActivity extends Activity {

    int newColor;
    String newNickname;
    ImageButton buttonSelect;
    HashMap<String, Integer> colorsHash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        newColor = getIntent().getExtras().getInt("userColor");
        newNickname = getIntent().getExtras().getString("nickname");
        buttonSelect = (ImageButton)findViewById(R.id.imageButtonColor);
        colorsHash = new HashMap<String, Integer>();

        colorsHash.put("red", Color.RED);
        colorsHash.put("green", Color.GREEN);
        colorsHash.put("blue", Color.BLUE);

        switch (newColor) {
            case Color.RED:
                buttonSelect.setBackgroundResource(R.drawable.red);
                break;
            case Color.GREEN:
                buttonSelect.setBackgroundResource(R.drawable.green);
                break;
            case Color.BLUE:
                buttonSelect.setBackgroundResource(R.drawable.blue);
            break;
        }
    }


    public void onColorSwitch(View view) {
        switch (buttonSelect.getResources().getColor(Color.RED)) {
            case R.drawable.red:
                Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
        }
    }
}
