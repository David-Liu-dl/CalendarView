package david.itimecalendar.calendar.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import david.itimecalendar.R;


/**
 * Created by yuhaoliu on 15/03/2017.
 */

public class RoundedImageView extends android.support.v7.widget.AppCompatImageView{
    private PaintFlagsDrawFilter filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    //px
    public  int numberSize = 10;
    private int number;
    private Rect bounds = new Rect();
    private String str;
    private final Paint numberPaint = new Paint();
    private final Paint borderPaint = new Paint();
    private int borderColor = Color.BLACK;
    private int borderWidth = 2;
    private int bgColor = Color.TRANSPARENT;

    public RoundedImageView(Context ctx) {
        super(ctx);
        init();
    }

    public RoundedImageView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init();
    }

    private void init(){
        numberPaint.setStyle(Paint.Style.FILL);
        numberPaint.setColor(getResources().getColor(R.color.image_number_grey));
        numberPaint.setTextSize(DensityUtil.dip2px(getContext(),numberSize));
        numberPaint.setAntiAlias(true);
        str = "+ " + String.valueOf(number);
        numberPaint.getTextBounds(str, 0, str.length(), bounds);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setTextSize(DensityUtil.dip2px(getContext(),0.5f));
        borderPaint.setAntiAlias(true);
//        borderPaint.setFilterBitmap(true);
//        borderPaint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int pdLeft = getPaddingLeft();
        int pdTop = getPaddingTop();
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        int w = getWidth() - pdLeft*2, h = getHeight() - pdTop*2;


        Bitmap roundBitmap = getRoundedCroppedBitmap(getBitmap(),w);
        canvas.setDrawFilter(filter);
        canvas.drawBitmap(roundBitmap, pdLeft, pdTop, null);

        int canvasRealW = canvas.getWidth() - pdLeft*2;
        int canvasRealH = canvas.getHeight() - pdTop*2;

        int xPos = (canvasRealW / 2);
        int yPos = (int) ((canvasRealH / 2) - ((numberPaint.descent() + numberPaint.ascent()) / 2));

        canvas.drawText(str,pdLeft + xPos - bounds.width() / 2, pdTop + yPos,numberPaint);

        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvasRealW/2 - borderWidth/2, borderPaint);
    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap;
        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius,
                    false);
        else
            finalBitmap = bitmap;
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(),
                finalBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.RED);
        canvas.drawCircle(finalBitmap.getWidth() / 2,
                finalBitmap.getHeight() / 2,
                finalBitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        str = "+" + String.valueOf(this.number);
        numberPaint.getTextBounds(str, 0, str.length(), bounds);
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    private Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(bgColor);

        return bitmap;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }
}
