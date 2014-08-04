package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class SettingsActivity extends Activity {

    int newColor;
    private static boolean NICKNAME_CHANGE = false;
    String newNickname;
    ImageButton buttonSelect;
    HashMap<String, Integer> colorsHash;

    TextView NickNameLabel;
    EditText editNewNickname;
    Button btnChangeNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        newColor = getIntent().getExtras().getInt("userColor");
        newNickname = getIntent().getExtras().getString("nickname");

        NickNameLabel = (TextView) findViewById(R.id.NickNameLabel);
        btnChangeNick = (Button) findViewById(R.id.button_NickChange);
        editNewNickname = (EditText) findViewById(R.id.editText_NewNick);

        buttonSelect = (ImageButton)findViewById(R.id.imageButtonColor);

        NickNameLabel.setText("Ваш Никнейм: "+newNickname.toString());

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

    /*
    public void onColorSwitch(View view) {
        switch (buttonSelect.getResources().getColor(Color.RED)) {
            case R.drawable.red:
                Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
        }
    }*/


    public void onClick_ChangeNick(View view) {
        if (!NICKNAME_CHANGE) {
            NICKNAME_CHANGE = true;

            NickNameLabel.setVisibility(view.INVISIBLE);
            editNewNickname.setVisibility(view.VISIBLE);
            btnChangeNick.setText("Выполнить");

        } else {
            NICKNAME_CHANGE = false;

            editNewNickname.setVisibility(view.INVISIBLE);
            NickNameLabel.setVisibility(view.VISIBLE);

            newNickname = editNewNickname.getText().toString();
            NickNameLabel.setText("Ваш Никнейм: "+newNickname);
            btnChangeNick.setText("Изменить");


        }
    }
}
