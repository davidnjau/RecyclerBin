package second_module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.remo.R;

import java.util.ArrayList;
import java.util.List;

public class BalanceDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String userUid;

    List<BalancePojo> balancePojoList = new ArrayList<>();
    private BalanceRecyclerAdapter balanceRecyclerAdapter;

    private int pointRedeemData = 0;
    private int pointDbData = 0;

    private TextView tvPointBalance,tvPoints,tvAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_details);

        mAuth = FirebaseAuth.getInstance();

        tvPointBalance = findViewById(R.id.tvPointBalance);
        tvPoints = findViewById(R.id.tvPoints);
        tvAmount = findViewById(R.id.tvAmount);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        getData();
    }

    private void getData() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            userUid = currentUser.getUid();

            databaseReference.child("redeemedPoints").child(userUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    balancePojoList.clear();
                    pointDbData = 0;

                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                        String points1 = String.valueOf(childDataSnapshot.child("points").getValue());
                        String createdAt = String.valueOf(childDataSnapshot.child("created_at").getValue());


                        if (!points1.equals("null")) {
                            int points = Integer.parseInt(points1);
                            pointDbData = pointDbData + points;

                            BalancePojo balancePojo = new BalancePojo(createdAt, points1);
                            balancePojoList.add(balancePojo);

                        }

                    }
                    balanceRecyclerAdapter = new BalanceRecyclerAdapter(BalanceDetails.this, balancePojoList);
                    recyclerView.setAdapter(balanceRecyclerAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference.child("orders").child(userUid).addValueEventListener(new ValueEventListener() {
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

                    String txtPoints = String.valueOf(pointRedeemData - pointDbData);
                    String txtTotalPoints = String.valueOf(pointRedeemData);
                    String txtAmount = (pointRedeemData - pointDbData)/ 10 + " Dirhams";

                    tvPointBalance.setText(txtPoints);
                    tvPoints.setText(txtTotalPoints);
                    tvAmount.setText(txtAmount);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }

}