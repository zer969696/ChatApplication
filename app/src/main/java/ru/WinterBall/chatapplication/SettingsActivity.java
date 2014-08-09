package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;


public class SettingsActivity extends Activity {

    int newColor;
    private static boolean NICKNAME_CHANGE = false;
    String newNickname;
    ImageButton buttonSelect, buttonNightMode;

    final int redImg = R.drawable.red;
    final int greenImg = R.drawable.green;
    final int blueImg = R.drawable.blue;
    final int magentaImg = R.drawable.magenta;
    final int moonImg = R.drawable.gnome_weather_clear_night;

    TextView NickNameLabel, switchTextView;
    EditText editNewNickname;
    Button btnChangeNick;

    private int newThemeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //если эта активность была перезагружена
        if (savedInstanceState != null) {
            newThemeId = savedInstanceState.getInt("current_theme");
            newColor = savedInstanceState.getInt("current_color");
            newNickname = savedInstanceState.getString("current_nickname");
        } else {
            newThemeId = getIntent().getExtras().getInt("theme");
            newColor = getIntent().getExtras().getInt("userColor");
            newNickname = getIntent().getExtras().getString("nickname");
        }
        setTheme(newThemeId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //рычаги управления
        NickNameLabel = (TextView) findViewById(R.id.NickNameLabel);
        editNewNickname = (EditText) findViewById(R.id.editText_NewNick);
        btnChangeNick = (Button) findViewById(R.id.button_NickChange);
        buttonSelect = (ImageButton)findViewById(R.id.imageButtonColor);
        buttonNightMode = (ImageButton)findViewById(R.id.imageButton_nightMode);

        //если включен ночной режим(при переходе с мейна или пересоздании) то ставим тумблеры как надо!
        if (newThemeId == R.style.HoloDark) {
            buttonNightMode.setBackgroundResource(moonImg);
        }

        //устанавливаем никнейм для изменения (и ставим соответствующий цвет)
        NickNameLabel.setText("Ваш Никнейм: ");
        SpannableStringBuilder nick = new SpannableStringBuilder(newNickname);
        nick.setSpan(new ForegroundColorSpan(newColor), 0, nick.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        NickNameLabel.append(nick);

        //устанавливаем цвет для изменения
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

    //метод для передачи данных после перезапуска активности
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt("current_theme", newThemeId);
        outState.putInt("current_color", newColor);
        outState.putString("current_nickname", newNickname);

        super.onSaveInstanceState(outState);
    }

    protected void setNickColorOnChange(int colorToChange) {
        NickNameLabel.setText("Ваш Никнейм: ");
        SpannableStringBuilder nick = new SpannableStringBuilder(newNickname);
        nick.setSpan(new ForegroundColorSpan(colorToChange), 0, nick.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        NickNameLabel.append(nick);
    }

    public void onColorSwitch(View view) {

        if (buttonSelect.getTag().equals(redImg)) {
            buttonSelect.setBackgroundResource(greenImg);
            buttonSelect.setTag(greenImg);
            newColor = Color.GREEN;

            setNickColorOnChange(newColor);

            return;
        }
        if (buttonSelect.getTag().equals(greenImg)) {
            buttonSelect.setBackgroundResource(blueImg);
            buttonSelect.setTag(blueImg);
            newColor = Color.BLUE;

            setNickColorOnChange(newColor);

            return;
        }
        if (buttonSelect.getTag().equals(blueImg)) {
            buttonSelect.setBackgroundResource(magentaImg);
            buttonSelect.setTag(magentaImg);
            newColor = Color.MAGENTA;

            setNickColorOnChange(newColor);

            return;
        }
        if (buttonSelect.getTag().equals(magentaImg)) {
            buttonSelect.setBackgroundResource(redImg);
            buttonSelect.setTag(redImg);
            newColor = Color.RED;

            setNickColorOnChange(newColor);

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

            setNickColorOnChange(newColor);
        }
    }

    // сохранение всего
    public void saveAllButton(View view) {

        Intent answer = new Intent();

        answer.putExtra("nick", newNickname);
        answer.putExtra("color", newColor);
        answer.putExtra("theme", newThemeId);

        setResult(RESULT_OK, answer);
        finish();
    }

    public void onButtonNightModeClick(View view) {
        if (newThemeId == R.style.AppTheme) {
            newThemeId = R.style.HoloDark;
            this.recreate();
        } else {
            newThemeId = R.style.AppTheme;
            this.recreate();
        }
    }
}
