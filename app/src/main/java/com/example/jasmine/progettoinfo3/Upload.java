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

/**
 * This class implement the connection, data send and data recieve with the server
 */
public class Upload extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate = null;

    /**
     * Uri of the server to be connected
     */
    private String serverURI;
    private String risultato = "nessuno";

    /**
     * Constructor of the class
     *
     * @param serverURI Name of the server to connect with (DNS or IP Address)
     */
    public Upload(String serverURI) {
        this.serverURI = serverURI;
    }

    /**
     * Action executed in the background of the main task/activity
     *
     * @param params Parameters to be used durin the computation, [photoPath,longitude,latitude]
     * @return The result given by the server
     */
    @Override
    protected String doInBackground(String... params) {

        String path = params[0];
        String longitudine = params[1];
        String latitudine = params[2];
        String username = params[3];

        Log.d("Log", "latitudine" + latitudine + "longitudine" + longitudine);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(serverURI);

        try {
            File file = new File(path);
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

            //entityBuilder.addBinaryBody("file", bytes);
            entityBuilder.addBinaryBody("file", file);
            entityBuilder.addTextBody("lon", longitudine);
            entityBuilder.addTextBody("lat", latitudine);
            entityBuilder.addTextBody("user", username);

            //entityBuilder.addPart("lon", new StringBody("1234", contentType));
            //entityBuilder.addTextBody("lat","3456",ContentType.TEXT_PLAIN);
            HttpEntity entity = entityBuilder.build();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();
            risultato = EntityUtils.toString(r_entity);
            Log.v("results", risultato);
            return risultato;
        } catch (ClientProtocolException e) {
            risultato = e.toString();
            Log.v("exception", risultato);
            return "errore";
        } catch (IOException e) {
            risultato = e.toString();
            Log.v("exception", risultato);
            return "errore";

        }
    }

    /**
     * method called after the doInBackground finishes, it returns the result to the main activity
     *
     * @param result Automatic parameter taken from the return line of doInBackground()
     */
    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }

}


