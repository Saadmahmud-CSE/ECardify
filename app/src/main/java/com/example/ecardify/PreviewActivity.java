package com.example.ecardify;

import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.io.File;
import java.io.FileOutputStream;

import pl.droidsonroids.gif.GifImageView;

public class PreviewActivity extends AppCompatActivity {
    GifImageView gifView;
    TextView tvGreeting, tvMessage;
    ImageView bgImage;
    ConstraintLayout cardLayout;
    Button btnShare, btnGift, btnSave, back;
    String name, message, type;
    TextToSpeech tts;
    private Handler handler = new Handler();
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvMessage = findViewById(R.id.tvMessage);
        bgImage = findViewById(R.id.bgImage);
        cardLayout = findViewById(R.id.cardLayout);
        btnShare = findViewById(R.id.btnShare);
        btnGift = findViewById(R.id.btnGift);
        btnSave = findViewById(R.id.btnSave);
        back = findViewById(R.id.back);
        gifView = findViewById(R.id.gifView);

        TextView dateText = findViewById(R.id.dateText);
        TextView timeText = findViewById(R.id.timeText);

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

        // Get data
        name = getIntent().getStringExtra("name");
        message = getIntent().getStringExtra("message");
        type = getIntent().getStringExtra("type");

        // Set content
        if ("eid".equals(type)) {
            tvGreeting.setText("Eid Mubarak, " + name + "!");
            bgImage.setImageResource(R.drawable.eid1);
        } else if("ramadan".equals(type)){
            tvGreeting.setText("Ramadan Mubarak, " + name + "!");
            bgImage.setImageResource(R.drawable.ramadan);
        } else if("year".equals(type)){
            tvGreeting.setText("Happy New Year, " + name + "!");
            bgImage.setImageResource(R.drawable.year);
        } else {
            tvGreeting.setText("Happy Birthday, " + name + "!");
            bgImage.setImageResource(R.drawable.bd2);
        }
        //TextToSpeech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.ENGLISH);
                tts.setSpeechRate(1.0f);

                String speechText = tvGreeting.getText().toString();
                tts.speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        // Animation
        Animation blink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        tvGreeting.setVisibility(View.VISIBLE);
        tvGreeting.startAnimation(blink);

        // Gift Card
        btnGift.setOnClickListener(v->{
            Animation bounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
            btnGift.setVisibility(View.VISIBLE);
            btnGift.startAnimation(bounce);
            Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
            btnGift.setVisibility(View.VISIBLE);
            btnGift.startAnimation(fadeOut);
            tvMessage.setText(message);

            // Show GIF based on card type
            gifView.setVisibility(View.VISIBLE);
            if ("eid".equals(type)) {
                gifView.setImageResource(R.drawable.eid); // Eid gif
            } else if("ramadan".equals(type)) {
                gifView.setImageResource(R.drawable.ramadan1); // Ramadan gif
            }
            else if("year".equals(type)) {
                gifView.setImageResource(R.drawable.year1); // Happy New Year gif
            } else {
                gifView.setImageResource(R.drawable.birth); // Birthday gif
            }
        });
        gifView.setOnClickListener(v->{
            Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            gifView.setVisibility(View.VISIBLE);
            gifView.startAnimation(fadeIn);
        });

        back.setOnClickListener(v->{
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            Bundle translateBundle =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(),
                            R.anim.slide_in_left,
                            R.anim.slide_in_right).toBundle();
            startActivity(i,translateBundle);
        });

        // Save Card
        btnSave.setOnClickListener(v->{
            saveCardToGallery(cardLayout);
            Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
            btnSave.setVisibility(View.VISIBLE);
            btnSave.startAnimation(fadeOut);
        });
        // Share card
        btnShare.setOnClickListener(v -> {
            Bitmap bitmap = Bitmap.createBitmap(cardLayout.getWidth(), cardLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            cardLayout.draw(canvas);
            Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
            btnShare.setVisibility(View.VISIBLE);
            btnShare.startAnimation(fadeOut);
            try {
                File file = new File(getExternalCacheDir(), "ecard.png");
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();

                Uri uri = FileProvider.getUriForFile(
                        PreviewActivity.this,
                        getPackageName() + ".fileprovider",
                        file
                );

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/png");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(share, "Share via"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void saveCardToGallery(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "ecard_" + System.currentTimeMillis() + ".png");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ECardify");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                fos = resolver.openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

                Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();
            } else {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ECardify");
                if (!file.exists()) {
                    file.mkdirs();
                }
                File image = new File(file, "ecard_" + System.currentTimeMillis() + ".png");
                fos = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

                // Make it visible in Gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(image));
                sendBroadcast(mediaScanIntent);

                Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to Save", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Prevent memory leaks
    }
}
