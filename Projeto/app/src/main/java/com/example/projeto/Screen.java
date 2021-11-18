package com.example.projeto;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import yuku.ambilwarna.AmbilWarnaDialog;

public class Screen extends AppCompatActivity implements SensorEventListener {
    RelativeLayout mLayout;
    RelativeLayout cLayout;

    public static int color = Color.BLACK;
    public static Path path = new Path();
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor accelerometer;
    private float lastLight;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 3000;
    public static int frag = 0;
    public static Paint brush = Canvas.brush;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    Canvas fragmentCanvas;
    Pallete fragmentPallete;
    public static Bitmap bitmap;
    public static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        mLayout = findViewById(R.id.layout2);
        mLayout.setBackgroundColor(Background.mDefault);
        fragmentCanvas = new Canvas();
        fragmentPallete = new Pallete();
        //cLayout = (FragmentContainerView) findViewById(R.id.fragment_container);
        //cLayout.setBackgroundColor(Color.GREEN);
        //
        if (findViewById(R.id.fragment_container2) != null) {
            fragmentTransaction.replace(R.id.fragment_container, fragmentCanvas);
            fragmentTransaction.replace(R.id.fragment_container2, fragmentPallete);
        }
        fragmentTransaction.commit();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    public void switchFrag(View view){
        if (frag == 0) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentPallete, null)
                    .addToBackStack(null) // name can be null
                    .commit();
            frag = 1;
        }else if (frag ==1){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentCanvas, null)
                    .addToBackStack(null) // name can be null
                    .commit();
            frag = 0;
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveCanvas(View v){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        builder.setTitle("Name")
                .setView(input)
                .setNegativeButton("cancel", null)
                .setPositiveButton("done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String username = input.getText().toString();
                        fragmentCanvas.saveCanvas(username);
                    }
                });
        builder.show();
    }

    public void loadCanvas(View v){
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://paintapp123-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference databaseReference = mDatabase.getReference();
        Task t = databaseReference.child("paints").get();
        t.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                try {
                    DataSnapshot dataSnapshot = (DataSnapshot) t.getResult();

                    AlertDialog.Builder builder = new AlertDialog.Builder(Screen.this);
                    builder.setTitle("Choose a draw!");
                    ArrayList<String> items = new ArrayList<String>();
                    HashMap<String,String> paints = new HashMap<String, String>();
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        items.add(ds.getKey());
                        paints.put(ds.getKey(), (String)ds.getValue());
                    }
                    String[] itemsArray = items.toArray(new String[0]);
                    builder.setItems(itemsArray, new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Canvas.loadCanvas(paints.get(itemsArray[which]));

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.itemSettings:
                GoToSettings(mLayout);
                return true;
            case R.id.itemAbout:
                GoToAbout(mLayout);
                return true;
            case R.id.itemMaps:
                GoToMaps(mLayout);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void GoToSettings(View view){
        Intent i = new Intent(this, Background.class);
        startActivityForResult(i,0);

    };
    public void GoToAbout(View view){
        Intent i = new Intent(this, About.class);
        startActivity(i);

    };
    public void GoToMaps(View view){
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    };

    public void setColorBlue(View view){
        brush.setColor(Color.BLUE);
        fragmentCanvas.changeColorPen(brush.getColor());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentCanvas, null)
                .addToBackStack(null) // name can be null
                .commit();
        frag = 0;
    };
    public void setColorRed(View view){
        brush.setColor(Color.RED);
        fragmentCanvas.changeColorPen(brush.getColor());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentCanvas, null)
                .addToBackStack(null) // name can be null
                .commit();
        frag = 0;
    };
    public void setColorGreen(View view){
        brush.setColor(Color.GREEN);
        fragmentCanvas.changeColorPen(brush.getColor());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentCanvas, null)
                .addToBackStack(null) // name can be null
                .commit();
        frag=0;
    };
    public void setColor(View view){
        brush.setColor(Color.BLACK);
        fragmentCanvas.changeColorPen(brush.getColor());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentCanvas, null)
                .addToBackStack(null) // name can be null
                .commit();
        frag=0;
    };
    public void erase(View view){
        fragmentCanvas.clean();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentCanvas, null)
                .addToBackStack(null) // name can be null
                .commit();
        frag=0;
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK && requestCode == 0) {
            if (data.hasExtra("color")) {
                mLayout.setBackgroundColor(data.getExtras().getInt("color"));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener( this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener( this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        /*if(mySensor.getType() == Sensor.TYPE_LIGHT){
            float brightness = sensorEvent.values[0];
            if(Math.abs(brightness-lastLight)>20) {
                brightness = normalize(brightness, 0, lightSensor.getMaximumRange(), 255, 0);
                Settings.System.putInt(getApplicationContext().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, Math.round(brightness));
            }
            lastLight = brightness;
        }*/
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            //alignment with x
            //RelativeLayout cLayout = (RelativeLayout) findViewById(R.id.rect);

            if(x>9 && x<11){
                fragmentCanvas.changeBgcolor(Color.parseColor("#d6f5d6"));
            }
            else if(x>-11 && x<-9){
                fragmentCanvas.changeBgcolor(Color.parseColor("#ffcccc"));
            }
            //alignment with y
            else if(y>9 && y<11){
                fragmentCanvas.changeBgcolor(Color.parseColor("#ffffff"));
            }
            else if(y>-11 && y<-9){
                fragmentCanvas.changeBgcolor(Color.parseColor("#ffffcc"));
            }
            //alignment with z
            else if(z>9 && z<11){
                fragmentCanvas.changeBgcolor(Color.parseColor("#d1e0e0"));
            }
            else if(z>-11 && z<-9){
                fragmentCanvas.changeBgcolor(Color.parseColor("#ffcc99"));
            }

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    fragmentCanvas.clean();
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private float normalize(float x, float inMin, float inMax, float outMin, float outMax) {
        float outRange = outMax - outMin;
        float inRange  = inMax - inMin;
        return (x - inMin) *outRange / inRange + outMin;
    }
}