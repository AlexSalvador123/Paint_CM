package com.example.projeto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Handler h = new Handler();
    private Runnable r = new Runnable() {

        @Override
        public void run() {
            // if you are redirecting from a fragment then use getActivity() as the context.
            startActivity(new Intent(MainActivity.this, Screen.class));

        }
    };
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The Runnable will be executed after the given delay time
        h.postDelayed(r, 3000);
    }

    public void GoToScreen (View view){
        h.removeCallbacks(r);
        Intent i = new Intent(this, Screen.class);
        startActivity(i);
        finish();
    }
}
