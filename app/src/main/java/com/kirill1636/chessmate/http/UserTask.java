package com.kirill1636.chessmate.http;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class UserTask extends AsyncTask<String, String, String> {

    private final String SERVER_HOST = "188.225.33.112";
    // private final String SERVER_HOST = "10.0.2.2";
    private final String SERVER_URL = "http://" + SERVER_HOST + ":8080/";
    //rivate final String SERVER_URL = "http://192.168.29.76:8000";
    @Override
    protected String doInBackground(String... params) {
        try {
            final String url =  SERVER_URL + "login?token={token}";
            RestTemplate restTemplate = new RestTemplate();
            //restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            String res = restTemplate.getForObject(url, String.class, params[0]);
            return res;
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String r) {
        System.out.println(r);
    }
}