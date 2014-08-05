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

        //устанавливаем тему из мейн окна или после обновления
        newThemeId = getIntent().getExtras().getInt("theme");
        setTheme(newThemeId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        newColor = getIntent().getExtras().getInt("userColor");
        newNickname = getIntent().getExtras().getString("nickname");

        NickNameLabel = (TextView) findViewById(R.id.NickNameLabel);
        editNewNickname = (EditText) findViewById(R.id.editText_NewNick);

        btnChangeNick = (Button) findViewById(R.id.button_NickChange);
        buttonSelect = (ImageButton)findViewById(R.id.imageButtonColor);

        switchTextView = (TextView)findViewById(R.id.textViewSwtich);
        themeSwitch = (Switch)findViewById(R.id.switchTheme);
        //если включен ночной режим(при переходе с мейна) то ставим тумблеры как надо!
        if (newThemeId == R.style.HoloDark) {
            themeSwitch.setChecked(true);
            switchTextView.setText("Выключить ночной режим");
        }
        //слушатель для изменения тублера и темы(вызов перезагрузки)
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reloadTheme(false);
                } else {
                    reloadTheme(true);
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

    // сохранение всего
    public void saveAllButton(View view) {

        Intent answer = new Intent();

        answer.putExtra("nick", newNickname);
        answer.putExtra("color", newColor);
        answer.putExtra("theme", newThemeId);

        setResult(RESULT_OK, answer);
        finish();
    }

    //функция обновления темы
    public void reloadTheme(boolean isActivated) {

        //передача информации "обновленной" активности
        Intent intent = getIntent();
        intent.putExtra("userColor", newColor);
        intent.putExtra("nickname", newNickname);

        // если isActivated(включен ли ночной режим) == true, то посылаем противоположную тему
        if (isActivated) {
            intent.putExtra("theme", R.style.AppTheme);
        } else {
            intent.putExtra("theme", R.style.HoloDark);
        }

        //переход без анимации
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        //еще одно отключение анимации (stackoverflow)
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
