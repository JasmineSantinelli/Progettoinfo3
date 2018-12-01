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

import java.io.File;
import java.io.IOException;

public class Upload extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        String responseString = null;
        //assegnazione parametri
        String path=params[0];
        String longitudine=params[1];
        String latitudine=params[2];

       // Log.d("Log", "File path");
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://progettoscandurra.andreacavagna.it/caricacellulare");

        try {
            File file = new File(path);
            MultipartEntityBuilder entityBuilder=MultipartEntityBuilder.create();
            entityBuilder.addBinaryBody("file",file);
            entityBuilder.addTextBody("long",longitudine);
            entityBuilder.addTextBody("lat",latitudine);
            HttpEntity entity=entityBuilder.build();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();
            String result=EntityUtils.toString(r_entity);
            Log.v("results",result);

        } catch (ClientProtocolException e) {
            responseString = e.toString();
            Log.v("exception",responseString);
        } catch (IOException e) {
            responseString = e.toString();
            Log.v("exception",responseString);
        }

        return(null);
    }
    protected void doPostExecute(){
//bo, dovremo farci qualcosa con i risultati
    }
}


