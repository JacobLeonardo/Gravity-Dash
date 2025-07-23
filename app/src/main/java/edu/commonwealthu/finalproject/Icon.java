package edu.commonwealthu.finalproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Represents a game icon with physics-based movement and rendering capabilities.
 * Manages icon position, velocity, and collision detection in a game environment.
 *
 * @author Jacob Leonardo
 */

public class Icon {
    private Bitmap iconBitmap;
    private int x, y;
    private int velocity = 0;
    private final int GRAVITY = 2;


    /**
     * Constructs an Icon with a game icon bitmap from resources.
     * Scales the icon to a fixed size and sets initial positioning.
     *
     * @param res Android Resources used to decode the icon bitmap
     */
    public Icon(Resources res) {
        iconBitmap = BitmapFactory.decodeResource(res, R.drawable.game_icon);

        if (iconBitmap == null) {
            Log.e("Icon", "Error: game_icon resource could not be loaded.");
        } else {
            iconBitmap = Bitmap.createScaledBitmap(iconBitmap, 75, 75, true);
        }

        x = 100; // Starting x position
        y = 300; // Starting y position

    }


    /**
     * Updates the icon's position based on velocity and gravity.
     * Applies gravitational acceleration and prevents upward movement beyond screen bounds.
     */
    public void update() {
        velocity += GRAVITY / 2;
        y += velocity / 2;
        if (y < 0) y = 0; // Don't go above screen
    }

    /**
     * Applies an upward jump force to the icon by setting a negative velocity.
     * Simulates a jumping or upward movement action.
     */
    public void jump() {
        velocity = -30;
    }

    /**
     * Retrieves the current y-coordinate of the icon.
     *
     * @return Current y-position
     */
    public float getY() {
        return y;
    }


    /**
     * Draws the icon bitmap on the provided canvas.
     *
     * @param canvas Canvas to draw the icon on
     * @param paint Paint object for rendering customization
     */
    public void draw(Canvas canvas, Paint paint) {
        assert canvas != null;
        canvas.drawBitmap(iconBitmap, x, y, paint);
    }

    /**
     * Generates a rectangular collision shape based on the icon's current position and dimensions.
     *
     * @return Rectangular bounds of the icon for collision detection
     */
    public Rect getCollisionShape() {
        return new Rect(x, y, x + iconBitmap.getWidth(), y + iconBitmap.getHeight());
    }
}
