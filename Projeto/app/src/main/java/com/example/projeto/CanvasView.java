package com.example.projeto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import org.osmdroid.ResourceProxy;

import java.util.ArrayList;
import java.util.Base64;

public class CanvasView extends View implements View.OnTouchListener {
    private GestureDetector mGestureDetector;
    ViewGroup.LayoutParams params;
    private static ArrayList<Path> pathList = new ArrayList<>();
    private static ArrayList<Integer> colorList = new ArrayList<>();
    private static int actualColorPen = Color.BLACK;
    private static Path path = Screen.path;
    private static Paint brush = Canvas.brush;
    private Bitmap bitmap;

    public CanvasView(Context context) {
        super(context);

        brush.setAntiAlias(true);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(8f);
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        invalidate();
    }
    public CanvasView(Context context, AttributeSet attrs, GestureDetector mGestureDetector) {
        super(context, attrs);
        this.mGestureDetector = mGestureDetector;
        setOnTouchListener(this);
        brush.setAntiAlias(true);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(8f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX =event.getX();
        float pointY =event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(pointX, pointY);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                pathList.add(path);
                colorList.add(actualColorPen);
                break;
            default:
                return false;
        }
        invalidate();
        return false;
    }

    public void erase(){
        pathList.clear();
        colorList.clear();
        path.reset();
        invalidate();
        Screen.bitmap= null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadCanvas(String encodedImage){
        byte[] decodedString = Base64.getDecoder().decode(encodedImage);
        bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length).
                copy(Bitmap.Config.ARGB_8888, true);
        this.invalidate();
        Screen.bitmap = bitmap;
    }

    public void currentColor(int i){
        actualColorPen = i;
        path= new Path();
    }
    public void bgColor(int i){
        setBackgroundColor(i);
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        if(Screen.bitmap != null){
            canvas.drawBitmap(Screen.bitmap,0,0,null);}
        for(int i = 0; i < pathList.size(); i++){
            brush.setColor(colorList.get(i));
            canvas.drawPath(pathList.get(i), brush);// draws the path with the paint
            invalidate();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mGestureDetector.onTouchEvent(motionEvent);
        return false;
    }
}
