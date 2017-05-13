package david.itimecalendar.calendar.util;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by yuhaoliu on 29/08/16.
 */
public class Text2Drawable extends Drawable {
    private final String text;
    private final Paint paint;
    private final Rect bounds;

    public Text2Drawable(Paint paint, String text) {
        this.paint = paint;
        this.text = text;
        bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
    }

    @Override
    public int getIntrinsicHeight() {
        return bounds.height();
    }

    @Override
    public int getIntrinsicWidth() {
        return bounds.width();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(text, 0, bounds.height(), paint);
    }

    @Override
    public void setAlpha(int i) {
        paint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
