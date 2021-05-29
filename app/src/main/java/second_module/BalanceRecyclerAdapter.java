package second_module;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.remo.R;

import java.util.List;


public class BalanceRecyclerAdapter extends RecyclerView.Adapter<BalanceRecyclerAdapter.ViewHolder>{


    private Context context;
    private final List<BalancePojo> balancePojoList;

    public BalanceRecyclerAdapter(Context context, List<BalancePojo> balancePojoList) {
        this.context = context;
        this.balancePojoList = balancePojoList;

    }

    @NonNull
    @Override
    public BalanceRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_balance, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BalanceRecyclerAdapter.ViewHolder holder, final int position) {

        final String txtPoints = balancePojoList.get(position).getPoints();
        final String txtCreatedAt = balancePojoList.get(position).getCreatedAt();

        holder.tvPoints.setText(txtPoints);
        holder.tvDate.setText(txtCreatedAt);

    }



    @Override
    public int getItemCount() {
        return balancePojoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvDate, tvPoints;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvDate);
            tvPoints = itemView.findViewById(R.id.tvPoints);


        }
    }


}
