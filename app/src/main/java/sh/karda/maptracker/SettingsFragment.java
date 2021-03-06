package sh.karda.maptracker;

import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import sh.karda.maptracker.database.DbManager;
import sh.karda.maptracker.database.PositionRow;
import sh.karda.maptracker.put.Sender;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preference button = findPreference(getString(R.string.key_reset_value));
        if (button != null){
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Sender sender = new Sender("DELETE", deviceId());
                    sender.execute();
                    DbManager.Delete();
                    return true;

                }
            });
        }

        Preference sendButton = findPreference(getString(R.string.key_send_value));
        if (sendButton != null){
            sendButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DbManager.InsertAndWait(createRandomRow());
                    Sender sender = new Sender("SEND", deviceId());
                    sender.execute();

                    return true;
                }
            });
        }

        Preference unsentButton = findPreference(getString(R.string.key_unsent_value));
        if (unsentButton != null){
            unsentButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DbManager.MarkAllAsUnsent();
                    Sender sender = new Sender("SEND", deviceId());
                    sender.execute();
                    return true;
                }
            });
        }
    }

    private String deviceId(){
        if (getContext() == null) return "unknown";
        return LocationStuff.getDeviceId(getContext());
    }

    private PositionRow createRandomRow(){
        String guid = java.util.UUID.randomUUID().toString();
        return new PositionRow("gud", "dev", 1,2,3,4,5,6,null, "wifi", "deleted", false);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.app_preferences);
    }
}
