package com.example.jasmine.progettoinfo3;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

public class Upload extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate=null;

    String risultato = "nessuno";

    @Override
    protected String doInBackground(String... params) {
        String result;
        //assegnazione parametri
        String path=params[0];
        String longitudine=params[1];
        String latitudine=params[2];

        Log.d("Log", "latitudine" + latitudine + "longitudine" + longitudine);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://progettoscandurra.andreacavagna.it/caricacellulare");

        try {
            File file = new File(path);
            MultipartEntityBuilder entityBuilder=MultipartEntityBuilder.create();

            //entityBuilder.addBinaryBody("file", bytes);
            entityBuilder.addBinaryBody("file",file);
            entityBuilder.addTextBody("lon",longitudine);
            entityBuilder.addTextBody("lat",latitudine);

            //entityBuilder.addPart("lon", new StringBody("1234", contentType));
            //entityBuilder.addTextBody("lat","3456",ContentType.TEXT_PLAIN);
            HttpEntity entity=entityBuilder.build();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();
            result=EntityUtils.toString(r_entity);
            Log.v("results",result);
            risultato = result;
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

    @Override
    protected void onPostExecute(String result) {

        delegate.processFinish(result);
    }


}


