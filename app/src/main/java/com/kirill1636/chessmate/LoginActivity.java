package com.kirill1636.chessmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kirill1636.chessmate.model.rest.User;
import com.kirill1636.chessmate.service.RestClientService;
import com.vk.id.AccessToken;
import com.vk.id.VKID;
import com.vk.id.VKIDAuthFail;
import com.vk.id.auth.VKIDAuthParams;


public class LoginActivity extends AppCompatActivity {

    private RestClientService restClientService = new RestClientService();
    private String curToken = "";
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences("TABLE", MODE_PRIVATE);
        Button loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKID.Companion.setLogsEnabled(true);
                VKID vkid = new VKID(getApplicationContext());
                VKIDAuthParams authParams = new VKIDAuthParams.Builder().build();
                vkid.authorize(LoginActivity.this, new AuthCallback(), authParams);
            }
        });
    }
    class AuthCallback implements VKID.AuthCallback {
        @Override
        public void onSuccess(@NonNull AccessToken accessToken) {
            Log.i("VKID","accessToken: " + accessToken.getToken());
            Toast.makeText(getApplicationContext(),
                    accessToken.getUserData().getFirstName() + " " + accessToken.getUserData().getLastName(),
                    Toast.LENGTH_LONG).show();

            GetUserTask task = new GetUserTask();
            task.execute(accessToken.getToken());
            curToken = accessToken.getToken();
        }
        @Override
        public void onFail(@NonNull VKIDAuthFail vkidAuthFail) {
            Log.e("VKID",vkidAuthFail.toString());
        }
    }

    private class GetUserTask extends AsyncTask<String, String, User> {
        @Override
        protected User doInBackground(String... params) {
            try {
                return restClientService.loginByToken(params[0]);
            } catch (Exception e) {
                Log.e("LoginActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if(user == null){
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                intent.putExtra("token", curToken);
                curToken = "";
                startActivity(intent);
            }
            else{
                // Выполнение входа в приложение
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("token", curToken);
                editor.apply();
                System.out.println(user);
                int a = 0;
                Intent intent = new Intent(LoginActivity.this, GameActivity.class);
                intent.putExtra("id", user.getId());
                intent.putExtra("name", user.getName());
                intent.putExtra("rating", user.getRating());
                startActivity(intent);
            }
        }
    }
}