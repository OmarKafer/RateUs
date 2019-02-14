package pi.rateusteam.rateus.Controladores;

import android.app.Activity;
import android.content.SharedPreferences;

public class GestorPreferencias {

    public static final String PREFS = "My Preferences";
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;




    public GestorPreferencias(Activity activity){
        sharedPreferences = activity.getSharedPreferences(PREFS,Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void introducirPreferencia(String clave , boolean valor){
        editor.putBoolean(clave,valor);
    }

    public boolean recuperarPreferencia(String clave){
        return sharedPreferences.getBoolean(clave,false);
    }
}
