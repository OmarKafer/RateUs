package pi.rateusteam.rateus.Controladores;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import pi.rateusteam.rateus.Fragments.LectorFragment;
import pi.rateusteam.rateus.Fragments.LoginFragment;
import pi.rateusteam.rateus.Fragments.VotacionFragment;
import pi.rateusteam.rateus.Interfaces.NavigationHost;
import pi.rateusteam.rateus.Modelo.Proyecto;
import pi.rateusteam.rateus.Modelo.Voto;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
                            ((NavigationHost) activity).navigateTo(new VotacionFragment(), false);
                        } else {
                            gestorErrores.mostrarError("ERROR: Usuario o clave incorrectos"); // PONER EN STRINGS
                        }
                    }
                });
    }

    public void registrarUsuario(final String email, final String contrasenya, final Proyecto p, final Uri uri) {
        mAuth.createUserWithEmailAndPassword(email, contrasenya)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            p.setIdUsuario(getIdUsuario());
                            guardarDatosProyecto(p, uri);
                            gestorErrores.mostrarMensaje("Proyecto creado! Ya puedes iniciar sesión.");
                            mAuth.signOut();
                            ((NavigationHost) activity).navigateTo(new LoginFragment(), false);
                        } else {
                            gestorErrores.mostrarError("ERROR: No se ha podido crear el proyecto: " + task.getException()); // PONER EN STRINGS
                        }
                    }
                });
    }

    public void guardarDatosProyecto(Proyecto p, Uri uri) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("proyectos");
        database.child(getIdUsuario()).setValue(p);
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("logos").child(getIdUsuario());
        mStorage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Omar", "Se ha guardado la imagen");
            }
        });
    }

    public void anyadirVoto(Voto v) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("proyectos").child(getIdUsuario());
        database.child(v.getIdVotante()).setValue(v);
    }

    public void recuperarProyecto(final TextView txtTitulo, final TextView txtDescripcion, final ImageView imgLogo) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("proyectos");
        Query q = database.orderByChild("idUsuario").equalTo(getIdUsuario());
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot i: dataSnapshot.getChildren()) {
                    Proyecto p = i.getValue(Proyecto.class);
                    txtTitulo.setText(p.getTitulo());
                    txtDescripcion.setText(p.getDescripcion());
                    recuperarLogoProyecto(imgLogo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recuperarLogoProyecto(final ImageView imgLogo) {
        //
    }

    public String getIdUsuario() {
        return mAuth.getCurrentUser().getUid();
    }
}
