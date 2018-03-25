package yam.womensafety;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private Button help,add;
    private LocationManager locationManager;
    private LocationListener listener;
    private String fileName= "file.txt";

    private String mUserId;

    private double longitude,latitude;

    private GeoFire geoFireLocation;
    private EditText msg,num;

    private ArrayList<String> arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //location services

        arr=new ArrayList<>();
        arr.add("9885810277");
        arr.add("9652303981");
        arr.add("9492188872");
        arr.add("9700677460");

        mAuth = FirebaseAuth.getInstance();

        help = (Button) findViewById(R.id.help);
        add = (Button) findViewById(R.id.add);
        msg = (EditText)findViewById(R.id.message);
        num = (EditText)findViewById(R.id.contact);

        if (mAuth.getCurrentUser()==null){
            startActivity(new Intent(Main2Activity.this,LoginActivity.class));
            finish();
            return;
        }
        else{
            mUserId=mAuth.getCurrentUser().getUid();
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=num.getText().toString();
                arr.add(s);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                //String s = readFile(fileName);


                if (checkPermission(Manifest.permission.SEND_SMS)) {
                    SmsManager sms = SmsManager.getDefault();
                    Toast.makeText(Main2Activity.this,Environment.getExternalStorageDirectory().getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    try {
                        // Scanner sc = new Scanner(org_file);
                        for(String s:arr){

                            Toast.makeText(Main2Activity.this, s, Toast.LENGTH_SHORT).show();
                            sms.sendTextMessage(s, null,"Alert Message!! I am in DANGER.\n"+msg.getText().toString()+"\n"+String.valueOf(latitude)+"\n"+String.valueOf(longitude), null, null);
                        }
                    }
                    catch(Exception e){
                        Toast.makeText(Main2Activity.this, "oh problem at scanner", Toast.LENGTH_SHORT).show();
                    }


                }
                else {
                    Toast.makeText(Main2Activity.this, "not worked ", Toast.LENGTH_SHORT).show();
                }

            }
        });


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Toast.makeText(Main2Activity.this, "changed", Toast.LENGTH_SHORT).show();

                latitude=location.getLatitude();
                longitude = location.getLongitude();

                DatabaseReference locationref = FirebaseDatabase.getInstance().getReference().child("Users").child(mUserId);

                GeoFire geoFireLocation=new GeoFire(locationref);
                geoFireLocation.setLocation("Location",new GeoLocation(location.getLatitude(),location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }

        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.


        locationManager.requestLocationUpdates("gps", 5000, 0, listener);


}

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.contacts) {
            // Handle the camera action
            startActivity(new Intent(Main2Activity.this,AddContacts.class));




        } else if (id == R.id.existing) {

        } else if (id == R.id.logout) {
            mAuth.signOut();
            startActivity(new Intent(Main2Activity.this,LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public String readFile(String file){
        try{
            String text = "";
            FileInputStream fin = openFileInput(file);
            int size = fin.available();
            byte[] arr = new byte[size];
            fin.read(arr);
            String s = new String(arr);
            fin.close();
            return s;
        }
        catch(Exception e){
            Toast.makeText(this, "error ocurred while reading", Toast.LENGTH_SHORT).show();return "";
        }

    }
    public boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check== PackageManager.PERMISSION_GRANTED);
    }


}
