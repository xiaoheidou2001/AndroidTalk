package com.example.version0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText etName;
    private EditText etID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_login);
        etName = findViewById(R.id.editText);
        etID = findViewById(R.id.editText2);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.USER_NAME = etName.getText().toString();
                Constants.STUDENT_ID = etID.getText().toString();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0,R.anim.go_out);
            }
        });
    }
}
