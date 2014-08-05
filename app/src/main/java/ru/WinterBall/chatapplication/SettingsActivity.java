package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsActivity extends Activity {

    int newColor;
    private static boolean NICKNAME_CHANGE = false;
    String newNickname;
    ImageButton buttonSelect;
    boolean isNightMode;

    final int redImg = R.drawable.red;
    final int greenImg = R.drawable.green;
    final int blueImg = R.drawable.blue;
    final int magentaImg = R.drawable.magenta;

    TextView NickNameLabel, switchTextView;
    EditText editNewNickname;
    Button btnChangeNick;
    Switch themeSwitch;

    private int newThemeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        newThemeId = getIntent().getExtras().getInt("theme");
        setTheme(newThemeId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        newColor = getIntent().getExtras().getInt("userColor");
        newNickname = getIntent().getExtras().getString("nickname");
        isNightMode = getIntent().getExtras().getBoolean("isSwitched");

        NickNameLabel = (TextView) findViewById(R.id.NickNameLabel);
        editNewNickname = (EditText) findViewById(R.id.editText_NewNick);

        btnChangeNick = (Button) findViewById(R.id.button_NickChange);
        buttonSelect = (ImageButton)findViewById(R.id.imageButtonColor);

        switchTextView = (TextView)findViewById(R.id.textViewSwtich);
        themeSwitch = (Switch)findViewById(R.id.switchTheme);
        if (isNightMode) {
            themeSwitch.setChecked(true);
            switchTextView.setText("Выключить ночной режим");
        }
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchTextView.setText("Выключить ночной режим");
                    reloadTheme(false);
                    isNightMode = true;
                } else {
                    switchTextView.setText("Включить ночной режим");
                    reloadTheme(true);
                    isNightMode = false;
                }
            }
        });

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

    public void reloadTheme(boolean test) {

        Intent intent = getIntent();
        intent.putExtra("userColor", newColor);
        intent.putExtra("nickname", newNickname);
        intent.putExtra("isSwitched", isNightMode);

        if (test) {
            intent.putExtra("theme", R.style.AppTheme);
        } else {
            intent.putExtra("theme", R.style.HoloDark);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);

    }
}
