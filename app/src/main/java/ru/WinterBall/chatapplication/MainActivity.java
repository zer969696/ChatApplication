package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
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
    boolean saidHello = false;

    int userColor = Color.RED;

    String nickUpdate;
    boolean isNickChanged = false;

    public static final int TYPE_SYSTEM = 0;
    public static final int TYPE_USER = 1;
    public static final int SERVER_PORT = 12378;//12378;
    //public static final String SERVER_ADRESS = "10.0.2.2";  // emulator IP
    public static final String SERVER_ADRESS = "benzoback.ddns.net";  // emulator IP
    //public static final String SERVER_ADRESS = "192.168.0.26"; //Pav PC Ip - for mobile tests

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //если было пересоздание активности
        if (savedInstanceState != null) {
            themeId = savedInstanceState.getInt("theme");
            nickname = savedInstanceState.getString("nick");
            userColor = savedInstanceState.getInt("col");
            saidHello = savedInstanceState.getBoolean("hello");
            isNickChanged = savedInstanceState.getBoolean("isNickChanged");
            nickUpdate = savedInstanceState.getString("nickUpdate");
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

                String answer[] = ((String)msg.obj).split("ð");
                String nick;
                String message = "";
                int color;

                nick = answer[0];
                color = Integer.parseInt(answer[1]);
                for (int i = 2; i < answer.length; i++) {
                    message += answer[i];
                }

                createMessage(message, nick, color);
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
        outState.putBoolean("hello", saidHello);
        outState.putString("nickUpdate", nickUpdate);
        outState.putBoolean("isNickChanged", isNickChanged);

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

                if (!oldNickname.equals(nickname) && clientSocket.isConnected()) {
                    //createMessage(oldNickname, nickname, Color.CYAN);
                    nickUpdate = "systemð" + String.valueOf(Color.CYAN) + "ð" + "\"" +
                            oldNickname + "\"" + " теперь - " + "\"" + nickname + "\"";

                    if (themeId == oldThemeId) {
                        pWriter.println(nickUpdate);
                        pWriter.flush();
                    } else {
                        isNickChanged = true;
                    }
                }

                if (themeId != oldThemeId) {
                    this.recreate();
                }
            } else {
                Toast.makeText(this, "Изменения не были сохранены", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void sayHelloToServer(String nick) {
        pWriter.println("systemð" + String.valueOf(Color.CYAN) + "ð" + nick + " онлайн :)");
        pWriter.flush();

        saidHello = true;
    }

    protected void createMessage(String msg, String nick, int color) {

            SpannableStringBuilder userPrefix = new SpannableStringBuilder(nick);
            userPrefix.setSpan(new ForegroundColorSpan(color), 0, nick.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            chatView.append(userPrefix);
            chatView.append(": " + msg +"\r\n");
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

            try {

                pWriter.println(nickname + "ð" + userColor + "ð" + message.getText().toString());
                pWriter.flush();


                if (clientSocket.isClosed()) {
                    Toast.makeText(this, "Disconnected. Trying to reconnect...", Toast.LENGTH_SHORT).show();

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
        } catch (Exception ignored) {}

        return themeResId;
    }

    private class SetUpConnect implements Runnable {

        @Override
        public void run() {
            try {
                clientSocket = new Socket(SERVER_ADRESS, SERVER_PORT);

                bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                pWriter = new PrintWriter(clientSocket.getOutputStream());

                if (!saidHello) {
                    sayHelloToServer(nickname);
                }

                if (isNickChanged) {
                    pWriter.println(nickUpdate);
                    pWriter.flush();

                    isNickChanged = false;
                }

                new Thread(new ChatUpdate()).start();
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
                    chatView.setMovementMethod(new ScrollingMovementMethod());
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


