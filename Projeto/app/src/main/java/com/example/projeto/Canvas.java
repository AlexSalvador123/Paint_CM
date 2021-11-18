package com.example.projeto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Canvas #newInstance} factory method to
 * create an instance of this fragment.
 */
public class Canvas extends Fragment{
    private static CanvasView cv;
    public static Paint brush= new Paint();

    private static FirebaseDatabase fd;
    private static DatabaseReference dr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fd = FirebaseDatabase.getInstance("https://paintapp123-default-rtdb.europe-west1.firebasedatabase.app");
        dr = fd.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GestureListener mGestureListener = new GestureListener();
        GestureDetector mGestureDetector = new GestureDetector(getActivity().getApplicationContext(), mGestureListener);
        mGestureDetector.setIsLongpressEnabled(true);
        mGestureDetector.setOnDoubleTapListener(mGestureListener);


        cv = new CanvasView(getActivity().getApplicationContext(), null, mGestureDetector);
        mGestureListener.setCanvas(cv);

        //setContentView(paintCanvas);// adds the created view to the screen

        //return rootView;

        return cv;
    }



    public void clean(){ cv.erase(); }

    public void changeColorPen(int i){ cv.currentColor(i); }

    public void changeBgcolor(int i){
        cv.bgColor(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveCanvas(String name){
        String paintName = null;
        if(name!=null){
            paintName = name;
        } else{
            paintName = dr.push().getKey();
        }
        cv.setDrawingCacheEnabled(true);
        Bitmap b = cv.getDrawingCache();
        /*if(b == null){
            b = loadLargeBitmapFromView(paintView);
        }*/
        Bitmap bitmap = Bitmap.createBitmap(b);

        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(b, 0, 0, null);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.getEncoder().encodeToString(byteArray);

        dr.child("paints").child(paintName).setValue(encodedImage);
        cv.destroyDrawingCache();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void loadCanvas(String encodedImage){
        cv.loadCanvas(encodedImage);
    }





}