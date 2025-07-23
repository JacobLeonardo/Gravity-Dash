package edu.commonwealthu.finalproject;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;


/**
 * MainActivity for a mobile game application, managing game initialization,
 * gameplay mechanics, sound, and user interactions.
 *
 * This activity handles the primary game loop, including:
 * - Game start and transition
 * - Character movement and jumping
 * - Pipe generation and collision detection
 * - Scoring system
 * - Sound and music management
 *
 * @author Jacob Leonardo
 */
public class MainActivity extends AppCompatActivity {
    private FrameLayout gameViewContainer;
    private Button startGameButton;
    private SoundManager soundManager;
    private MediaPlayer mediaPlayer;
    private ImageView gameIcon;
    private ImageView startGameIcon;
    private ImageView topPipe, bottomPipe;
    private ImageButton play;
    private TextView score, dialogScore;
    private Icon gameCharacter;
    private List<Pipe> pipes = new ArrayList<>();
    private boolean isGameRunning = false;
    private boolean hasPassed = false;
    private int points = 0;
    private static final int SCREEN_UPDATE_INTERVAL = 17; // ~60 FPS


    /**
     * Initializes the activity, sets up UI components, and prepares the game environment.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUIComponents();
        playIdleAnimation(gameIcon);
        startGameButton.setOnClickListener(v -> startGameAnimations());

    }

    /**
     * Initializes all UI components and sets up initial game configurations.
     * This includes toolbar setup, sound initialization, and referencing layout elements.
     */
    private void initializeUIComponents() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleStyle);
        setSupportActionBar(toolbar);

        soundManager = new SoundManager(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music3);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        gameViewContainer = findViewById(R.id.gameViewContainer);
        startGameButton = findViewById(R.id.startGameButton);
        gameIcon = findViewById(R.id.gameIcon);
        startGameIcon = findViewById(R.id.startGameIcon);
        topPipe = findViewById(R.id.top_pipe);
        bottomPipe = findViewById(R.id.bottom_pipe);
        score = findViewById(R.id.game_points);
    }

    /**
     * Triggers the initial game start animations and prepares the game environment.
     * Hides menu elements, shows game components, and sets up game loop and touch listeners.
     */
    private void startGameAnimations() {
        gameIcon.setVisibility(View.GONE);
        startGameButton.setVisibility(View.GONE);

        gameViewContainer.setVisibility(View.VISIBLE);
        startGameIcon.setVisibility(View.VISIBLE);
        topPipe.setVisibility(View.VISIBLE);
        bottomPipe.setVisibility(View.VISIBLE);

        gameCharacter = new Icon(getResources());
        isGameRunning = true;
        updatePoints(0);


        gameViewContainer.post(() -> {
            if (gameViewContainer.getWidth() > 0 && gameViewContainer.getHeight() > 0) {
                initializePipes();
                startGameLoop();
            }
        });

        gameViewContainer.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && isGameRunning) {
                gameCharacter.jump();
                soundManager.playPipeSound();

                startGameIcon.setY(gameCharacter.getY());
            }
            return true;
        });
    }

    /**
     * Initializes game pipes with predefined spacing across the game screen.
     * Creates a set of pipes positioned at specific intervals for gameplay.
     */
    private void initializePipes() {
        pipes.clear();
        final int PIPE_GAP = 425;
        int screenWidth = gameViewContainer.getWidth();
        int screenHeight = gameViewContainer.getHeight();

        for (int i = 0; i < 3; i++) {
            pipes.add(new Pipe(
                    getResources(),
                    screenWidth + i * 400,
                    screenHeight,
                    screenWidth,
                    PIPE_GAP
            ));
        }
    }

    /**
     * Starts the main game loop, updating game character and pipe positions,
     * and checking for collisions at regular intervals.
     */
    private void startGameLoop() {
        startGameIcon.setAlpha(1.0f);

        Runnable gameLoop = new Runnable() {
            @Override
            public void run() {
                if (isGameRunning && startGameIcon.getVisibility() == View.VISIBLE) {
                    gameCharacter.update();

                    float newY = gameCharacter.getY();
                    if (Math.abs(startGameIcon.getY() - newY) > 1.0f) {
                        startGameIcon.setY(newY);
                    }

                    updatePipes();
                    checkCollisions();

                    if (isGameRunning) {
                        gameViewContainer.postDelayed(this, SCREEN_UPDATE_INTERVAL);
                    }
                }
            }
        };

        startGameIcon.setX(100);
        gameViewContainer.post(gameLoop);
    }

    /**
     * Checks for collisions between the game character and pipes or screen boundaries.
     * Triggers collision handling if any intersection is detected.
     */
    private void checkCollisions() {
        Rect iconRect = new Rect(
                (int) startGameIcon.getX(),
                (int) startGameIcon.getY(),
                (int) (startGameIcon.getX() + startGameIcon.getWidth()),
                (int) (startGameIcon.getY() + startGameIcon.getHeight())
        );

        if (topPipe.getVisibility() == View.VISIBLE && bottomPipe.getVisibility() == View.VISIBLE) {
            Rect topPipeRect = new Rect(
                    (int) topPipe.getX(),
                    (int) topPipe.getY(),
                    (int) (topPipe.getX() + topPipe.getWidth()),
                    (int) (topPipe.getY() + topPipe.getLayoutParams().height)
            );

            Rect bottomPipeRect = new Rect(
                    (int) bottomPipe.getX(),
                    (int) bottomPipe.getY(),
                    (int) (bottomPipe.getX() + bottomPipe.getWidth()),
                    (int) (bottomPipe.getY() + bottomPipe.getLayoutParams().height)
            );

            if (Rect.intersects(iconRect, topPipeRect) ||
                    Rect.intersects(iconRect, bottomPipeRect)) {
                handleCollision();
            }
        }

        if (startGameIcon.getY() <= 0 ||
                startGameIcon.getY() + startGameIcon.getHeight() >= gameViewContainer.getHeight()) {
            handleCollision();
        }
    }

    /**
     * Handles game character collision by stopping the game,
     * playing collision sound, and showing the game over dialog.
     */
    private void handleCollision() {
        if (isGameRunning) {
            soundManager.playCollisionSound();
            isGameRunning = false;

            startGameIcon.setVisibility(View.GONE);
            gameViewContainer.setOnTouchListener(null);
            showDeadDialog();
            points = 0;
        }
    }

    /**
     * Updates pipe positions, manages pipe recycling, and tracks player's score.
     * Handles pipe movement, repositioning, and point scoring logic.
     */
    private void updatePipes() {
        for (Pipe pipe : pipes) {
            pipe.update();

            if (pipe == pipes.get(0)) {
                topPipe.setX(pipe.getX());
                topPipe.setY(0);
                topPipe.getLayoutParams().height = pipe.getTopCollisionShape().height();

                bottomPipe.setX(pipe.getX());
                Rect bottomRect = pipe.getBottomCollisionShape();
                bottomPipe.setY(bottomRect.top);
                bottomPipe.getLayoutParams().height = bottomRect.height();

                float iconX = startGameIcon.getX();
                float iconY = startGameIcon.getY();
                float pipeX = pipe.getX();

                boolean inGapVertically = iconY > topPipe.getHeight() &&
                        iconY + startGameIcon.getHeight() < bottomPipe.getY();

                if (!hasPassed && inGapVertically && iconX > pipeX + topPipe.getWidth()) {
                    points++;
                    soundManager.playPointSound();
                    updatePoints(points);
                    hasPassed = true;
                }

                if (pipe.getX() + pipe.getWidth() < 0) {
                    hasPassed = false;
                }

                topPipe.requestLayout();
                bottomPipe.requestLayout();
            }

            if (pipe.getX() + pipe.getWidth() < 0) {
                int rightmostX = -1;
                for (Pipe p : pipes) {
                    rightmostX = Math.max(rightmostX, p.getX());
                }
                pipe.resetPosition(rightmostX + 400);
            }
        }
    }

    /**
     * Starts the main game view and initializes game components.
     */
    private void startGame() {
        soundManager.playPipeSound();
        gameViewContainer.setVisibility(View.VISIBLE);
        startGameButton.setVisibility(View.GONE);
    }

    /**
     * Plays a subtle floating animation for the game icon.
     *
     * @param iconImage The ImageView to animate
     */
    public static void playIdleAnimation(final ImageView iconImage) {
        ObjectAnimator floatAnimator = ObjectAnimator.ofFloat(
                iconImage,
                "translationY",
                0f, -20f, 0f
        );

        floatAnimator.setDuration(1500);
        floatAnimator.setRepeatCount(ValueAnimator.INFINITE);
        floatAnimator.setRepeatMode(ValueAnimator.REVERSE);
        floatAnimator.start();
    }

    /**
     * Updates the displayed game points on the screen.
     *
     * @param p The current points to display
     */
    private void updatePoints(int p) {
        ((TextView)score).setText(String.valueOf(p));
    }


    /**
     * Called when the activity is paused. Stops game and shows pause dialog.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (isGameRunning) {
            showPauseDialog();
        }
        isGameRunning = false;
    }

    /**
     * Called when the activity is resumed. Restarts game state.
     */
    @Override
    protected void onResume() {
        super.onResume();
        isGameRunning = true;
    }

    /**
     * Called when the activity is destroyed. Performs cleanup.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Creates the options menu for the activity.
     *
     * @param menu The options menu in which the items are placed
     * @return True for the menu to be displayed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * The options menu of the app
     * @param item The selected menu item
     * @return The selected menu item
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_sound_option) {
            handleSoundOption(item);
        } else if (id == R.id.menu_exit) {
            showExitDialog();
        } else if (id == R.id.menu_pause_option) {
            onPause();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Enables and disables sound
     * @param item the menuItem being referenced
     */
    private void handleSoundOption(MenuItem item) {
        if(soundManager.isSoundEnabled()) {
            item.setIcon(R.drawable.no_sound);
            mediaPlayer.pause();
        } else {
            item.setIcon(R.drawable.sound);
            mediaPlayer.start();
        }
        soundManager.toggleSoundEnabled();
    }


    /**
     * Shows an exit dialog asking if the user wants to exit (if so, app terminates)
     */
    private void showExitDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_exit, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (v, n) -> {
                    mediaPlayer.stop();
                    finish();
                });
        AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.holo_purple);
        }
        dialog.setCanceledOnTouchOutside(false);
    }

    /**
     * Shows an exit dialog asking if the user wants to exit (if so, app terminates)
     */
    private void showDeadDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dead_dialog, null);

        dialogScore = dialogView.findViewById(R.id.dialog_game_points);
        dialogScore.setText(String.valueOf(points));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setNegativeButton(R.string.exit_game, (v, n) -> {
                    mediaPlayer.stop();
                    finish();
                })
                .setPositiveButton(R.string.new_game, (v, n) -> startGameAnimations());
        AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.holo_purple);
        }
        dialog.setCanceledOnTouchOutside(false);
    }

    /**
     * Shows a pause dialog that lets user resume playing with a play button
     */
    private void showPauseDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pause_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        play = dialogView.findViewById(R.id.play_button);
        dialog.show();
        play.setOnClickListener(v -> {
            dialog.dismiss();

            // Restart the game loop
            isGameRunning = true;
            startGameLoop();

            gameViewContainer.setOnTouchListener((v1, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN && isGameRunning) {
                    gameCharacter.jump();
                    soundManager.playPipeSound();
                    startGameIcon.setY(gameCharacter.getY());
                }
                return true;
            });
        });

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.holo_purple);
        }
        dialog.setCanceledOnTouchOutside(false);
    }

}