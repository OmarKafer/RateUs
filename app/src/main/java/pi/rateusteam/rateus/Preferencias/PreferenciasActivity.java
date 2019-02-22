package pi.rateusteam.rateus.Preferencias;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pi.rateusteam.rateus.R;

public class PreferenciasActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{


    SharedPreferences preferenciasUsuario;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);

        //Cal llegir les preferencies i actualitzar tots els valor de summaries
        actualitzaSummaries();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals("color_de_fondo")) {
            Preference pref = findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, "Ninguno").toString());
        }
        if(key.equals("aplicar_background")) {
            Preference pref = findPreference(key);
            boolean aplicar = sharedPreferences.getBoolean(key, false);
            //pref.setSummary(aplicar?etString(R.string.etiqueta_si):getString(R.string.etiqueta_no));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void actualitzaSummaries(){
        preferenciasUsuario  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Preference pref;

        //Color de fondo
        pref = findPreference("color_de_fondo");
        pref.setSummary(preferenciasUsuario.getString("color_de_fondo", "Ninguno").toString());

        //Aplicar_background
        pref = findPreference("aplicar_background");
        boolean aplicar = preferenciasUsuario.getBoolean("aplicar_background", false);
        //pref.setSummary(aplicar?getString(R.string.etiqueta_si):getString(R.string.etiqueta_no));

    }
}