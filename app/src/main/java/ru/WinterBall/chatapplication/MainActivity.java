package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.io.PrintWriter;
import java.net.Socket;




public class MainActivity extends Activity {

    private int themeId = R.style.AppTheme;

    TextView chatView;
    EditText message;
    String nickname;

    Socket clientSocket;
    BufferedReader bReader;
    PrintWriter pWriter;
    Handler handleMsg;
    boolean reconnect = true;

    int userColor = Color.RED;

    public static final int TYPE_SYSTEM = 0;
    public static final int TYPE_USER = 1;
    public static final int SERVER_PORT = 12378;
    public static final String SERVER_ADRESS = "10.0.2.2";  // emulator IP
    //public static final String SERVER_ADRESS = "192.168.0.26"; //Pav PC Ip - for mobile tests

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

        //хандлер для связи с окном чата (решает все проблемы, маленький ублюдок)
        handleMsg = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                createMessage((String)msg.obj, TYPE_USER);
            }
        };
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

        new Thread(new SetUpConnect()).start();
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

                new Thread(new SetUpConnect()).start();

            } else {
                //если пользователь при вводе логина нажал назад
                this.finish();
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

                aboutOpen.putExtra("theme", getThemeID());

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
            //createMessage(message.getText().toString(), TYPE_USER);

            try {
                //Thread server_connect = new Thread( new Server_thread( message.getText().toString() ) );
                //server_connect.start();
                //wait(2);
                //createMessage(serverAnswer, TYPE_USER);

                if (clientSocket.isConnected() && reconnect) {
                    reconnect = false;

                    new Thread(new ChatUpdate()).start();
                }

                pWriter.println(message.getText().toString());
                pWriter.flush();


                if (clientSocket.isClosed()) {
                    Toast.makeText(this, "Disconnected. Trying to reconnect...", Toast.LENGTH_SHORT).show();
                    reconnect = true;

                    new Thread(new SetUpConnect()).start();
                }
            } catch (Exception e) {
                e.getStackTrace();
            } finally {
                if (clientSocket == null) {
                    Toast.makeText(this, "Troubles with connection. Trying to reconnect...", Toast.LENGTH_SHORT).show();

                    new Thread(new SetUpConnect()).start();
                }
            }

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

    private class SetUpConnect implements Runnable {

        @Override
        public void run() {
            try {
                clientSocket = new Socket(SERVER_ADRESS, SERVER_PORT);

                bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                pWriter = new PrintWriter(clientSocket.getOutputStream());


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class ChatUpdate implements Runnable {
        String message;

        @Override
        public void run() {
            try {
                while ((message = bReader.readLine()) != null) {
                    Message msg = new Message();
                    msg.obj = message;
                    handleMsg.sendMessage(msg);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally { //файнали нужен для 100% закрытия сокета(иначе баги всплывают)
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


