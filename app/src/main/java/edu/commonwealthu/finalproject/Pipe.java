package edu.commonwealthu.finalproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;

import java.util.Random;

/**
 * Represents a pair of pipes in a side-scrolling game with randomized gap positioning.
 * Manages pipe rendering, movement, and collision detection.
 *
 * @author Jacob Leonardo
 */

public class Pipe {
    private Bitmap topPipe, bottomPipe;
    private int x, gapY, gap, screenHeight, screenWidth;
    private int pipeWidth = 75;
    private static final int PIPE_SPEED = 7;
    private Random random;
    private Matrix matrix;

    /**
     * Constructs a Pipe with specified parameters and generates pipe bitmaps.
     *
     * @param res Android Resources for bitmap loading
     * @param startX Initial horizontal starting position
     * @param screenHeight Total screen height
     * @param screenWidth Total screen width
     * @param gap Size of the gap between pipes
     */
    public Pipe(Resources res, int startX, int screenHeight, int screenWidth, int gap) {
        random = new Random();
        matrix = new Matrix();

        Bitmap originalPipe = BitmapFactory.decodeResource(res, R.drawable.bottom_pipe);

        if (originalPipe != null) {
            float scale = (float) pipeWidth / originalPipe.getWidth();
            int scaledHeight = (int) (originalPipe.getHeight() * scale);

            bottomPipe = Bitmap.createScaledBitmap(originalPipe, pipeWidth, scaledHeight, true);

            matrix.setScale(1, -1);
            topPipe = Bitmap.createBitmap(bottomPipe, 0, 0, bottomPipe.getWidth(),
                    bottomPipe.getHeight(), matrix, true);

            originalPipe.recycle();
        }

        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.gap = gap;
        this.x = startX;
        resetPosition(startX);
    }

    /**
     * Resets pipe position with randomized gap location.
     *
     * @param startX Horizontal starting position
     */
    public void resetPosition(int startX) {
        x = startX;
        int minGapY = gap + 100; // Minimum distance from top of screen
        int maxGapY = screenHeight - gap - 100; // Maximum distance from top of screen
        gapY = minGapY + random.nextInt(Math.max(1, maxGapY - minGapY));
    }

    /**
     * Updates pipe's horizontal position based on movement speed.
     */
    public void update() {
        x -= PIPE_SPEED;
    }

    /** @return Current horizontal position of the pipe */
    public int getX() { return x; }

    /** @return Width of the pipe */
    public int getWidth() { return pipeWidth; }

    /**
     * Generates collision shape for the top pipe.
     *
     * @return Rectangular bounds of the top pipe
     */
    public Rect getTopCollisionShape() {
        return new Rect(x, 0, x + pipeWidth, gapY);
    }

    /**
     * Generates collision shape for the bottom pipe.
     *
     * @return Rectangular bounds of the bottom pipe
     */
    public Rect getBottomCollisionShape() {
        return new Rect(x, gapY + gap, x + pipeWidth, screenHeight);
    }
}