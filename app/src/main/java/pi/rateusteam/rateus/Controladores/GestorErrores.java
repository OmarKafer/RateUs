package pi.rateusteam.rateus.Controladores;

import android.content.Context;
import android.widget.Toast;

public class GestorErrores {

    private Context context;

    public GestorErrores(Context context) {
        this.context = context;
    }

    public void mostrarError(String error) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }

    public void mostrarMensaje(String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }
}
