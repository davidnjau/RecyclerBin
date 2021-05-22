package com.remo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class RequestBin extends AppCompatActivity {

    private MapView map;
    private Float StartLatitude, StartLongitude;

    private LocationFinderGPSNLP finder;
    double longitude = 0.000, latitude = 0.000;
    private Button btnAddBin, bntEditBin;
    AlertDialog.Builder builder;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("recycler_details");

    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    FusedLocationProviderClient mFusedLocationClient;

    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_bin);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        builder = new AlertDialog.Builder(this);
        finder = new LocationFinderGPSNLP(this);
        map = findViewById(R.id.map);

        btnAddBin = findViewById(R.id.btnAddBin);
        bntEditBin = findViewById(R.id.bntEditBin);
        bntEditBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UpdateData.class);
                startActivity(intent);
//                openDialog();

            }
        });


        btnAddBin.setOnClickListener(v -> {

            if (finder.canGetLocation()) {
                latitude = finder.getLatitude();
                longitude = finder.getLongitude();
            }
            LayoutInflater li = LayoutInflater.from(RequestBin.this);
            View promptsView = li.inflate(R.layout.alert_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RequestBin.this);
            alertDialogBuilder.setView(promptsView);


            final EditText etRecycleCenterName = promptsView.findViewById(R.id.etRecycleCenterName);
            final EditText etPhoneNumber = promptsView.findViewById(R.id.etPhoneNumber);
            TimePicker timePickerOpen = promptsView.findViewById(R.id.timePickerOpen); // initiate a time
            TimePicker timePickerClose = promptsView.findViewById(R.id.timePickerClose); // initiate a time
            TextView tvLatitude = promptsView.findViewById(R.id.tvLatitude);
            TextView tvLongitude = promptsView.findViewById(R.id.tvLongitude);
            Button btnGetLocation = promptsView.findViewById(R.id.btnGetLocation);

            btnGetLocation.setOnClickListener(v1 -> {
                getCurrentLocation();

                String lat1 = String.valueOf(latitude);
                String lon2 = String.valueOf(longitude);

                if (!lat1.equals("0.0") || !lon2.equals("0.0")){

                    tvLatitude.setText(lat1);
                    tvLongitude.setText(lon2);

                    setText(tvLatitude, tvLongitude, lat1, lon2);
                }else {
                    Toast.makeText(RequestBin.this, "Check if you have locations turned on and try again", Toast.LENGTH_LONG).show();
                }

            });

            String lat1 = String.valueOf(latitude);
            String lon2 = String.valueOf(longitude);
            tvLatitude.setText(lat1);
            tvLongitude.setText(lon2);

            setText(tvLatitude, tvLongitude, lat1, lon2);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> {

                        String recyclerName = etRecycleCenterName.getText().toString();
                        String phoneNumber = etPhoneNumber.getText().toString();

                        String lat = tvLatitude.getText().toString();
                        String lon = tvLongitude.getText().toString();

                        String openTime = getSelectedTime(timePickerOpen);
                        String closeTime = getSelectedTime(timePickerClose);

                        if (!TextUtils.isEmpty(recyclerName)
                                && !TextUtils.isEmpty(phoneNumber)
                                && !lat1.equals("0.0")
                                && !lon2.equals("0.0")){

                            RecyclerBinPojo recyclerBinPojo = new RecyclerBinPojo();
                            recyclerBinPojo.setRecyclerBin(recyclerName);
                            recyclerBinPojo.setPhoneNumber(phoneNumber);
                            recyclerBinPojo.setLatitude(lat);
                            recyclerBinPojo.setLongitude(lon);
                            recyclerBinPojo.setOpenTime(openTime);
                            recyclerBinPojo.setCloseTime(closeTime);

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("recycler_details");
                            myRef.push().setValue(recyclerBinPojo);

                            Toast.makeText(getApplicationContext(), "Data has been added.", Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(getApplicationContext(), "All fields should be filled. Also make sure location is on on the phone.", Toast.LENGTH_LONG).show();

                        }
                    })
                    .setNegativeButton("Cancel",
                            (dialog, id) -> dialog.cancel());

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        });


    }

    private void openDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View rowList = getLayoutInflater().inflate(R.layout.row, null);
        ListView listView = rowList.findViewById(R.id.listView);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<RecyclerBinPojo> recyclerBinPojoList = new ArrayList<>();
                List<String> nameList = new ArrayList<>();
                List<String> keyList = new ArrayList<>();
                for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){

                    String keys = childDataSnapshot.getKey();
                    assert keys != null;
                    DataSnapshot dataSnapshot1 = dataSnapshot.child(keys);
                    RecyclerBinPojo recyclerBinPojo = dataSnapshot1.getValue(RecyclerBinPojo.class);

                    assert recyclerBinPojo != null;
                    recyclerBinPojoList.add(recyclerBinPojo);
                    keyList.add(keys);

                }

                for (int i = 0; i < recyclerBinPojoList.size(); i++){

                    String recyclerBinName = recyclerBinPojoList.get(i).getRecyclerBin();
                    nameList.add(recyclerBinName);

                }

                adapter = new ArrayAdapter<String>(RequestBin.this, android.R.layout.simple_list_item_1, nameList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                alertDialog.setView(rowList);
                AlertDialog dialog = alertDialog.create();
                dialog.show();


                listView.setOnItemClickListener((parent, view, position, id) -> {

                    String key = keyList.get(position);
                    Intent intent = new Intent(getBaseContext(), UpdateData.class);
                    intent.putExtra("key", key);
                    startActivity(intent);

//                    dialog.dismiss();
//                    updateData(key);

                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void updateData(String key){

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.alert_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final EditText etRecycleCenterName = (EditText) promptsView.findViewById(R.id.etRecycleCenterName);
        final EditText etPhoneNumber = (EditText) promptsView.findViewById(R.id.etPhoneNumber);
        TimePicker timePickerOpen = (TimePicker) promptsView.findViewById(R.id.timePickerOpen); // initiate a time
        TimePicker timePickerClose = (TimePicker) promptsView.findViewById(R.id.timePickerClose); // initiate a time
        TextView tvLatitude = (TextView)promptsView.findViewById(R.id.tvLatitude);
        TextView tvLongitude = (TextView)promptsView.findViewById(R.id.tvLongitude);
        Button btnGetLocation = (Button)promptsView.findViewById(R.id.btnGetLocation);

        String lat1 = String.valueOf(latitude);
        String lon2 = String.valueOf(longitude);
        setText(tvLatitude, tvLongitude, lat1, lon2);

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getCurrentLocation();

            }
        });

        myRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                RecyclerBinPojo recyclerBinPojo = dataSnapshot.getValue(RecyclerBinPojo.class);
                assert recyclerBinPojo != null;
                String recyclerBinName = recyclerBinPojo.getRecyclerBin();
                String phoneNumber = recyclerBinPojo.getPhoneNumber();
                String latitude = recyclerBinPojo.getLatitude();
                String longitude = recyclerBinPojo.getLongitude();
                String openTime = recyclerBinPojo.getOpenTime();
                String closeTime = recyclerBinPojo.getCloseTime();

                etRecycleCenterName.setText(recyclerBinName);
                etPhoneNumber.setText(phoneNumber);
                setText(tvLatitude, tvLongitude, latitude, longitude);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", (dialog1, id) -> {

                    String recyclerName = etRecycleCenterName.getText().toString();
                    String phoneNumber = etPhoneNumber.getText().toString();

                    String lat = tvLatitude.getText().toString();
                    String lon = tvLongitude.getText().toString();

                    String openTime = getSelectedTime(timePickerOpen);
                    String closeTime = getSelectedTime(timePickerClose);

                    if (!TextUtils.isEmpty(recyclerName) && !TextUtils.isEmpty(phoneNumber)) {

                        RecyclerBinPojo recyclerBinPojo = new RecyclerBinPojo();
                        recyclerBinPojo.setRecyclerBin(recyclerName);
                        recyclerBinPojo.setPhoneNumber(phoneNumber);
                        recyclerBinPojo.setLatitude(lat);
                        recyclerBinPojo.setLongitude(lon);
                        recyclerBinPojo.setOpenTime(openTime);
                        recyclerBinPojo.setCloseTime(closeTime);

//                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        DatabaseReference myRef = database.getReference("recycler_details");
//                        myRef.child(key).setValue(recyclerBinPojo);

                        Intent intent = new Intent(getBaseContext(), UpdateData.class);
                        intent.putExtra("key", key);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "Data has been updated.", Toast.LENGTH_LONG).show();

                        dialog1.dismiss();
                        getData();
                    }else {
                        Toast.makeText(getApplicationContext(), "All fields should be filled.", Toast.LENGTH_LONG).show();

                    }
                })
                .setNegativeButton("Cancel",
                        (dialog1, id) -> dialog1.cancel());

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void getData(){

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<RecyclerBinPojo> recyclerBinPojoList = new ArrayList<>();

                for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){

                    String keys = childDataSnapshot.getKey();
                    assert keys != null;
                    DataSnapshot dataSnapshot1 = dataSnapshot.child(keys);
                    RecyclerBinPojo recyclerBinPojo = dataSnapshot1.getValue(RecyclerBinPojo.class);

                    assert recyclerBinPojo != null;
                    recyclerBinPojoList.add(recyclerBinPojo);

                }

                for (int i = 0; i < recyclerBinPojoList.size(); i++){

                    String recyclerBinName = recyclerBinPojoList.get(i).getRecyclerBin();
                    String phoneNumber = recyclerBinPojoList.get(i).getPhoneNumber();
                    String latitude = recyclerBinPojoList.get(i).getLatitude();
                    String longitude = recyclerBinPojoList.get(i).getLongitude();
                    String openTime = recyclerBinPojoList.get(i).getOpenTime();
                    String closeTime = recyclerBinPojoList.get(i).getCloseTime();

                    float lat = Float.parseFloat(latitude);
                    float lon = Float.parseFloat(longitude);

                    String markerDetails =
                            "Recycle Center :" + recyclerBinName + "\n" +
                                    "Opening Time : " + openTime + "\n" +
                                    "Closing Time : " + closeTime + "\n" +
                                    "Phone Number : " + phoneNumber;


                    final GeoPoint geoPoint3 = new GeoPoint(lat, lon);

                    Marker marker = new Marker(map);
                    marker.setPosition(geoPoint3);
                    map.getOverlays().add(marker);
                    marker.setTitle(markerDetails);
                    marker.setIcon(getResources().getDrawable(R.mipmap.ic_action_recycler));

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setText(TextView tvLatitude, TextView tvLongitude, String lat1, String lon2){
        tvLatitude.setText(lat1);
        tvLongitude.setText(lon2);
    }
    private String getSelectedTime(TimePicker timePickerOpen) {

        int hour, minute;
        String am_pm;
        String timeData;

        if (Build.VERSION.SDK_INT >= 23 ){
            hour = timePickerOpen.getHour();
            minute = timePickerOpen.getMinute();


        }
        else{
            hour = timePickerOpen.getCurrentHour();
            minute = timePickerOpen.getCurrentMinute();

        }
        if(hour > 12) {
            am_pm = "PM";
            hour = hour - 12;
        }
        else
        {
            am_pm="AM";
        }
        String min = getTime(minute);
        timeData = hour + ":" + min + " " + am_pm;


        return timeData;

    }

    private String getTime(int minute){
        String min = "";

        if (minute<10){
            min = "0"+ minute;
        }else {
            min = "" + minute;
        }

        return min;
    }

    private boolean getCurrentLocation(){

        boolean isLocation = false;

        if (finder.canGetLocation()) {

            latitude = finder.getLatitude();
            longitude = finder.getLongitude();

            if (latitude != 0.000 && longitude != 0.000) {

                isLocation = true;

                final GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                OverlayItem overlayItem = new OverlayItem("Lat", "Long", geoPoint);
                Drawable markerDrawable = this.getResources().getDrawable(R.drawable.ic_action_profile);
                overlayItem.setMarker(markerDrawable);

                setPointers(overlayItem);

                getLastLocation();
            }

        }

        return isLocation;

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void getLastLocation() {

        if (isLocationEnabled()) {

            // getting last
            // location from
            // FusedLocationClient
            // object
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();


                    }
                }
            });
        } else {
            Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }


    }
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    private LocationCallback mLocationCallback = new LocationCallback() {

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

        }
    };

    private void initMap() {

        getCurrentLocation();

        final GeoPoint geoPoint = new GeoPoint(StartLatitude, StartLongitude);

        map.setTileSource(TileSourceFactory.OpenTopo);
        map.setMultiTouchControls(true);
        map.getController().setZoom(4.0f);
        map.setMaxZoomLevel(null);

        MapController mapController = (MapController) map.getController();
//        mapController.animateTo(geoPoint, 10.5, 9000L);
        mapController.setZoom(12.5);
        mapController.setCenter(geoPoint);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<RecyclerBinPojo> recyclerBinPojoList = new ArrayList<>();

                for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){

                    String keys = childDataSnapshot.getKey();
                    assert keys != null;
                    DataSnapshot dataSnapshot1 = dataSnapshot.child(keys);
                    RecyclerBinPojo recyclerBinPojo = dataSnapshot1.getValue(RecyclerBinPojo.class);

                    assert recyclerBinPojo != null;
                    recyclerBinPojoList.add(recyclerBinPojo);

                }

                for (int i = 0; i < recyclerBinPojoList.size(); i++){

                    String recyclerBinName = recyclerBinPojoList.get(i).getRecyclerBin();
                    String phoneNumber = recyclerBinPojoList.get(i).getPhoneNumber();
                    String latitude = recyclerBinPojoList.get(i).getLatitude();
                    String longitude = recyclerBinPojoList.get(i).getLongitude();
                    String openTime = recyclerBinPojoList.get(i).getOpenTime();
                    String closeTime = recyclerBinPojoList.get(i).getCloseTime();

                    float lat = Float.parseFloat(latitude);
                    float lon = Float.parseFloat(longitude);

                    String markerDetails =
                            "Recycle Center :" + recyclerBinName + "\n" +
                                    "Opening Time : " + openTime + "\n" +
                                    "Closing Time : " + closeTime + "\n" +
                                    "Phone Number : " + phoneNumber;


                    final GeoPoint geoPoint3 = new GeoPoint(lat, lon);

                    Marker marker = new Marker(map);
                    marker.setPosition(geoPoint3);
                    map.getOverlays().add(marker);
                    marker.setTitle(markerDetails);
                    marker.setIcon(getResources().getDrawable(R.mipmap.ic_action_recycler));

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setPointers(OverlayItem overlayItem){

        ArrayList<OverlayItem> overlayItemArrayList = new ArrayList<OverlayItem>();
        overlayItemArrayList.add(overlayItem);

        ItemizedOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<OverlayItem>(this, overlayItemArrayList, null);
        map.getOverlays().add(locationOverlay);
    }

    @Override
    protected void onStart() {
        super.onStart();

        StartLatitude = Float.valueOf("24.157185");
        StartLongitude = Float.valueOf("55.698689");

        initMap();

        checkPermissions();

    }

    private void checkPermissions() {

        if(ContextCompat.checkSelfPermission(RequestBin.this, Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(RequestBin.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(RequestBin.this,Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(RequestBin.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(RequestBin.this);
                builder.setMessage("Location and Write External" +
                        " Storage permissions are required for the app.");
                builder.setTitle("Please grant these permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                RequestBin.this,
                                new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        RequestBin.this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        }
        
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CODE:{
                // When request is cancelled, the results array are empty
                if(
                        (grantResults.length >0) &&
                                (grantResults[0]
                                        + grantResults[1]
                                        == PackageManager.PERMISSION_GRANTED
                                )
                ){
                    // Permissions are granted
                    Toast.makeText(RequestBin.this, "Permissions granted.", Toast.LENGTH_SHORT).show();

                }else {
                    // Permissions are denied
                    Toast.makeText(RequestBin.this, "Permissions denied.", Toast.LENGTH_SHORT).show();
                    
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();

    }
}