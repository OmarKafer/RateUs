package pi.rateusteam.rateus.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import pi.rateusteam.rateus.Controladores.GestorFirebase;
import pi.rateusteam.rateus.Interfaces.NavigationHost;
import pi.rateusteam.rateus.Preferencias.PreferenciasActivity;
import pi.rateusteam.rateus.R;

public class AjustesDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity activity;
    public Dialog d;
    public TextView txtEditar, txtCerrarSesion, txtCancelar;

    private GestorFirebase gestorFirebase;

    public AjustesDialog(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ajustes);
        txtEditar = (TextView) findViewById(R.id.txtEditar);
        txtCerrarSesion = (TextView) findViewById(R.id.txtCerrarSesion);
        txtCancelar = (TextView) findViewById(R.id.txtCancelar);
        txtEditar.setOnClickListener(this);
        txtCerrarSesion.setOnClickListener(this);
        txtCancelar.setOnClickListener(this);

        gestorFirebase = new GestorFirebase(activity);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtEditar:
                Log.d("Omar", "Boton editar pulsado");
                //activity.finish();
                dismiss();
                ((NavigationHost) activity).navigateTo(new EditarFragment(), true); // No se si True o False
                break;
            case R.id.txtCerrarSesion:
                Log.d("Omar", "Boton preferencias pulsado");
                gestorFirebase.cerrarSesion();
                ((NavigationHost) activity).navigateTo(new LoginFragment(), false);
                dismiss();
                break;
            case R.id.txtCancelar:
                Log.d("Omar", "Boton cancelar pulsado");
                dismiss();
                break;
            default:
                Log.d("Omar", "Ningún boton pulsado");
                dismiss();
                break;
        }
    }
}
