package com.remo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

public class DisplayMapData extends AppCompatActivity {

    private MapView map;
    private Float StartLatitude, StartLongitude;

    private LocationFinderGPSNLP finder;
    double longitude = 0.000, latitude = 0.000;
    AlertDialog.Builder builder;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("recycler_details");

    ArrayAdapter<String> adapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map_data);

        builder = new AlertDialog.Builder(this);
        finder = new LocationFinderGPSNLP(this);
        map = findViewById(R.id.map);

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

    private void initMap() {

        final GeoPoint geoPoint = new GeoPoint(StartLatitude, StartLongitude);

        map.setTileSource(TileSourceFactory.OpenTopo);
        map.setMultiTouchControls(true);
        map.getController().setZoom(4.0f);
        map.setMaxZoomLevel(null);

        MapController mapController = (MapController) map.getController();
//        mapController.animateTo(geoPoint, 10.5, 9000L);
        mapController.setZoom(12.5);
        mapController.setCenter(geoPoint);


        getData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        StartLatitude = Float.valueOf("24.157185");
        StartLongitude = Float.valueOf("55.698689");

        initMap();


    }
}