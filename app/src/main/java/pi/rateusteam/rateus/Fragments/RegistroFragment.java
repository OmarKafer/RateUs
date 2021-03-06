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

import pi.rateusteam.rateus.Controladores.GestorErrores;
import pi.rateusteam.rateus.Controladores.GestorFirebase;
import pi.rateusteam.rateus.Interfaces.NavigationHost;
import pi.rateusteam.rateus.Modelo.Proyecto;
import pi.rateusteam.rateus.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RegistroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistroFragment extends Fragment implements View.OnClickListener {

    private EditText txtEmail, txtContrasenya, txtConfirmarContrasenya, txtTitulo, txtDescripcion;
    private Button btnGuardar, btnCancelar;
    private ImageView btnImagen;
    private Spinner spinnerCiclo;
    private Uri uri = null;

    private GestorErrores gestorErrores;
    private GestorFirebase gestorFirebase;

    private static final int ACTIVITY_IMAGEN = 1;



    public RegistroFragment() {
        // Required empty public constructor
    }

    public static RegistroFragment newInstance() {
        RegistroFragment fragment = new RegistroFragment();
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
        View v =inflater.inflate(R.layout.fragment_registro, container, false);
        txtEmail = v.findViewById(R.id.txtEmail);
        txtContrasenya = v.findViewById(R.id.txtContrasenya);
        txtConfirmarContrasenya = v.findViewById(R.id.txtConfirmarContrasenya);
        txtTitulo = v.findViewById(R.id.txtTitulo);
        txtDescripcion = v.findViewById(R.id.txtDescripcion);
        spinnerCiclo = v.findViewById(R.id.spinnerCiclo);
        btnGuardar = v.findViewById(R.id.btnGuardar);
        btnCancelar = v.findViewById(R.id.btnCancelar);
        btnImagen = v.findViewById(R.id.btnImagen);
        btnGuardar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        btnImagen.setOnClickListener(this);

        gestorFirebase = new GestorFirebase(getActivity());
        gestorErrores = new GestorErrores(getContext());
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

    @Override
    public void onClick(View v) {
        ocultarTeclado();
        switch (v.getId()) {
            case R.id.btnGuardar:
                if(comprobarCampos()) {
                    if(comprobarContrasenyas()) {
                        Proyecto p = new Proyecto(txtTitulo.getText().toString(), txtDescripcion.getText().toString(), null, spinnerCiclo.getSelectedItem().toString(), 0, 0, 0, 0, 0);
                        gestorFirebase.registrarUsuario(txtEmail.getText().toString(), txtContrasenya.getText().toString(), p, uri);
                    } else {
                        // Contraseñas no coinciden
                        gestorErrores.mostrarError(getActivity().getResources().getString(R.string.errorContrasenyas)); // PONER EN STRINGS
                    }
                } else {
                    gestorErrores.mostrarError(getActivity().getResources().getString(R.string.errorCampos)); // PONER EN STRINGS
                }
                break;
            case R.id.btnCancelar:
                ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false);
                break;
            case R.id.btnImagen:
                abrirGaleria();
                break;
        }
    }

    private void abrirGaleria() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, ACTIVITY_IMAGEN);
    }

    private boolean comprobarCampos() {
        if(txtEmail.getText().toString().compareToIgnoreCase("") == 0
                || txtContrasenya.getText().toString().compareToIgnoreCase("") == 0
                || txtConfirmarContrasenya.getText().toString().compareToIgnoreCase("") == 0
                || txtTitulo.getText().toString().compareToIgnoreCase("") == 0
                || txtDescripcion.getText().toString().compareToIgnoreCase("") == 0
                || uri == null
                || spinnerCiclo.getSelectedItem().toString().compareToIgnoreCase(getResources().getString(R.string.spinnerCiclo)) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean comprobarContrasenyas() {
        if(txtContrasenya.getText().toString().compareToIgnoreCase(txtConfirmarContrasenya.getText().toString()) == 0) {
            return true;
        } else {
            return false;
        }
    }

    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(txtContrasenya.getWindowToken(), 0);
    }

    private void cargarImagen(Intent data) {
        btnImagen.setImageURI(data.getData());
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
