package org.sounddrive.sounddrivemobile.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.sounddrive.sounddrivemobile.R;

public class JoystickView extends View {

    private float joystickX;
    private float joystickY;
    private Paint paint;

    public JoystickView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        paint = new Paint();
        paint.setColor(Color.BLUE);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.JoystickView,
                0, 0);

        try {
            joystickX = a.getFloat(R.styleable.JoystickView_joystickX, 0);
            joystickY = a.getFloat(R.styleable.JoystickView_joystickY, 0);
        } finally {
            a.recycle();
        }
    }

    public float getJoystickX() {
        return joystickX;
    }

    public void setJoystickX(float joystickX) {
        this.joystickX = joystickX;
        invalidate();
        requestLayout();
    }

    public float getJoystickY() {
        return joystickY;
    }

    public void setJoystickY(float joystickY) {
        this.joystickY = joystickY;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float midX = canvas.getWidth() / 2;
        float midY = canvas.getHeight() / 2;
        float maxRadius = Math.min(midX, midY);

        float x = midX + (this.joystickX * maxRadius);
        float y = midY + (this.joystickY * maxRadius);
        canvas.drawCircle(x, y, 10, paint);
    }
}
