package com.example.jasmine.progettoinfo3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LogActivity extends AppCompatActivity implements AsyncResponse {

    EditText et_email,et_password;
    String err_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        et_email = findViewById(R.id.editText3);
        et_password = findViewById(R.id.editText4);
    }


    public void entra(View view){
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        if (email==null || password==null || email.equals("") || password.equals("")) {
            Toast.makeText(this, "campi non inseriti correttamente", Toast.LENGTH_SHORT).show();
            return;
            }
        RequestForLogin reqForLogin = new RequestForLogin();
        reqForLogin.delegate = this;
        reqForLogin.execute(email,password);
    }

    public  void registrati(View view){
        Intent intent=new Intent(this,LogActivity.class);
        startActivity(intent);

    }


    @Override
    public void processFinish(String output) {
        // TODO: 11/12/2018 finisci di fare il salvataggio globale delle variabili di login
        if (output.equals("loggato")){
        } else if (output.equals("non trovato")){
            err_msg = "Utente non trovato, controlla la mail oppure registrati";
            Toast.makeText(this, err_msg, Toast.LENGTH_SHORT).show();
            Log.v("Log",err_msg);
            return;
        } else {
            err_msg = "qualcosa è andato storto";
            Toast.makeText(this, err_msg, Toast.LENGTH_SHORT).show();
            Log.v("Log",err_msg);
            return;
        }
    }
}
