package edu.commonwealthu.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Splash screen activity that displays a logo and text with animations
 * before transitioning to the main activity of the application.
 *
 * @author Jacob Leonardo
 */
public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DELAY = 5000; // 5 seconds

    /**
     * Called when the activity is first created. Sets up the splash screen
     * with logo and text animations, then transitions to the main activity.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logoView = findViewById(R.id.splash_logo);
        TextView textView = findViewById(R.id.splash_text);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);

        fadeIn.setDuration(1500);
        scaleUp.setDuration(1500);

        logoView.startAnimation(scaleUp);
        textView.startAnimation(fadeIn);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, SPLASH_DELAY);
    }
}