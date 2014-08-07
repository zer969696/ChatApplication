package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(getIntent().getExtras().getInt("theme"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
}