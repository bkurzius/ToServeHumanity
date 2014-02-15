package com.bahaiexplorer.toservehumanity.util;

import android.os.AsyncTask;
import android.util.Log;

import com.bahaiexplorer.toservehumanity.BuildConfig;
import com.bahaiexplorer.toservehumanity.model.ConfigObjects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by briankurzius on 2/12/14.
 */
public class JsonUtils {

    public final static String TAG = "JsonUtils";

    /**
     * Creates a JSONObject from a valid location on the web
     * @param url
     *            the url of the file
     * @return JSONObject
     */
    public void getJSONfromURL(String url) {
        new GetJsonFromHTTP().execute("http://bahaiexplorer.com/toservehumanity/config.json");
    }

    public static <U> U convertToClassFromJson(Class<U> clazz, String jo) {

        U io = null;
        Gson gson = new GsonBuilder().create();
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



    private class GetJsonFromHTTP extends AsyncTask<String, Void, JSONObject> {
        JSONObject jsonObj;
        @Override
        protected JSONObject doInBackground(String... params) {
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

                ConfigObjects configObjs = JsonUtils.convertToClassFromJson(ConfigObjects
                        .class,
                        sb.toString());
                if(configObjs!=null){
                    Log.d(TAG, "GetJsonFromHTTP: processJSON: configObjs:" + configObjs.toString());
                Log.d(TAG,"configObjs.configObjects.get(0).projectName: " + configObjs
                        .configObjects.get(0).projectName);
                }else{
                    Log.d(TAG, "GetJsonFromHTTP: WAS NULL");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error converting result " + e.toString());
                return null;
            }
            // try parse the string to a JSON object
            // if we can't parse it because there are no results then
            // create an empty JSONObject
            // otherwise return null so we can display the error
            try {
                jArray = new JSONObject(result);
            } catch (JSONException e) {
                if(BuildConfig.DEBUG){
                    Log.d(TAG, "JsonUtils.getJSONfromURL():"+e.getMessage());
                }
                jArray = null;
            }
            jsonObj = jArray;
            return jArray;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Log.d(TAG,"onPostExecute:jsonObj: " + result.toString());

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
