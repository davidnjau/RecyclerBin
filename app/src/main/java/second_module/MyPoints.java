package second_module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyPoints extends AppCompatActivity {

    private TextView tvPointsEarned, tvPointsRedeemed, tvRedeemablePoints;
    private String userUid;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private int pointDbData = 0;
    private int pointRedeemData = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_points);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        tvPointsEarned = findViewById(R.id.tvPointsEarned);
        tvPointsRedeemed = findViewById(R.id.tvPointsRedeemed);
        tvRedeemablePoints = findViewById(R.id.tvRedeemablePoints);

        findViewById(R.id.btnRedeem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pointAvailableRedeem = Integer.parseInt(tvRedeemablePoints.getText().toString());

                if (pointAvailableRedeem > 10){

                    //Convert Points to cash and redeem
                    redeemPoints();

                }else {
                    Toast.makeText(MyPoints.this, "You have points"
                            + " less than the minimum conversion rate.", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void redeemPoints() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference redeemedPoints = database.getReference().child("redeemedPoints");

        String key = redeemedPoints.push().getKey();

        int txtRedeemablePoint = Integer.parseInt(tvRedeemablePoints.getText().toString());
        String finalData = String.valueOf(txtRedeemablePoint + pointRedeemData);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM.dd HH:mm");
        String currentDateTime = sdf.format(new Date());

        assert key != null;
        redeemedPoints.child(userUid).child(key).child("redeem_id").setValue(key);
        redeemedPoints.child(userUid).child(key).child("created_at").setValue(currentDateTime);
        redeemedPoints.child(userUid).child(key).child("points").setValue(String.valueOf(txtRedeemablePoint));

        setDataText(tvRedeemablePoints, "0");
        setDataText(tvPointsRedeemed, finalData);

        Toast.makeText(this, "You have successfully redeemed your points.", Toast.LENGTH_SHORT).show();

    }

    private PointsPojo getPoints(int points){

        int amountDHS = points / 10;
        return new PointsPojo(points, amountDHS);

    }

    @Override
    protected void onStart() {
        super.onStart();

        getData();



    }

    private void getData() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            pointRedeemData = 0;
            pointDbData = 0;

            userUid = currentUser.getUid();

            pointDbData = Integer.parseInt(getIntent().getStringExtra("earned_points"));
            pointRedeemData = Integer.parseInt(getIntent().getStringExtra("redeemed_points"));

            String points =  String.valueOf(pointDbData);
            setDataText(tvPointsEarned, points);

            int pointAvailableRedeem = pointDbData - pointRedeemData;
            String points1 = String.valueOf(pointAvailableRedeem);
            setDataText(tvRedeemablePoints, points1);

            String finalData = String.valueOf(pointRedeemData);
            setDataText(tvPointsRedeemed, finalData);

        }

    }

    private void setDataText(TextView textView, String s) {
        textView.setText(s);
    }


}