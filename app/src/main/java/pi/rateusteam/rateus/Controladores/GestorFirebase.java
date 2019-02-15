package pi.rateusteam.rateus.Controladores;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class GestorFirebase {



    private FirebaseAuth mAuth;
    private Activity activity;
    private GestorErrores gestorErrores;
    private int axisX = 0;

    private GestorPreferencias gestorPreferencias;

    public GestorFirebase(Activity activity) {
        mAuth = FirebaseAuth.getInstance();
        this.activity = activity;
        gestorErrores = new GestorErrores(activity.getApplicationContext());
        gestorPreferencias = new GestorPreferencias(activity);


    }

    public void login(final String email, String contrasenya) {
        mAuth.signInWithEmailAndPassword(email, contrasenya)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(email.compareToIgnoreCase("info@rateus.es") == 0 ){
                                gestorPreferencias.introducirPreferencia("admin",true);
                                Log.d("Jorge","Prefrencia: "+gestorPreferencias.recuperarPreferencia("admin"));
                            }else {
                                gestorPreferencias.introducirPreferencia("admin",false);
                                Log.d("Jorge","Prefrencia: "+gestorPreferencias.recuperarPreferencia("admin"));

                            }
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
                            gestorErrores.mostrarMensaje("Proyecto creado! Ya puedes iniciar sesión.");   // PONER EN STRINGS
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
                // Se guarda la imagen de usuario.
            }
        });
    }

    public void anyadirVoto(Voto v) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("proyectos").child(getIdUsuario()).child("votos");
        database.child(v.getIdVotante()).setValue(v);
        gestorErrores.mostrarMensaje("¡VOTO REGISTRADO!");  // PONER EN STRINGS
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
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("logos").child(getIdUsuario());
        mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(activity.getApplicationContext())
                        .load(uri)
                        .into(imgLogo);
            }
        });
        mStorage.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                gestorErrores.mostrarError("Ha habido un error al cargar el logo"); // METER A STRINGS
            }
        });
    }

    public String getIdUsuario() {
        return mAuth.getCurrentUser().getUid();
    }

    public void comprobarVotante(String votante, final VotacionFragment f) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("proyectos").child(getIdUsuario()).child("votos");
        Query q = database.orderByChild("idVotante").equalTo(votante);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    // El voto de este usuario ya se ha registrado.
                    f.ventanaSinVotos();
                    gestorErrores.mostrarError("Este QR ya ha votado el proyecto.");  // METER A STRINGS
                } else {
                    // El voto de este usuario no se ha registrado.
                    f.ventanaConVotos();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void crearGraficas(final BarChart grafica) {
        final ArrayList<BarEntry> entries = new ArrayList<>();
        final ArrayList<Voto> votos = new ArrayList<>();
        final BarDataSet dataset = new BarDataSet(entries, "Proyectos Integrados");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        final BarData data = new BarData(dataset);
        data.setBarWidth((float)0.9);
        grafica.setData(data);
        grafica.setFitBars(true);
        grafica.getAxisRight().setDrawGridLines(false);
        grafica.getAxisLeft().setDrawGridLines(false);
        grafica.getXAxis().setDrawGridLines(false);

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("proyectos");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot i: dataSnapshot.getChildren()) {
                    votos.clear();
                    final String idProyecto = i.child("idUsuario").getValue(String.class);
                    DatabaseReference databaseAux = FirebaseDatabase.getInstance().getReference("proyectos").child(idProyecto).child("votos");
                    databaseAux.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot i: dataSnapshot.getChildren()) {
                                votos.add(i.getValue(Voto.class));
                            }
                            float media = calcularMedia(votos);
                            Log.d("Omar", "AxisX: " + axisX + " Media: " + media);
                            database.child(idProyecto).child("axisXProyecto").setValue(axisX);
                            BarEntry entrada = new BarEntry(axisX, media);
                            dataset.addEntry(entrada);
                            data.notifyDataChanged(); // let the data know a dataSet changed
                            grafica.notifyDataSetChanged(); // let the chart know it's data changed
                            grafica.invalidate();
                            axisX++;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                actualizarGraficas(grafica, dataset, data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void actualizarGraficas(final BarChart grafica, final BarDataSet dataset, final BarData data) {
        final ArrayList<Voto> votos = new ArrayList<>();

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("proyectos");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot i: dataSnapshot.getChildren()) {
                    votos.clear();
                    String idProyecto = i.child("idUsuario").getValue(String.class);
                    final int axisXProyecto = i.child("axisXProyecto").getValue(Integer.class);
                    DatabaseReference databaseAux = FirebaseDatabase.getInstance().getReference("proyectos").child(idProyecto).child("votos");
                    databaseAux.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot i: dataSnapshot.getChildren()) {
                                votos.add(i.getValue(Voto.class));
                            }
                            float media = calcularMedia(votos);
                            float[] test ={axisXProyecto, media};
                            dataset.getEntryForIndex(axisXProyecto).setVals(test);
                            dataset.notifyDataSetChanged();
                            data.notifyDataChanged(); // let the data know a dataSet changed
                            grafica.notifyDataSetChanged(); // let the chart know it's data changed
                            grafica.invalidate();
                            axisX++;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private float calcularMedia(ArrayList<Voto> votos) {
        ArrayList<Float> medias = new ArrayList<>();
        medias.clear();
        Iterator it = votos.iterator();
        while(it.hasNext()) {
            Voto v = (Voto) it.next();
            medias.add((float)(v.getVotoComunicacion() + v.getVotoCreatividad() + v.getVotoViabilidad())/3);
            Log.d("Operacion", "Nueva Media: " + (float)(v.getVotoComunicacion() + v.getVotoCreatividad() + v.getVotoViabilidad())/3);
        }
        float mediaTotal = 0;

        it = medias.iterator();
        while(it.hasNext()) {
            mediaTotal += (float)it.next();
        }
        mediaTotal = mediaTotal / medias.size();
        Log.d("Omar", "MediaTotal: " + mediaTotal + " Entre: " + medias.size());
        //Log.d("Omar", "Total: " + mediaTotal);
        return mediaTotal;
    }
}
