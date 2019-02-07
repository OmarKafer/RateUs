package pi.rateusteam.rateus.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import pi.rateusteam.rateus.Interfaces.NavigationHost;
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
        switch (v.getId()) {
            case R.id.btnGuardar:
                // Guardar registro
                break;
            case R.id.btnCancelar:
                ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false);
                break;
        }
    }
}
