package com.bahaiexplorer.toservehumanity.util;

import android.os.AsyncTask;
import android.util.Log;

import com.bahaiexplorer.toservehumanity.model.ConfigObjects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by briankurzius on 2/12/14.
 */
public class JsonUtils {

    public interface ConfigListener{
        public void configLoaded(ConfigObjects configObjects);
    }

    public final static String TAG = "JsonUtils";
    private ConfigListener mListener;

    /**
     * Creates a JSONObject from a valid location on the web
     * @param url
     *            the url of the file
     * @return JSONObject
     */
    public void getJSONfromURL(String url, ConfigListener listener) {
        mListener = listener;
        new GetJsonFromHTTP().execute(url);

    }

    public static <U> U convertToClassFromJson(Class<U> clazz, String jo) {

        U io = null;
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        try {
            io = gson.fromJson(jo, clazz);
        } catch (Error error) {
            Log.e(TAG, "error parsing. - error parsing object");
            io = null;
        } catch (JsonSyntaxException error){
            Log.e(TAG, "JsonSyntaxException error parsing. - error parsing object");
            io = null;
        }
        return io;
    }



    private class GetJsonFromHTTP extends AsyncTask<String, Void, ConfigObjects> {
        JSONObject jsonObj;
        ConfigObjects configObjs;
        @Override
        protected ConfigObjects doInBackground(String... params) {
            InputStream is = null;
            String result = "";
            JSONObject jArray = null;

            // http post
            try {
                HttpClient httpclient = new DefaultHttpClient();
                // HttpPost httppost = new HttpPost(url);
                HttpGet httpget = new HttpGet(params[0]);
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch (Exception e) {
                Log.e(TAG, "Error in http connection " + e.toString());
                return null;
            }

            // convert response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
                Log.i(TAG, "This is the json result: " + result);

                configObjs = JsonUtils.convertToClassFromJson(ConfigObjects
                        .class,
                        sb.toString());
                if(configObjs!=null){
                    Log.d(TAG, "GetJsonFromHTTP: processJSON: configObjs:" + configObjs.toString());
                }else{
                    Log.d(TAG, "GetJsonFromHTTP: WAS NULL");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error converting result " + e.toString());
                return null;
            }
            return configObjs;
        }

        @Override
        protected void onPostExecute(ConfigObjects result) {
           // Log.d(TAG,"onPostExecute:ConfigObjects: " + result.toString());
            // call listeners with result
            mListener.configLoaded(result);

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
