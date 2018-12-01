package com.example.jasmine.progettoinfo3;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

public class Upload extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        String responseString = null;
        String path=params[0];
        Log.d("Log", "File path");
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://progettoscandurra.andreacavagna.it/caricacellulare");

        try {
            File file = new File(path);
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            // DEVI AGGIUNGERE IN QUALCHE MODO QUI IL CONTENT TYPE JPEG JPG IN MODO CHE IL CODICE LO ACCETTI
            // SE VUOI POSSO TOGLIERE IL FATTO CHE CONTROLLI CHE IL FILE INSERITO VADA BENE E VEIDAMO COME VA
            entityBuilder.addBinaryBody("file",file);
            // MultipartEntity entity = new MultipartEntity();
            //ExifInterface newIntef = new ExifInterface(path);
            //newIntef.setAttribute(ExifInterface.TAG_ORIENTATION,String.valueOf(2));
            HttpEntity entity = entityBuilder.build();
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity);
            Log.v("result", result);

            /*
            FileEntity entity = new FileEntity(file);

            FormBodyPart part=new FormBodyPart("file",new FileBody(file));

            //long totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();


            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
                Log.d("Log", responseString);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode + " -> " + response.getStatusLine().getReasonPhrase();
                Log.d("Log", responseString);
            }
            */
        } catch (ClientProtocolException e) {
            responseString = e.toString();
        } catch (IOException e) {
            responseString = e.toString();
        }

        return(null);
    }

}









/**protected void onCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState);
 setContentView(R.layout.activity_upload);
 //save path taken from Main_Activity
 String path=null;
 path=getIntent().getExtras().getString("path");
 String responseString = null;

 Log.d("Log", "File path");
 HttpClient httpclient = new DefaultHttpClient();
 HttpPost httppost = new HttpPost("http://progettoscandurra.andreacavagna.it/caricacellulare");

 try {
 // MultipartEntity entity = new MultipartEntity();
 //ExifInterface newIntef = new ExifInterface(path);
 //newIntef.setAttribute(ExifInterface.TAG_ORIENTATION,String.valueOf(2));

 File file = new File(path);
 FormBodyPart part=new FormBodyPart("pic",new FileBody(file));
 //entity.addPart(part);
 FileEntity entity=new FileEntity(file,"pic");
 //long totalSize = entity.getContentLength();
 httppost.setEntity(entity);

 // Making server call
 HttpResponse response = httpclient.execute(httppost);
 HttpEntity r_entity = response.getEntity();


 int statusCode = response.getStatusLine().getStatusCode();
 if (statusCode == 200) {
 // Server response
 responseString = EntityUtils.toString(r_entity);
 Log.d("Log", responseString);
 } else {
 responseString = "Error occurred! Http Status Code: "
 + statusCode + " -> " + response.getStatusLine().getReasonPhrase();
 Log.d("Log", responseString);
 }

 } catch (ClientProtocolException e) {
 responseString = e.toString();
 } catch (IOException e) {
 responseString = e.toString();
 }
 Toast.makeText(Upload.this, "Risposta" + responseString, Toast.LENGTH_SHORT).show();
 //return responseString;
 }

 public void back(View view) {
 Intent intent = new Intent(Upload.this, MainActivity.class);
 startActivity(intent);

 }
 }*/
