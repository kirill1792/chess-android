package com.kirill1636.chessmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.kirill1636.chessmate.databinding.ActivityGameBinding;
import com.kirill1636.chessmate.model.rest.User;
import com.vk.id.AccessToken;
import com.vk.id.VKID;
import com.vk.id.VKIDAuthFail;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityGameBinding binding;

    private Map<String, Object> activityData = new HashMap<>();

    public Map<String, Object> getActivityData() {
        return activityData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            Intent intent = getIntent();
            User user = new User();
            user.setId(intent.getIntExtra("id", -1));
            user.setName(intent.getStringExtra("name"));
            user.setRating(intent.getIntExtra("rating", -1));
            activityData.put("user", user);

            binding = ActivityGameBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setSupportActionBar(binding.appBarMain.toolbar);
            binding.appBarMain.fab.setOnClickListener(
                    view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show());
            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationView = binding.navView;
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                    .setOpenableLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            
            TextView playerName = navigationView.getHeaderView(0).findViewById(R.id.playerName);
            TextView playerRating = navigationView.getHeaderView(0).findViewById(R.id.playerRating);
            playerName.setText(user.getName());
            playerRating.setText(user.getRating().toString());
        } catch (Exception e) {
            Log.e("GameActivity", "Error while onCreate()");
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    class AuthCallback implements VKID.AuthCallback {
        @Override
        public void onSuccess(@NonNull AccessToken accessToken) {
            Log.i("VKID","accessToken: " + accessToken.getToken());
            Toast.makeText(getApplicationContext(),
                    accessToken.getUserData().getFirstName() + " " + accessToken.getUserData().getLastName(),
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFail(@NonNull VKIDAuthFail vkidAuthFail) {
            Log.e("VKID",vkidAuthFail.toString());
        }
    }

    public ActivityGameBinding getBinding() {
        return binding;
    }

    public void setBinding(ActivityGameBinding binding) {
        this.binding = binding;
    }
}