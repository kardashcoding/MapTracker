package sh.karda.maptracker;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import sh.karda.maptracker.database.AppDatabase;
import sh.karda.maptracker.database.DbActivity;
import sh.karda.maptracker.get.GetLocations;
import sh.karda.maptracker.put.Sender;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class ActionFragment extends Fragment {
    private static final String TAG = "ActionFragment";
    private int numberOfPresses = 0;
    private AppDatabase db;

    private ImageButton loadButton;
    private ImageButton sendToCloud;
    private ImageButton resetButton;
    private ImageButton loadFromCloud;
    private ImageButton startSettings;
    private Switch displayLinesSwitch;
    private boolean displayLines;

    public ActionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MapsActivity mapsActivity = (MapsActivity)getActivity();
        return inflater.inflate(R.layout.fragment_action, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState){
        db = Room.databaseBuilder(Objects.requireNonNull(getContext()), AppDatabase.class, "production")
                .build();
        startSettings = getView().findViewById(R.id.button_start_settings);
        startSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });


        loadButton = Objects.requireNonNull(getView()).findViewById(R.id.button_db);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberOfPresses++;
                Intent dbIntent = new Intent(getContext(), DbActivity.class);
                startActivity(dbIntent);
            }
        });
        sendToCloud = getView().findViewById(R.id.button_send);
        sendToCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sender s = new Sender(db);
                s.execute();
            }
        });

        loadFromCloud = getView().findViewById(R.id.button_load_from_cloud);
        loadFromCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity mapsActivity = (MapsActivity)getActivity();
                GetLocations getLocations = new GetLocations(mapsActivity.getMap(), getDeviceId(), displayLines);
                getLocations.execute();

            }
        });

        resetButton = getView().findViewById(R.id.button_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    db.posDao().setRowsAsSent(false);
                    db.posDao().messUpGuid();
                }catch (Exception e){
                    Log.v(TAG, e.getMessage());
                }
            }
        });
        loadButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getContext(), "Long press no: " + numberOfPresses, Toast.LENGTH_SHORT).show();
                numberOfPresses++;
                if (numberOfPresses == 3) {
                    MapsActivity mapsActivity = (MapsActivity)getActivity();
                    GetLocations getLocations = new GetLocations(mapsActivity.getMap(), "any", displayLines);
                    getLocations.execute();
                }else{
                    Log.v(TAG, "Shit kom hit 1");

                    Sender s = new Sender(db);
                    s.execute();
                }
                return true;
            }
        });

        displayLinesSwitch = getView().findViewById(R.id.switch_lines);
        displayLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                displayLines = isChecked;
                MapsActivity mapsActivity = (MapsActivity)getActivity();

                GetLocations getLocations = new GetLocations(mapsActivity.getMap(), getDeviceId(), displayLines);
                getLocations.execute();
            }
        });
    }
    private String getDeviceId(){
        return Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
