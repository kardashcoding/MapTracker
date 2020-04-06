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

    public ActionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_action, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState){
        db = Room.databaseBuilder(Objects.requireNonNull(getContext()), AppDatabase.class, "production")
                .build();
        ImageButton startSettings = Objects.requireNonNull(getView()).findViewById(R.id.button_start_settings);
        startSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });


        ImageButton loadButton = Objects.requireNonNull(getView()).findViewById(R.id.button_db);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberOfPresses++;
                Intent dbIntent = new Intent(getContext(), DbActivity.class);
                startActivity(dbIntent);
            }
        });

        ImageButton loadFromCloud = getView().findViewById(R.id.button_load_from_cloud);
        loadFromCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity mapsActivity = (MapsActivity)getActivity();
                assert mapsActivity != null;
                GetLocations getLocations = new GetLocations(mapsActivity.getMap(), getDeviceId(), PreferenceHelper.getDrawLinesFromPreferences());
                getLocations.execute();

            }
        });

        loadButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getContext(), "Long press no: " + numberOfPresses, Toast.LENGTH_SHORT).show();
                numberOfPresses++;
                if (numberOfPresses == 3) {
                    MapsActivity mapsActivity = (MapsActivity)getActivity();
                    assert mapsActivity != null;
                    GetLocations getLocations = new GetLocations(mapsActivity.getMap(), "any", PreferenceHelper.getDrawLinesFromPreferences());
                    getLocations.execute();
                }else{
                    Log.v(TAG, "Shit kom hit 1");

                    Sender s = new Sender(db);
                    s.execute();
                }
                return true;
            }
        });
    }
    private String getDeviceId(){
        return Settings.Secure.getString(Objects.requireNonNull(getContext()).getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
