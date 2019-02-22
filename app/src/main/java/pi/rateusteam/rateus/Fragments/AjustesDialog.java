package pi.rateusteam.rateus.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import pi.rateusteam.rateus.R;

public class AjustesDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity activity;
    public Dialog d;
    public TextView txtEditar, txtPreferencias, txtCancelar;

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
        txtPreferencias = (TextView) findViewById(R.id.txtPreferencias);
        txtCancelar = (TextView) findViewById(R.id.txtCancelar);
        txtEditar.setOnClickListener(this);
        txtPreferencias.setOnClickListener(this);
        txtCancelar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtEditar:
                Log.d("Omar", "Boton editar pulsado");
                //activity.finish();
                break;
            case R.id.txtPreferencias:
                Log.d("Omar", "Boton preferencias pulsado");
                //dismiss();
                break;
            case R.id.txtCancelar:
                Log.d("Omar", "Boton cancelar pulsado");
                break;
        }
        //dismiss();
    }
}
