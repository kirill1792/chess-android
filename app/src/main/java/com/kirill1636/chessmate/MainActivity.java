package com.kirill1636.chessmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.kirill1636.chessmate.model.rest.User;
import com.kirill1636.chessmate.service.RestClientService;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private RestClientService restClientService = new RestClientService();
    private String curToken = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Некая логика проверки наличия сохранённого на устройстве токена
        pref = getSharedPreferences("TABLE", MODE_PRIVATE);
        curToken = pref.getString("token", "None");

        if(!curToken.equals("None")){
            // Выполняется вход в аккаут пользователя
            GetUserTask task = new GetUserTask();
            task.execute(curToken);
        }
        else{
            // Переход на логин
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
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
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                curToken = "";
                startActivity(intent);
            }
            else{
                // Выполнение входа в приложение
                System.out.println(user);
                int a = 0;
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("id", user.getId());
                intent.putExtra("name", user.getName());
                intent.putExtra("rating", user.getRating());
                startActivity(intent);
            }
        }
    }
}