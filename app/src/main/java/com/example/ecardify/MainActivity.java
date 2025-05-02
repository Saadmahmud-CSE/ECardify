package com.example.ecardify;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConstraintLayout main;
        main = findViewById(R.id.main);

        TextView dateText = findViewById(R.id.dateText);
        TextView timeText = findViewById(R.id.timeText);
        Button btnEid, btnBirthday, btnRamadan, btnYear;
        btnEid = findViewById(R.id.btnEid);
        btnBirthday = findViewById(R.id.btnBirthday);
        btnRamadan = findViewById(R.id.btnRamadan);
        btnYear = findViewById(R.id.btnYear);

        runnable = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());

                Date currentDate = new Date();
                dateText.setText(dateFormat.format(currentDate));
                timeText.setText(timeFormat.format(currentDate));

                // Repeat every second
                handler.postDelayed(this, 1000);
            }
        };

        // Start the runnable
        handler.post(runnable);

        // Animation
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        main.setVisibility(View.VISIBLE);
        main.startAnimation(fadeIn);
        Animation slideinl = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left);
        btnRamadan.setVisibility(View.VISIBLE);
        btnRamadan.startAnimation(slideinl);
        btnBirthday.setVisibility(View.VISIBLE);
        btnBirthday.startAnimation(slideinl);
        Animation slideinr = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        btnYear.setVisibility(View.VISIBLE);
        btnYear.startAnimation(slideinr);
        btnEid.setVisibility(View.VISIBLE);
        btnEid.startAnimation(slideinr);

        btnEid.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(),CustomizeActivity.class);
            Bundle translateBundle =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(),
                            R.anim.slide_in_left,
                            R.anim.slide_in_right).toBundle();
            i.putExtra("type","eid");
            startActivity(i,translateBundle);
        });
        btnRamadan.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(),CustomizeActivity.class);
            Bundle translateBundle =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(),
                            R.anim.slide_in_left,
                            R.anim.slide_in_right).toBundle();
            i.putExtra("type","ramadan");
            startActivity(i,translateBundle);
        });
        btnYear.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(),CustomizeActivity.class);
            Bundle translateBundle =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(),
                            R.anim.slide_in_left,
                            R.anim.slide_in_right).toBundle();
            i.putExtra("type","year");
            startActivity(i,translateBundle);
        });
        btnBirthday.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(),CustomizeActivity.class);
            Bundle translateBundle =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(),
                            R.anim.slide_in_left,
                            R.anim.slide_in_right).toBundle();
            i.putExtra("type","birthday");
            startActivity(i,translateBundle);
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Prevent memory leaks
    }
}