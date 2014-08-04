package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.content.Intent;
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

    final int redImg = R.drawable.red;
    final int greenImg = R.drawable.green;
    final int blueImg = R.drawable.blue;
    final int magentaImg = R.drawable.magenta;



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

        NickNameLabel.setText("Ваш Никнейм: "+ newNickname);

        switch (newColor) {
            case Color.RED:
                buttonSelect.setBackgroundResource(redImg);
                buttonSelect.setTag(redImg);
                break;
            case Color.GREEN:
                buttonSelect.setBackgroundResource(greenImg);
                buttonSelect.setTag(greenImg);
                break;
            case Color.BLUE:
                buttonSelect.setBackgroundResource(blueImg);
                buttonSelect.setTag(blueImg);
                break;
            case Color.MAGENTA:
                buttonSelect.setBackgroundResource(magentaImg);
                buttonSelect.setTag(magentaImg);
                break;
        }

    }

    public void onColorSwitch(View view) {

        if (buttonSelect.getTag().equals(redImg)) {
            buttonSelect.setBackgroundResource(greenImg);
            buttonSelect.setTag(greenImg);
            newColor = Color.GREEN;

            return;
        }
        if (buttonSelect.getTag().equals(greenImg)) {
            buttonSelect.setBackgroundResource(blueImg);
            buttonSelect.setTag(blueImg);
            newColor = Color.BLUE;

            return;
        }
        if (buttonSelect.getTag().equals(blueImg)) {
            buttonSelect.setBackgroundResource(magentaImg);
            buttonSelect.setTag(magentaImg);
            newColor = Color.MAGENTA;

            return;
        }
        if (buttonSelect.getTag().equals(magentaImg)) {
            buttonSelect.setBackgroundResource(redImg);
            buttonSelect.setTag(redImg);
            newColor = Color.RED;

            return;
        }
    }


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

    public void saveAllButton(View view) {

        Intent answer = new Intent();

        answer.putExtra("nick", newNickname);
        answer.putExtra("color", newColor);

        setResult(RESULT_OK, answer);
        finish();
    }
}
