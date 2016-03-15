package com.example.alexa.objsense;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import java.util.GregorianCalendar;

/**
 * Created by Alexa on 3/12/2016.
 */
public class DrawableSurface extends View {

    private Rect touchableSurface = new Rect();
    private PointF[] points = {new PointF(0,0),new PointF(0,0),new PointF(0,0),new PointF(0,0)};
    PointF center;
    int buttonWidth;
    private boolean buttonEnabled = false;

    public DrawableSurface(Context context) {
        super(context);
    }

    public DrawableSurface(Context context, AttributeSet attrs){
        super(context, attrs);
    }


    public DrawableSurface(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // nothing gets drawn :(
        Paint paint = new Paint(); //Drawing to screen for debugging purposes
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);
        if(buttonEnabled) {
            calculateButtonPosition();

        }else{
            touchableSurface = new Rect();
            paint.setColor(Color.TRANSPARENT);
        }
        canvas.drawCircle(touchableSurface.centerX(), touchableSurface.centerY(), buttonWidth/2, paint);
    }

    public void setPoints(PointF[] points){
        this.points = points;
    }

    public boolean insideButton(int x, int y){
        Log.d("DEBUG", "CHECK:" + x + "," + y);
        return touchableSurface.contains(x,y);
    }

    public void setButtonState(boolean enabled){
        buttonEnabled = enabled;
    }

    private void calculateButtonPosition(){
        center = new PointF((points[0].x+points[2].x)/2,(points[0].y+points[2].y)/2);
        buttonWidth = (int) Math.sqrt(Math.pow(points[0].x-points[1].x,2)+Math.pow(points[0].y-points[1].y,2));
//        this.setX(center.x);
//        this.setY(center.y);
//        this.setTop((int) (center.y - buttonWidth / 2));
//        this.setRight((int) (center.x + buttonWidth / 2));
//        this.setBottom((int) (center.y + buttonWidth / 2));
//        this.setLeft( (int) (center.x - (buttonWidth / 2)));
        touchableSurface.set((int) (center.x - (buttonWidth / 2)), (int) (center.y - buttonWidth / 2), (int) (center.x + buttonWidth / 2), (int) (center.y + buttonWidth / 2));
    }


}
