package com.kirill1636.chessmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kirill1636.chessmate.model.rest.AfterMoveStatus;
import com.kirill1636.chessmate.model.rest.Move;
import com.kirill1636.chessmate.model.rest.MoveResponse;
import com.kirill1636.chessmate.model.rest.User;
import com.kirill1636.chessmate.service.RestClientService;
import com.kirill1636.chessmate.ui.play.PlayFragment;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RegistrationActivity extends AppCompatActivity {

    private RestClientService restClientService = new RestClientService();
    private SharedPreferences pref;
    private String curToken = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        pref = getSharedPreferences("TABLE", MODE_PRIVATE);

        Intent intent = getIntent();
        String intentParam = intent.getStringExtra("token");
        curToken = intentParam;
        int a = 0;
        Button button = findViewById(R.id.buttonRegister);
        EditText nameArea = findViewById(R.id.editTextArea);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = nameArea.getText().toString();
                if(!name.equals("") && name.length() <= 20){
                     RegisterUserTask registerUserTask = new RegisterUserTask();
                     registerUserTask.execute(new User(0, name, intentParam, 0));
                }
            }
        });
    }

    private class RegisterUserTask extends AsyncTask<User, String, User> {
        @Override
        protected User doInBackground(User... params) {
            try {
                return restClientService.registerUser(params[0]);
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            User newUs = user;
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("token", curToken);
            editor.apply();
            int a = 0;
            System.out.println(user);
            Intent intent = new Intent(RegistrationActivity.this, GameActivity.class);
            intent.putExtra("id", user.getId());
            intent.putExtra("name", user.getName());
            intent.putExtra("rating", user.getRating());
            startActivity(intent);
        }
    }
}