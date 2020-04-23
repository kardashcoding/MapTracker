package sh.karda.maptracker.database;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import sh.karda.maptracker.R;

public class DbAdapter extends RecyclerView.Adapter<DbAdapter.DbAdapterViewHolder> implements View.OnClickListener {

    private List<PositionRow> positionRowList;

    @Override
    public void onClick(View v) {

    }

    public static class DbAdapterViewHolder extends RecyclerView.ViewHolder{
        public TextView txtTime;
        public TextView txtDate;
        public TextView txtLatitude;
        public TextView txtLongitude;
        public TextView txtAccuracy;
        public TextView txtSpeed;
        public TextView txtHeight;
        CardView cardView;

        public DbAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.time);
            txtDate = itemView.findViewById(R.id.date);
            txtLatitude = itemView.findViewById(R.id.latitude);
            txtLongitude = itemView.findViewById(R.id.longitude);
            txtAccuracy = itemView.findViewById(R.id.accuracy);
            txtSpeed = itemView.findViewById(R.id.speed);
            txtHeight = itemView.findViewById(R.id.height);
            cardView = itemView.findViewById(R.id.card);
        }
    }

    public DbAdapter(List<PositionRow> positionRowList){
         this.positionRowList = positionRowList;
    }

    @NonNull
    @Override
    public DbAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        DbAdapterViewHolder dbAdapterViewHolder = new DbAdapterViewHolder(v);
        return dbAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DbAdapterViewHolder holder, int position) {
        PositionRow row = positionRowList.get(position);
        holder.txtAccuracy.setText(getValue(row.getAccuracy()));
        holder.txtTime.setText(row.getTimeString());
        holder.txtDate.setText(row.getDateString());
        holder.txtLatitude.setText(getValue(row.getLatitude()));
        holder.txtLongitude.setText(getValue(row.getLongitude()));
        holder.txtSpeed.setText(getValue((row.getSpeed()*3.6)));
        holder.txtHeight.setText(getValue((row.getHeight())));
        if (row.sent){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#dee8fc"));
        }
    }

    @Override
    public int getItemCount() {
        return positionRowList.size();
    }

    private String getValue(double v){
        try{
            return String.valueOf(v);
        }catch (Exception e){
            Log.e("DbAdapter.getValue", Objects.requireNonNull(e.getMessage()));
            return "0";
        }
    }
}
