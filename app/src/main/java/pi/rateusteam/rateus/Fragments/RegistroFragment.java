package pi.rateusteam.rateus.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import pi.rateusteam.rateus.Controladores.GestorErrores;
import pi.rateusteam.rateus.Controladores.GestorFirebase;
import pi.rateusteam.rateus.Interfaces.NavigationHost;
import pi.rateusteam.rateus.Modelo.Proyecto;
import pi.rateusteam.rateus.R;

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

    private GestorErrores gestorErrores;
    private GestorFirebase gestorFirebase;

    private static final int ACTIVITY_IMAGEN = 1;


    public RegistroFragment() {
        // Required empty public constructor
    }

    public static RegistroFragment newInstance(String param1, String param2) {
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
        btnGuardar = v.findViewById(R.id.btnGuardar);
        btnCancelar = v.findViewById(R.id.btnCancelar);
        btnGuardar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

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
                        Proyecto p = new Proyecto(txtTitulo.getText().toString(), txtDescripcion.getText().toString());
                        gestorFirebase.registrarUsuario(txtEmail.getText().toString(), txtContrasenya.getText().toString(), p);
                    } else {
                        // Contraseñas no coinciden
                        gestorErrores.mostrarError("ERROR: Las contraseñas no coinciden"); // PONER EN STRINGS
                    }
                } else {
                    gestorErrores.mostrarError("ERROR: Compruebe los campos"); // PONER EN STRINGS
                }
                break;
            case R.id.btnCancelar:
                ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false);
                break;
            /*case R.id.btnImagen:
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, ACTIVITY_IMAGEN);
                break;*/
        }
    }

    private boolean comprobarCampos() {
        if(txtEmail.getText().toString().compareToIgnoreCase("") == 0
                || txtContrasenya.getText().toString().compareToIgnoreCase("") == 0
                || txtConfirmarContrasenya.getText().toString().compareToIgnoreCase("") == 0
                || txtTitulo.getText().toString().compareToIgnoreCase("") == 0
                || txtDescripcion.getText().toString().compareToIgnoreCase("") == 0) {
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

}
