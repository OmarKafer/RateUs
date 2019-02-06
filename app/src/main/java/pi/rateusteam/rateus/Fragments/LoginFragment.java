package pi.rateusteam.rateus.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


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
    private ImageButton btnAnyadir;
    private FirebaseAuth mAuth;



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
                ((NavigationHost) getActivity()).navigateTo(new LectorFragment(), false);
                /*mAuth.signInWithEmailAndPassword(txtEmail.getText().toString(), txtContrasenya.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    ((NavigationHost) getActivity()).navigateTo(new LectorFragment(), false);
                                } else {
                                    // Log in no funciona
                                }
                            }
                        });*/
                break;

            case R.id.btnAnyadir:
                //((NavigationHost) getActivity()).navigateTo(new RegistroFragment(), false);
                break;
        }
    }

}