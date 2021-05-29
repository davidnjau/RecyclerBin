package second_module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.remo.R;

public class OrderWasteCollection extends AppCompatActivity {

    private EditText etFullName, etEmail, etMobile, etRegion, etArea;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("waste_collection_request");
    private FirebaseAuth mAuth;
    private String userUid;

    private int pointData = 0;
    private int pointDbData = 0;
    private DatabaseReference databaseReference;
    private String orderNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_waste_collection);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etRegion = findViewById(R.id.etRegion);
        etArea = findViewById(R.id.etArea);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = database.getReference();


        findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtFullName = etFullName.getText().toString();
                String txtEmail = etEmail.getText().toString();
                String txtMobile = etMobile.getText().toString();
                String txtRegion = etRegion.getText().toString();
                String txtArea = etArea.getText().toString();

                if (!TextUtils.isEmpty(txtFullName) && !TextUtils.isEmpty(txtEmail)
                        && !TextUtils.isEmpty(txtMobile) && !TextUtils.isEmpty(txtRegion)
                        && !TextUtils.isEmpty(txtArea)){

                    generateOrder(txtFullName, txtEmail, txtMobile, txtRegion, txtArea);

                }else {

                    Toast.makeText(OrderWasteCollection.this, "Please fill all fields before submitting", Toast.LENGTH_SHORT).show();

                }


            }
        });
    }

    private void generateOrder(String txtFullName, String txtEmail, String txtMobile, String txtRegion, String txtArea) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference orderDetails = database.getReference().child("orders");

        if (userUid != null){
            int dbPoints = pointDbData;

            String finalPoints = String.valueOf(dbPoints);

            assert orderNo != null;

            orderDetails.child(userUid).child(orderNo).child("orderId").setValue(orderNo);
            orderDetails.child(userUid).child(orderNo).child("points").setValue(finalPoints);

            UserData userData = new UserData(txtFullName, txtEmail, txtMobile, txtRegion, txtArea, orderNo, finalPoints);
            myRef.child(userUid).child(orderNo).setValue(userData);

            Toast.makeText(OrderWasteCollection.this, "Data submitted successfully.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getBaseContext(), OrderConfirmation.class);
            intent.putExtra("orderId", orderNo);
            startActivity(intent);


        }


    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            userUid = currentUser.getUid();

            orderNo = getIntent().getStringExtra("orderId");

            databaseReference.child("orders").child(userUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                        String points1 = String.valueOf(childDataSnapshot.child("points").getValue());
                        if (!points1.equals("null")) {

                            int points = Integer.parseInt(points1);
                            pointDbData = pointDbData + points;

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