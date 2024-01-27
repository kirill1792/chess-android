package com.kirill1636.chessmate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.kirill1636.chessmate.databinding.ActivityMainBinding;
import com.vk.id.AccessToken;
import com.vk.id.VKID;
import com.vk.id.VKIDAuthFail;
import com.vk.id.auth.VKIDAuthParams;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VKID.Companion.setLogsEnabled(true);
        VKID vkid = new VKID(getApplicationContext());
        VKIDAuthParams authParams = new VKIDAuthParams.Builder().build();
        vkid.authorize(this, new AuthCallback(), authParams);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
}