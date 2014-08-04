package ru.WinterBall.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginButtonClick(View view) {

        EditText editLogin = (EditText) findViewById(R.id.editLogin);

        if (editLogin.getText().toString().replaceAll(" ", "").isEmpty()) {
            editLogin.setHint("need more letters...");
            editLogin.setText("");
        } else {
            Intent loginAnswer = new Intent();
            loginAnswer.putExtra("login", ((EditText) findViewById(R.id.editLogin)).getText().toString());
            setResult(RESULT_OK, loginAnswer);
            finish();
        }
    }
}
