package edu.commonwealthu.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom View class that manages a scrolling background for a game.
 * Handles background image scaling, continuous scrolling, and rendering.
 *
 * @author Jacob Leonardo
 */

public class Background extends View {

    private Bitmap background;
    private Bitmap scaledBackground;
    private int backgroundX = 0;
    private int scrollSpeed = 7;

    /**
     * Constructor for creating Background in code without XML attributes.
     *
     * @param context The Context in which the view is created
     */
    public Background(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor for creating Background from XML layout.
     *
     * @param context The Context in which the view is created
     * @param atr Attributes set from XML
     */
    public Background(Context context, AttributeSet atr) {
        super(context, atr);
        init();
    }

    /**
     * Initializes background bitmap and ensures view can be drawn.
     */
    public void init() {
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        setWillNotDraw(false);
    }

    /**
     * Scales background bitmap when view size is determined.
     *
     * @param w Current width of view
     * @param h Current height of view
     * @param oldw Previous width of view
     * @param oldh Previous height of view
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (background != null) {
            scaledBackground = Bitmap.createScaledBitmap(background, w, h, false);
        }
    }

    /**
     * Renders scrolling background by drawing two background bitmaps.
     * Implements continuous scrolling effect with automatic reset.
     *
     * @param canvas Canvas to draw background on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (scaledBackground != null) {
            int canvasWidth = canvas.getWidth();

            canvas.drawBitmap(scaledBackground, backgroundX, 0, null);
            canvas.drawBitmap(scaledBackground, backgroundX + canvasWidth, 0, null);

            backgroundX -= scrollSpeed;

            if (backgroundX <= -canvasWidth) {
                backgroundX = 0;
            }

            postInvalidateDelayed(16); // ~60 FPS
        }
    }

}
