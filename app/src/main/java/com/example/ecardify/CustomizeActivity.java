package com.example.ecardify;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomizeActivity extends AppCompatActivity {
    EditText etName,etMsg;
    Button btnPreview;
    String type;
    private Handler handler = new Handler();
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        type = getIntent().getStringExtra("type");
        TextView dateText = findViewById(R.id.dateText);
        TextView timeText = findViewById(R.id.timeText);
        etName = findViewById(R.id.etName);
        etMsg = findViewById(R.id.etMessage);
        btnPreview = findViewById(R.id.btnPreview);

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

        etName.setOnClickListener(v->{
            Animation zoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
            etName.setVisibility(View.VISIBLE);
            etName.startAnimation(zoomIn);
        });
        etMsg.setOnClickListener(v->{
            Animation zoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
            etMsg.setVisibility(View.VISIBLE);
            etMsg.startAnimation(zoomIn);
        });

        btnPreview.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(),PreviewActivity.class);
            i.putExtra("name",etName.getText().toString());
            i.putExtra("message",etMsg.getText().toString());
            i.putExtra("type",type);
            Animation zoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
            btnPreview.setVisibility(View.VISIBLE);
            btnPreview.startAnimation(zoomOut);
            Bundle translateBundle =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(),
                            R.anim.slide_in_left,
                            R.anim.slide_in_right).toBundle();
            startActivity(i,translateBundle);
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Prevent memory leaks
    }
}