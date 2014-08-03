package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    TextView chat;
    EditText message;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "hello", Toast.LENGTH_LONG).show();
        chat = (TextView)findViewById(R.id.textViewChat);
        message = (EditText)findViewById(R.id.editTextMessage);

        count = 0;
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
            case R.id.action_about:
                Toast.makeText(this, "selected about", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(this, "selected settings", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendButtonClick(View view) {

        if (chat.getGravity() == Gravity.CENTER) {
            chat.setGravity(Gravity.NO_GRAVITY);
        }

        chat.append(++count + ": " + message.getText() + "\r\n");
        message.setText("");
        message.requestFocus();
    }
}


