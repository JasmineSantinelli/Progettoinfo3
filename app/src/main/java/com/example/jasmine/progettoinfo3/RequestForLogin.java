package com.example.jasmine.progettoinfo3;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

// TODO: 11/12/2018 tutta da verificare se no semplifichiamo le cose come negli altri file, ho provato a dividere in piu funzioni
public class RequestForLogin extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate=null;
    String risultato = "nessuno";

    @Override
    protected String doInBackground(String... strings) {

        // FAI QUALCOSA PER IL LOGIN
        try {
            if (strings[0].equals("login") && strings.length == 3) {
                return login_utente(strings);
            }
            else if (strings[0].equals("register") && strings.length == 3212) {
                // TODO: 11/12/2018 aggiungi funzione per registrarsi e cambia numero di parametri
                return login_utente(strings);
            } else {
                throw new IndexOutOfBoundsException("Numero di parametri passati sbagliato!");
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d("errror","numero di parametri passati al metodo requestforlogin/doinbackground errato");
            return null;
        }
    }

    private String login_utente(String... strings){
        String result;
        String email=strings[0];
        String password=strings[1];

        HttpClient httpclient = new DefaultHttpClient();
        // TODO: 11/12/2018 Cambia l indirizzo del sito che non va bene ora manda a un posto a caso
        HttpPost httppost = new HttpPost("http://progettoscandurra.andreacavagna.it/richiestabuche");
        MultipartEntityBuilder entityBuilder=MultipartEntityBuilder.create();
        entityBuilder.addTextBody("email",email);
        entityBuilder.addTextBody("password",password);
        HttpEntity entity=entityBuilder.build();

        try {
            result = call_receive_params(httppost,httpclient,entity);
            return result;
        } catch (ClientProtocolException e) {
            result = e.toString();
            Log.v("exception",result);
            return "errore";
        } catch (IOException e) {
            result = e.toString();
            Log.v("exception",result);
            return "errore";
        }
    }

    private String call_receive_params(HttpPost httppost,HttpClient httpclient,HttpEntity entity) throws IOException{
        httppost.setEntity(entity);
        String result;
        // Making server call
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity r_entity = response.getEntity();
        result=EntityUtils.toString(r_entity);
        Log.v("results",result);
        risultato = result;
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
