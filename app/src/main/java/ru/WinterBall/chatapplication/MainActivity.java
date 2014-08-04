package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    TextView chat;
    EditText message;
    String nickname; //здесь хранится ник
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chat = (TextView)findViewById(R.id.textViewChat);
        chat.setMovementMethod(new ScrollingMovementMethod());
        message = (EditText)findViewById(R.id.editTextMessage);

        askLogin(1337);
    }

    //
    protected void askLogin(int code) {
        Intent getLogin = new Intent(this, LoginActivity.class);
        startActivityForResult(getLogin, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1337) {
            if (resultCode == RESULT_OK) {

                nickname = data.getStringExtra("login");

                chat.setHint("");
                chat.setGravity(Gravity.NO_GRAVITY);

                Spanned loginLog = Html.fromHtml("<font color=\"red\"> " + nickname + " вошел в чат </font>");
                chat.append(loginLog);
                chat.append("\r\n");
            } else {
                System.exit(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:

                Intent settingsOpen = new Intent(this, SettingsActivity.class);
                startActivity(settingsOpen);

                return true;

            case R.id.action_about:

                Intent aboutOpen = new Intent(this, AboutActivity.class);
                startActivity(aboutOpen);

                return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendButtonClick(View view) {

        chat.setHint("");
        chat.setGravity(Gravity.NO_GRAVITY);

        if (message.getText().toString().replaceAll(" ", "").isEmpty()) {
            message.setHint("need more letters...");
        } else {
            chat.append(nickname + ": " + message.getText() + "\r\n");
            message.setHint("");
        }

        message.setText("");
        message.requestFocus();
    }
}


