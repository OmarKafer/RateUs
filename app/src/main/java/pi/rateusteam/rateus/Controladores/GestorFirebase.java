package pi.rateusteam.rateus.Controladores;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import pi.rateusteam.rateus.Fragments.LectorFragment;
import pi.rateusteam.rateus.Fragments.LoginFragment;
import pi.rateusteam.rateus.Interfaces.NavigationHost;
import pi.rateusteam.rateus.Modelo.Proyecto;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GestorFirebase {

    private FirebaseAuth mAuth;
    private Activity activity;
    private GestorErrores gestorErrores;

    public GestorFirebase(Activity activity) {
        mAuth = FirebaseAuth.getInstance();
        this.activity = activity;
        gestorErrores = new GestorErrores(activity.getApplicationContext());
    }

    public void login(String email, String contrasenya) {
        mAuth.signInWithEmailAndPassword(email, contrasenya)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ((NavigationHost) activity).navigateTo(new LectorFragment(), false);
                        } else {
                            gestorErrores.mostrarError("ERROR: Usuario o clave incorrectos"); // PONER EN STRINGS
                        }
                    }
                });
    }

    public void registrarUsuario(String email, String contrasenya, final Proyecto p) {
        mAuth.createUserWithEmailAndPassword(email, contrasenya)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            guardarDatosProyecto(p);
                            gestorErrores.mostrarMensaje("Proyecto creado! Ya puedes iniciar sesión.");
                            mAuth.signOut();
                            ((NavigationHost) activity).navigateTo(new LoginFragment(), false);
                        } else {
                            gestorErrores.mostrarError("ERROR: No se ha podido crear el proyecto: " + task.getException()); // PONER EN STRINGS
                        }
                    }
                });
    }

    public void guardarDatosProyecto(Proyecto p) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("proyectos");
        database.child(getIdUsuario()).setValue(p);
    }

    public String getIdUsuario() {
        return mAuth.getCurrentUser().getUid();
    }
}
