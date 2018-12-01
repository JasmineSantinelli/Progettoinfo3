package com.example.jasmine.progettoinfo3;

import android.os.AsyncTask;
import android.util.Log;

import com.android.internal.http.multipart.MultipartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.FormBodyPart;
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
        HttpPost httppost = new HttpPost("http://progettoscandurra.andreacavagna.it/caricacellulareunsafe");

        try {
            File file = new File(path);
            MultipartEntityBuilder entityBuilder=MultipartEntityBuilder.create();
            entityBuilder.addBinaryBody("file",file);
            HttpEntity entity=entityBuilder.build();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();
            String result=EntityUtils.toString(r_entity);
            Log.v("results",result);
            /**
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
**/
        } catch (ClientProtocolException e) {
            responseString = e.toString();
            Log.v("exception",responseString);
        } catch (IOException e) {
            responseString = e.toString();
            Log.v("exception",responseString);
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