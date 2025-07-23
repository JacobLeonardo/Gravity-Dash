package edu.commonwealthu.finalproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Custom View that creates an animated pixelated rain background
 * for the splash screen with randomized blue-toned raindrops.
 *
 * @author Jacob Leonardo
 */
public class SplashBackground extends View {
    private List<RainDrop> rainDrops;
    private Paint paint;
    private Random random;
    private int width, height;
    private static final int MAX_DROPS = 100;
    private static final int DROP_SIZE = 10;

    /**
     * Constructor for creating SplashBackground programmatically.
     *
     * @param context The Context in which the view is created
     */
    public SplashBackground(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor for creating SplashBackground from XML layout.
     *
     * @param context The Context in which the view is created
     * @param attrs Attributes set from XML
     */
    public SplashBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Initializes background components including raindrop list, paint, and random generator.
     */
    private void init() {
        rainDrops = new ArrayList<>();
        paint = new Paint();
        random = new Random();
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Generates raindrops when view size is determined.
     *
     * @param w Current width of view
     * @param h Current height of view
     * @param oldw Previous width of view
     * @param oldh Previous height of view
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        generateRainDrops();
    }

    /**
     * Creates a collection of raindrops with random positions and blue shades.
     */
    private void generateRainDrops() {
        rainDrops.clear();
        for (int i = 0; i < MAX_DROPS; i++) {
            RainDrop drop = new RainDrop(
                    random.nextInt(width),
                    -random.nextInt(height),
                    DROP_SIZE,
                    getRandomBlueShade()
            );
            rainDrops.add(drop);
        }
    }

    /**
     * Generates a random blue color shade for raindrops.
     *
     * @return Randomly generated blue color
     */
    private int getRandomBlueShade() {
        int blueIntensity = random.nextInt(100) + 100;
        return android.graphics.Color.rgb(
                50,  // Low red
                80,  // Low green
                blueIntensity  // Varied blue
        );
    }

    /**
     * Draws and animates raindrops on the canvas.
     *
     * @param canvas Canvas to draw raindrops on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (RainDrop drop : rainDrops) {
            paint.setColor(drop.color);

            canvas.drawRect(
                    drop.x,
                    drop.y,
                    drop.x + DROP_SIZE,
                    drop.y + DROP_SIZE,
                    paint
            );

            drop.y += drop.speed;

            if (drop.y > height) {
                drop.y = -random.nextInt(height);
                drop.x = random.nextInt(width);
            }
        }

        invalidate();
    }

    private static class RainDrop {
        float x, y;
        int color;
        int speed;

        RainDrop(float x, float y, int size, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.speed = size + new Random().nextInt(5) + 2;
        }
    }
}