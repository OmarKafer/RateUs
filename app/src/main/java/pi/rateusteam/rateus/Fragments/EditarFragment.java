package pi.rateusteam.rateus.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import pi.rateusteam.rateus.Controladores.GestorFirebase;
import pi.rateusteam.rateus.Interfaces.NavigationHost;
import pi.rateusteam.rateus.R;

import static android.app.Activity.RESULT_OK;

public class EditarFragment extends Fragment implements View.OnClickListener {

    private TextView txtTitulo, txtDescripcion;
    private ImageView imgLogo;
    private EditText txtTituloEditar, txtDescripcionEditar;
    private Spinner spinnerCiclo;
    private Uri uri = null;
    private Button btnCancelar, btnGuardar;

    private GestorFirebase gestorFirebase;


    private static final int ACTIVITY_IMAGEN = 1;

    public EditarFragment() {
        // Required empty public constructor
    }

    public static EditarFragment newInstance(String param1, String param2) {
        EditarFragment fragment = new EditarFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_editar, container, false);
        txtTitulo = v.findViewById(R.id.txtTitulo);
        txtDescripcion = v.findViewById(R.id.txtDescripcion);
        imgLogo = v.findViewById(R.id.imgLogo);
        imgLogo.setOnClickListener(this);
        txtTituloEditar = v.findViewById(R.id.txtTituloEditar);
        txtDescripcionEditar = v.findViewById(R.id.txtDescripcionEditar);
        spinnerCiclo = v.findViewById(R.id.spinnerCiclo);
        btnGuardar = v.findViewById(R.id.btnGuardarEditar);
        btnGuardar.setOnClickListener(this);
        btnCancelar = v.findViewById(R.id.btnCancelarEditar);
        btnCancelar.setOnClickListener(this);

        gestorFirebase = new GestorFirebase(getActivity());

        cargarDatosProyecto();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void cargarDatosProyecto() {
        gestorFirebase.recuperarProyecto(txtTitulo, txtDescripcion, imgLogo);
        txtTituloEditar.setText(txtTitulo.getText().toString());
        txtDescripcionEditar.setText(txtDescripcion.getText().toString());
    }

    private void abrirGaleria() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, ACTIVITY_IMAGEN);
    }

    @Override
    public void onClick(View v) {
        ocultarTeclado();
        switch (v.getId()) {
            case R.id.imgLogo:
                abrirGaleria();
                break;
            case R.id.btnGuardar:
                if (comprobarCampos()) {
                    gestorFirebase.editarProyecto(txtTituloEditar.getText().toString(), txtDescripcionEditar.getText().toString(), spinnerCiclo.getSelectedItem().toString());
                }
                break;
            case R.id.btnCancelar:
                ((NavigationHost) getActivity()).navigateTo(new VotacionFragment(), false);
                break;
        }
    }

    private boolean comprobarCampos() {
        if(txtTituloEditar.getText().toString().compareToIgnoreCase("") == 0
                || txtDescripcionEditar.getText().toString().compareToIgnoreCase("") == 0
                || uri == null
                || spinnerCiclo.getSelectedItem().toString().compareToIgnoreCase(getResources().getString(R.string.spinnerCiclo)) == 0) {
            return false;
        } else {
            return true;
        }
    }

    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(txtTituloEditar.getWindowToken(), 0);
    }

    private void cargarImagen(Intent data) {
        imgLogo.setImageURI(data.getData());
        uri = data.getData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ACTIVITY_IMAGEN:
                if(resultCode == RESULT_OK) {
                    cargarImagen(data);
                }
                break;
        }
    }
}
