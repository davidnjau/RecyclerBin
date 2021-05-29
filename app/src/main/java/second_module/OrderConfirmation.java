package second_module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.remo.R;

public class OrderConfirmation extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private TextView tvIdNumber, tvEmail, tvOrderId,tvDbPoints, tvDbAmount,tvBottleNo;
    private int pointDbData = 0;
    private String orderNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        tvDbAmount = findViewById(R.id.tvDbAmount);
        tvDbPoints = findViewById(R.id.tvDbPoints);
        tvEmail = findViewById(R.id.tvEmail);
        tvIdNumber = findViewById(R.id.tvIdNumber);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvBottleNo = findViewById(R.id.tvBottleNo);

        findViewById(R.id.btnDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(OrderConfirmation.this, Calculator.class));


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){

            orderNo = getIntent().getStringExtra("orderId");
            String orderId = "Order Number: " + "ORD" + orderNo;
            tvOrderId.setText(orderId);


            String userUid = currentUser.getUid();
            databaseReference.child("users").child(userUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String idNumber = "Id: " + dataSnapshot.child("id_number").getValue() ;
                    String emailAddress = "Email: "+ dataSnapshot.child("email").getValue();
                    String points1 = String.valueOf(dataSnapshot.child("points").getValue());

                    String points;
                    if (!points1.equals("null")){

                        points = "Total number of points: " + points1;

                    }else {
                        points = "Total number of points: 0";

                    }
                    tvDbPoints.setText(points);

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

                    for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){

                        String points1 = String.valueOf(childDataSnapshot.child("points").getValue());
                        if (!points1.equals("null")){

                            int points = Integer.parseInt(points1);
                            pointDbData = pointDbData + points;

                        }


                    }

                    int amountPojo = getPoints(pointDbData).getAmount();
                    String amounts = "Amount: "+ amountPojo + " Dirhams";

                    tvDbAmount.setText(amounts);

                    String points = "Total number of points: " + pointDbData;
                    tvDbPoints.setText(points);

                    int bottleNumber = pointDbData * 5;
                    String noBottle = "You have recycled: " + bottleNumber + " bottles.";
                    tvBottleNo.setText(noBottle);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


    }

    private PointsPojo getPoints(int points){

        int amountDHS = points / 10;

        return new PointsPojo(points, amountDHS);

    }
}