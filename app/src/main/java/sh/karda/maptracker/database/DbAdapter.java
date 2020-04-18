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

import sh.karda.maptracker.R;

public class DbAdapter extends ArrayAdapter<PositionRow> implements View.OnClickListener {

    private String TAG = "DbAdapter";
    private List<PositionRow> row;
    private Context context;

    // View lookup cache
    private static class ViewHolder {
        TextView txtTime;
        TextView txtDate;
        TextView txtLatitude;
        TextView txtLongitude;
        TextView txtAccuracy;
        TextView txtSpeed;
        TextView txtHeight;
    }

    DbAdapter(List<PositionRow> data, Context context) {
        super(context, R.layout.row_item, data);
        this.row = data;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
    }
    private int lastPosition = -1;

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        // Get the data item for this position
        PositionRow positionRow = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtTime = convertView.findViewById(R.id.time);
            viewHolder.txtDate = convertView.findViewById(R.id.date);
            viewHolder.txtLatitude = convertView.findViewById(R.id.latitude);
            viewHolder.txtLongitude = convertView.findViewById(R.id.longitude);
            viewHolder.txtAccuracy = convertView.findViewById(R.id.accuracy);
            viewHolder.txtSpeed = convertView.findViewById(R.id.speed);
            viewHolder.txtHeight = convertView.findViewById(R.id.height);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;


        assert positionRow != null;
        String lat = getValue(positionRow.getLongitude());
        viewHolder.txtTime.setText(positionRow.getTimeString());
        viewHolder.txtDate.setText(positionRow.getDateString());
        viewHolder.txtLatitude.setText(lat);
        viewHolder.txtLongitude.setText(getValue(positionRow.getLongitude()));
        viewHolder.txtAccuracy.setText(getValue(positionRow.getAccuracy()));
        viewHolder.txtSpeed.setText(getValue(positionRow.getSpeed()));
        viewHolder.txtHeight.setText(getValue(positionRow.getHeight()));
        if (positionRow.sent){
            viewHolder.txtTime.setTextColor(Color.parseColor("#7f9bc9"));
            viewHolder.txtDate.setTextColor(Color.parseColor("#7f9bc9"));
            viewHolder.txtLatitude.setTextColor(Color.parseColor("#7f9bc9"));
            viewHolder.txtLongitude.setTextColor(Color.parseColor("#7f9bc9"));
            viewHolder.txtAccuracy.setTextColor(Color.parseColor("#7f9bc9"));
            viewHolder.txtSpeed.setTextColor(Color.parseColor("#7f9bc9"));
            viewHolder.txtHeight.setTextColor(Color.parseColor("#7f9bc9"));
        }

        return convertView;
    }

    private String getValue(double v){
        try{
            return String.valueOf(v);
        }catch (Exception e){
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            return "0";
        }
    }
}
