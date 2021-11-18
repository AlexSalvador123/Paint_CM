package com.example.projeto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import yuku.ambilwarna.AmbilWarnaDialog;

public class Background extends AppCompatActivity {
    RelativeLayout mLayout;
    public static int mDefault;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        mLayout = findViewById(R.id.layout2);
        mDefault = ContextCompat.getColor(this, R.color.white);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });
    }
    public void finish(View v,int color)
    {
        Intent data = new Intent();
        data.putExtra("color",
                color);
        setResult(RESULT_OK, data);
        super.finish();
    }
    public void openColorPicker() {
        AmbilWarnaDialog colorPicker= new AmbilWarnaDialog(this, mDefault, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDefault = color;
                finish(mLayout,color);
            }
        });
        colorPicker.show();
    }
}