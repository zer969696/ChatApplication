package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;



public class MainActivity extends Activity {

    private int themeId = R.style.AppTheme;

    Socket ClientSocket;

    TextView chatView;
    EditText message;
    String nickname;
    int userColor = Color.RED;

    public static final int TYPE_SYSTEM = 0;
    public static final int TYPE_USER = 1;
    public static final int SERVER_PORT = 16212;
    //public static final String SERVER_ADRESS = "10.0.2.2";  // emulator IP
    public static final String SERVER_ADRESS = "192.168.0.26"; //Pav PC Ip - for mobile tests

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //если было пересоздание активности
        if (savedInstanceState != null) {
            themeId = savedInstanceState.getInt("theme");
            nickname = savedInstanceState.getString("nick");
            userColor = savedInstanceState.getInt("col");
        }

        setTheme(themeId);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatView = (TextView)findViewById(R.id.textViewChat);
        chatView.setMovementMethod(new ScrollingMovementMethod());
        message = (EditText)findViewById(R.id.editTextMessage);

        //если это первый вход в приложение
        if (savedInstanceState == null) {
            askLogin(1337);
        }
    }

    //при перезагрузке активности посылаем текущии данные для сохранения
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putCharSequence("chat", chatView.getText());
        outState.putInt("theme", themeId);
        outState.putString("nick", nickname);
        outState.putInt("col", userColor);

        super.onSaveInstanceState(outState);
    }

    //воостанавливаем чат
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        chatView.setText(savedInstanceState.getCharSequence("chat"));
    }

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
                int oldThemeId = getThemeID();

                nickname = data.getStringExtra("nick");
                userColor = data.getExtras().getInt("color");
                themeId = data.getExtras().getInt("theme");

                if (!oldNickname.equals(nickname)) {
                    createMessage(oldNickname, nickname, TYPE_SYSTEM);
                }

                if (themeId != oldThemeId) {
                    this.recreate();
                }
            } else {
                Toast.makeText(this, "Изменения не были сохранены", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void createMessage(String one, String two, int type) {

        if (type == TYPE_SYSTEM) {

            String userChangeNick = "system: " + one + " теперь: " + two;

            SpannableStringBuilder log = new SpannableStringBuilder(userChangeNick);
            log.setSpan(new ForegroundColorSpan(Color.CYAN), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
            log.setSpan(new ForegroundColorSpan(Color.CYAN), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            log.setSpan(new StyleSpan(Typeface.ITALIC), 6, userOnline.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            chatView.append(log);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:

                Intent settingsOpen = new Intent(this, SettingsActivity.class);

                settingsOpen.putExtra("userColor", userColor);
                settingsOpen.putExtra("nickname", nickname);
                settingsOpen.putExtra("theme", getThemeID());

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



    public class Server_thread implements Runnable {
        String message;
        Socket ClientSocket;

        public Server_thread(String msg) { message = msg; }

        @Override
        public void run(){
            try {
                ClientSocket = new Socket(InetAddress.getByName(SERVER_ADRESS), SERVER_PORT);
                ClientSocket.setKeepAlive(false);

                PrintWriter writer = new PrintWriter(ClientSocket.getOutputStream());
                writer.println(message);
                writer.flush();

                ClientSocket.close();
            } catch (UnknownHostException e1) { e1.printStackTrace();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }


    public void sendButtonClick(View view) {

        chatView.setHint("");
        chatView.setGravity(Gravity.NO_GRAVITY);

        if (message.getText().toString().replaceAll(" ", "").isEmpty()) {
            message.setHint("need more letters...");
        } else {
            createMessage(message.getText().toString(), TYPE_USER);

            try {
                Thread server_connect = new Thread( new Server_thread( message.getText().toString() ) );
                server_connect.start();
            } catch (Exception e) { e.getStackTrace(); }

            message.setHint("");
        }

        message.setText("");
        message.requestFocus();
    }

    //from stackoverflow (получаем айди текущей темы)
    public int getThemeID() {

        int themeResId = 0;
        try {
            Class<?> clazz = ContextThemeWrapper.class;
            Method method = clazz.getMethod("getThemeResId");
            method.setAccessible(true);
            themeResId = (Integer) method.invoke(this);
        } catch (Exception ex) {}

        return themeResId;
    }
}


