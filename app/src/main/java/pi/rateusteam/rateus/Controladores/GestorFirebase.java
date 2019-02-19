package pi.rateusteam.rateus.Controladores;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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
    static String[] arrayLabels;

    private GestorPreferencias gestorPreferencias;
    private GestorGraficas gestorGraficas;

    public GestorFirebase(Activity activity) {
        mAuth = FirebaseAuth.getInstance();
        this.activity = activity;
        gestorErrores = new GestorErrores(activity.getApplicationContext());
        gestorPreferencias = new GestorPreferencias(activity);
    }
    public GestorFirebase(Activity activity, GestorGraficas gestorGraficas) {
        mAuth = FirebaseAuth.getInstance();
        this.activity = activity;
        gestorErrores = new GestorErrores(activity.getApplicationContext());
        gestorPreferencias = new GestorPreferencias(activity);
        this.gestorGraficas = gestorGraficas;
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

    public void anyadirVoto(final Voto v) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("proyectos").child(getIdUsuario());
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float mediaActual = dataSnapshot.child("media").getValue(Float.class);
                int numVotos = dataSnapshot.child("numVotos").getValue(Integer.class);
                DatabaseReference databaseAux = FirebaseDatabase.getInstance().getReference("proyectos").child(getIdUsuario());
                databaseAux.child("votos").child(v.getIdVotante()).setValue(v);
                databaseAux.child("numVotos").setValue(numVotos+1);
                databaseAux.child("media").setValue(mediaActual+v.getMediaVoto());
                gestorErrores.mostrarMensaje("¡VOTO REGISTRADO!");  // PONER EN STRINGS
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                if (dataSnapshot.hasChildren()) {
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

    public void cargarAxisX() {
        axisX = 0;
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("proyectos");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot i: dataSnapshot.getChildren()) {
                        Proyecto p = i.getValue(Proyecto.class);
                        database.child(p.getIdUsuario()).child("axisXProyecto").setValue(axisX);
                        axisX++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void cargarGraficas(final HorizontalBarChart grafica) {
        final ArrayList<BarEntry> entries = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList<>();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("proyectos");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot i: dataSnapshot.getChildren()) {
                    String idProyecto = i.child("idUsuario").getValue(String.class);
                    float sumaMedias = i.child("media").getValue(Float.class);
                    int numVotos = i.child("numVotos").getValue(Integer.class);
                    int axisXProyecto = i.child("axisXProyecto").getValue(Integer.class);
                    float mediaProyecto = sumaMedias/numVotos;
                    if(numVotos>0){
                        if(idProyecto.compareToIgnoreCase(getIdUsuario()) == 0) {
                            //labels.add(i.child("titulo").getValue(String.class));
                            entries.add(new BarEntry(axisXProyecto, mediaProyecto, i.child("titulo").getValue(String.class)));
                            Log.d("Omar", i.child("titulo").getValue(String.class));
                        } else {
                            //labels.add("");
                            entries.add(new BarEntry(axisXProyecto, mediaProyecto, ""));
                        }
                        labels.add(i.child("titulo").getValue(String.class)); // TEST
                    }
                }

                /*arrayLabels = new String[labels.size()];
                arrayLabels = labels.toArray(arrayLabels);
                IAxisValueFormatter formatter = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        Log.d("Omar", "Valor del array ahora: " + (int)value);
                        String valor = arrayLabels[(int)value];
                        return valor;
                    }
                };

                XAxis xAxis = grafica.getXAxis();
                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                xAxis.setValueFormatter(formatter);*/

                BarDataSet dataset = new BarDataSet(entries, "Proyectos Integrados");
                dataset.setColors(ColorTemplate.LIBERTY_COLORS);
                dataset.setDrawValues(false);

                BarData data = new BarData(dataset);
                data.setBarWidth((float)0.9);

                YAxis rightYAxis = grafica.getAxisRight();
                YAxis leftYAxis = grafica.getAxisLeft();
                rightYAxis.setAxisMaxValue(5);
                rightYAxis.setAxisMinValue(0);
                rightYAxis.setLabelCount(10);
                leftYAxis.setAxisMaxValue(5);
                leftYAxis.setAxisMinValue(0);
                leftYAxis.setLabelCount(10);

                grafica.setFitBars(true); // make the x-axis fit exactly all bars
                //grafica.getXAxis().setPosition(XAxis.XAxisPosition.TOP_INSIDE);
                grafica.getXAxis().setCenterAxisLabels(true);
                //grafica.getXAxis().setLabelCount(labels.size());
                //grafica.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                grafica.getXAxis().setLabelCount(Integer.MAX_VALUE, true);
                grafica.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels) {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return super.getFormattedValue(value, axis);
                    }
                });
                grafica.setData(data);
                grafica.notifyDataSetChanged();
                grafica.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
