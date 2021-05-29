package second_module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.remo.R;
import com.remo.RecyclerBinPojo;
import com.remo.UpdateData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Calculator extends AppCompatActivity {

    private EditText etPlasticBottles;
    private TextView tvPoints, tvAmount;
    private TextView tvEmail, tvIdNumber, tvDbPoints;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String userUid;

    private int pointData = 0;
    private int pointDbData = 0;
    private int pointRedeemData = 0;

    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        mAuth = FirebaseAuth.getInstance();


        tvEmail = findViewById(R.id.tvEmail);
        tvIdNumber = findViewById(R.id.tvIdNumber);
        tvDbPoints = findViewById(R.id.tvDbPoints);


        etPlasticBottles = findViewById(R.id.etPlasticBottles);
        tvPoints = findViewById(R.id.tvPoints);
        tvAmount = findViewById(R.id.tvAmount);

        findViewById(R.id.btnBalance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), BalanceDetails.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnSendWaste).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtPlsticBottles = etPlasticBottles.getText().toString();
                if (!TextUtils.isEmpty(txtPlsticBottles)) {
                    String orderId = generateOrder();
                    if (orderId != null) {
                        Intent intent = new Intent(getBaseContext(), OrderConfirmation.class);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                    }else {
                        Toast.makeText(Calculator.this, "Please wait until we load your data. Try reconnecting your device.", Toast.LENGTH_SHORT).show();

                    }
                }else {
                    etPlasticBottles.setError("This field cannot be empty.");
                }

            }
        });

        etPlasticBottles.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {

                if (!s.toString().equals("")){
                    int plasticBottles = Integer.parseInt(s.toString());
                    setTextData(plasticBottles);
                }else {
                    int plasticBottles = 0;
                    setTextData(plasticBottles);
                }

            }
        });



        findViewById(R.id.btnOrderWaste).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Order Waste Collection

                String txtPlsticBottles = etPlasticBottles.getText().toString();
                if (!TextUtils.isEmpty(txtPlsticBottles)){

                    String orderId = generateOrder();
                    if (orderId != null){
                        Intent intent = new Intent(getBaseContext(), OrderWasteCollection.class);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                    }else {
                        Toast.makeText(Calculator.this, "Please wait until we load your data. Try reconnecting your device.", Toast.LENGTH_SHORT).show();

                    }

                }else
                    etPlasticBottles.setError("This field cannot be empty.");

            }
        });

        findViewById(R.id.btnMyPoints).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLoaded){
                    Intent intent = new Intent(getBaseContext(), MyPoints.class);
                    intent.putExtra("earned_points", String.valueOf(pointDbData));
                    intent.putExtra("redeemed_points", String.valueOf(pointRedeemData));
                    startActivity(intent);
                }else {
                    Toast.makeText(Calculator.this, "Please wait until we load your data. " +
                            "Try reconnecting your device.", Toast.LENGTH_SHORT).show();

                }




            }
        });

        getData();

    }

    private String generateOrder() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference orderDetails = database.getReference().child("orders");
        String orderKey = orderDetails.push().getKey();

        if (userUid != null){

            int points = pointData;
            int dbPoints = pointDbData;

            int totalPoints = points + dbPoints;
            String finalPoints = String.valueOf(totalPoints);

            assert orderKey != null;

            orderDetails.child(userUid).child(orderKey).child("orderId").setValue(orderKey);
            orderDetails.child(userUid).child(orderKey).child("points").setValue(finalPoints);


        }
        return orderKey;


    }

    private void setTextData(int plasticBottles) {

        if (plasticBottles > 5){
            PointsPojo pointsPojo = getPoints(plasticBottles);

            String txtPoints = pointsPojo.getPoints() + " Points";
            String txtAmount = pointsPojo.getAmount() + " Dhs";

            pointData = pointsPojo.getPoints();

            tvPoints.setText(txtPoints);
            tvAmount.setText(txtAmount);
        }else {
            if (plasticBottles < 5){
                etPlasticBottles.setError("Your plastic bottles are less than the minimum required bottles of 5 bottles.");
            }else {
                etPlasticBottles.setError(null);
            }
        }


    }

    private PointsPojo getPoints(int plasticNo){

        int points = plasticNo / 5;
        int amountDHS = points / 10;

        return new PointsPojo(points, amountDHS);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getData() {

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){

            isLoaded = false;

            userUid = currentUser.getUid();

            databaseReference.child("users").child(userUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String idNumber = "Id: " + dataSnapshot.child("id_number").getValue() ;
                    String emailAddress = "Email: "+ dataSnapshot.child("email").getValue();

                    tvEmail.setText(emailAddress);
                    tvIdNumber.setText(idNumber);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference.child("orders").child(userUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    pointDbData = 0;
                    pointData = 0;

                    for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){

                        String points1 = String.valueOf(childDataSnapshot.child("points").getValue());
                        if (!points1.equals("null")){

                            int points = Integer.parseInt(points1);
                            pointDbData = pointDbData + points;

                        }


                    }
                    String points = "Points: " + pointDbData;
                    tvDbPoints.setText(points);
                    isLoaded = true;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference.child("redeemedPoints").child(userUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    pointRedeemData = 0;

                    for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){

                        String points1 = String.valueOf(childDataSnapshot.child("points").getValue());
                        if (!points1.equals("null")){

                            int points = Integer.parseInt(points1);
                            pointRedeemData = pointRedeemData + points;

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }
}