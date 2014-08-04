package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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

    TextView chatView;
    EditText message;
    String nickname; //здесь хранится ник
    int userColor = Color.RED;

    public static final int TYPE_SYSTEM = 0;
    public static final int TYPE_USER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatView = (TextView)findViewById(R.id.textViewChat);
        chatView.setMovementMethod(new ScrollingMovementMethod());
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

                chatView.setHint("");
                chatView.setGravity(Gravity.NO_GRAVITY);

                createMessage("log", TYPE_SYSTEM);

            } else {
                System.exit(0);
            }
        }

        if (requestCode == 12){
            if (resultCode == RESULT_OK) {

                String oldNickname = nickname;

                nickname = data.getStringExtra("nick");
                userColor = data.getExtras().getInt("color");

                createMessage(oldNickname, nickname, TYPE_SYSTEM);
            } else {
                Toast.makeText(this, "Изменения не сохранены", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void createMessage(String one, String two, int type) {

        if (type == TYPE_SYSTEM) {

            String userChangeNick = "system: " + one + " теперь: " + two;

            SpannableStringBuilder log = new SpannableStringBuilder(userChangeNick);
            log.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            log.setSpan(new StyleSpan(Typeface.ITALIC), 6, userChangeNick.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            log.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), userChangeNick.length() - two.length(), userChangeNick.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            chatView.append(log);

            chatView.append("\r\n");
        }

    }

    protected void createMessage(String msg, int type) {

        if (type == TYPE_SYSTEM) {
            String userOnline = "system: " + nickname + " вошел в чат :)";

            SpannableStringBuilder log = new SpannableStringBuilder(userOnline);
            log.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            log.setSpan(new StyleSpan(Typeface.ITALIC), 6, userOnline.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            chatView.append(log);
            //test
            chatView.append("\r\n");
        }

        if (type == TYPE_USER) {

            SpannableStringBuilder userPrefix = new SpannableStringBuilder(nickname);
            userPrefix.setSpan(new ForegroundColorSpan(userColor), 0, nickname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            chatView.append(userPrefix);

            chatView.append(": " + msg +"\r\n");
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

                settingsOpen.putExtra("userColor", userColor);
                settingsOpen.putExtra("nickname", nickname);

                startActivityForResult(settingsOpen, 12);

                break;

            case R.id.action_about:

                Intent aboutOpen = new Intent(this, AboutActivity.class);
                startActivity(aboutOpen);

                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void sendButtonClick(View view) {

        chatView.setHint("");
        chatView.setGravity(Gravity.NO_GRAVITY);

        if (message.getText().toString().replaceAll(" ", "").isEmpty()) {
            message.setHint("need more letters...");
        } else {
            createMessage(message.getText().toString(), TYPE_USER);
            message.setHint("");
        }

        message.setText("");
        message.requestFocus();
    }
}


