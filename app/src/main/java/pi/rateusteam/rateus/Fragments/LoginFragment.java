package pi.rateusteam.rateus.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import pi.rateusteam.rateus.Controladores.GestorErrores;
import pi.rateusteam.rateus.Interfaces.NavigationHost;
import pi.rateusteam.rateus.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText txtEmail;
    private EditText txtContrasenya;
    private Button btnEntrar;
    private TextView btnAnyadir;
    private FirebaseAuth mAuth;
    private GestorErrores gestorErrores;



    public LoginFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        // Instanciamos los elementos de la vista
        txtEmail = v.findViewById(R.id.txtEmail);
        txtContrasenya = v.findViewById(R.id.txtContrasenya);
        btnEntrar = v.findViewById(R.id.btnEntrar);
        btnAnyadir = v.findViewById(R.id.btnAnyadir);
        btnEntrar.setOnClickListener(this);
        btnAnyadir.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

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
        switch(v.getId()) {
            case R.id.btnEntrar:
                if (comprobarCampos()) {
                    login(txtEmail.getText().toString(), txtContrasenya.getText().toString());
                } else {
                    ocultarTeclado();
                    gestorErrores.mostrarError("ERROR: Comprueba los campos"); // PONER EN STRINGS
                }
                break;

            case R.id.btnAnyadir:
                ((NavigationHost) getActivity()).navigateTo(new RegistroFragment(), false);
                break;
        }
    }

    private void login(String email, String contrasenya) {
        mAuth.signInWithEmailAndPassword(email, contrasenya)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ((NavigationHost) getActivity()).navigateTo(new LectorFragment(), false);
                        } else {
                            ocultarTeclado();
                            gestorErrores.mostrarError("ERROR: Usuario o clave incorrectos"); // PONER EN STRINGS
                        }
                    }
                });
    }

    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(txtContrasenya.getWindowToken(), 0);
    }

    private boolean comprobarCampos() {
        if(txtEmail.getText().toString().compareToIgnoreCase("") == 0
                || txtContrasenya.getText().toString().compareToIgnoreCase("") == 0) {
            return false;
        } else {
            return true;
        }
    }

}
