package pi.rateusteam.rateus.Controladores;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import pi.rateusteam.rateus.Fragments.LoginFragment;
import pi.rateusteam.rateus.Fragments.VotacionFragment;
import pi.rateusteam.rateus.Interfaces.NavigationHost;
import pi.rateusteam.rateus.Modelo.EntradaProyecto;
import pi.rateusteam.rateus.Modelo.Proyecto;
import pi.rateusteam.rateus.Modelo.Voto;
import pi.rateusteam.rateus.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
                            gestorErrores.mostrarError(activity.getResources().getString(R.string.errorLogin));
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
                            gestorErrores.mostrarMensaje(activity.getResources().getString((R.string.proyectoRegistrado)));
                            mAuth.signOut();
                            cargarAxisX();
                            ((NavigationHost) activity).navigateTo(new LoginFragment(), false);
                        } else {
                            gestorErrores.mostrarError(activity.getResources().getString(R.string.errorCrearProyecto));
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
                float mediaViabilidadActual = dataSnapshot.child("mediaViabilidad").getValue(Float.class);
                float mediaComunicacionActual = dataSnapshot.child("mediaComunicacion").getValue(Float.class);
                float mediaCreatividadActual = dataSnapshot.child("mediaCreatividad").getValue(Float.class);
                int numVotos = dataSnapshot.child("numVotos").getValue(Integer.class);
                DatabaseReference databaseAux = FirebaseDatabase.getInstance().getReference("proyectos").child(getIdUsuario());
                databaseAux.child("votos").child(v.getIdVotante()).setValue(v);
                databaseAux.child("numVotos").setValue(numVotos+1);
                databaseAux.child("media").setValue(mediaActual + v.getMediaVoto());
                databaseAux.child("mediaViabilidad").setValue(mediaViabilidadActual + v.getVotoViabilidad());
                databaseAux.child("mediaComunicacion").setValue(mediaComunicacionActual + v.getVotoComunicacion());
                databaseAux.child("mediaCreatividad").setValue(mediaCreatividadActual + v.getVotoCreatividad());
                gestorErrores.mostrarMensaje(activity.getResources().getString(R.string.votoRegistrado));
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
                gestorErrores.mostrarError(activity.getResources().getString(R.string.errorCargarLogo));
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
                    gestorErrores.mostrarError(activity.getResources().getString(R.string.QRUsado));
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

    public void cargarGraficas(final HorizontalBarChart grafica, final String categoria) {
        //final ArrayList<BarEntry> entries = new ArrayList<>();
        final ArrayList<EntradaProyecto> proyectos = new ArrayList<>();
        //final ArrayList<String> labels = new ArrayList<>();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("proyectos");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot i: dataSnapshot.getChildren()) {
                    String idProyecto = i.child("idUsuario").getValue(String.class);
                    String txtMedia = "media";
                    switch (categoria) {
                        case "General":
                            txtMedia = "media";
                            break;
                        case "Viabilidad":
                            txtMedia = "mediaViabilidad";
                            break;
                        case "Comunicación":
                            txtMedia = "mediaComunicacion";
                            break;
                        case "Creatividad":
                            txtMedia = "mediaCreatividad";
                            break;
                    }
                    float sumaMedias = i.child(txtMedia).getValue(Float.class);
                    int numVotos = i.child("numVotos").getValue(Integer.class);
                    int axisXProyecto = i.child("axisXProyecto").getValue(Integer.class);
                    float mediaProyecto = sumaMedias/numVotos;

                    if(idProyecto.compareToIgnoreCase(getIdUsuario()) == 0) {
                        //labels.add(i.child("titulo").getValue(String.class));
                        //entries.add(new BarEntry(axisXProyecto, mediaProyecto, i.child("titulo").getValue(String.class)));
                        proyectos.add(new EntradaProyecto(i.child("titulo").getValue(String.class), axisXProyecto, mediaProyecto));
                    } else {
                        //labels.add("");
                        //entries.add(new BarEntry(axisXProyecto, mediaProyecto, ""));
                        proyectos.add(new EntradaProyecto(i.child("titulo").getValue(String.class), axisXProyecto, mediaProyecto));
                    }

                }

                final ArrayList<String> labels = new ArrayList<>();
                final ArrayList<BarEntry> entries = listarMejoresProyectos(proyectos, labels);
                // Animación de las barras
                //grafica.animateXY(100, 100);
                // Descripción de la gráfica
                grafica.getDescription().setText("");
                // Color de fondo
                grafica.setBackgroundColor(Color.WHITE);
                // Le asignamos el nombre de los labels a cada barra
                grafica.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                // Deshabilitar el zoom
                grafica.setScaleEnabled(false);


                // Diseño del axis X
                XAxis xAxis = grafica.getXAxis();
                xAxis.setCenterAxisLabels(true);
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setTextSize(40 - entries.size());
                xAxis.setTextColor(Color.rgb(39, 45, 60));
                xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);

                // Diseño de los axis Y
                YAxis rightYAxis = grafica.getAxisRight();
                YAxis leftYAxis = grafica.getAxisLeft();
                rightYAxis.setDrawGridLines(false);
                rightYAxis.setAxisMaxValue(5);
                rightYAxis.setAxisMinValue(0);
                rightYAxis.setLabelCount(5);
                leftYAxis.setDrawGridLines(false);
                leftYAxis.setAxisMaxValue(5);
                leftYAxis.setAxisMinValue(0);
                leftYAxis.setLabelCount(5);

                // Asignamos los datos de las entradas al dataset
                BarDataSet dataset = new BarDataSet(entries, "Proyectos Integrados");
                dataset.setColors(ColorTemplate.LIBERTY_COLORS);
                dataset.setDrawValues(false);

                // Asignamos el dataset a los datos de la gráfica
                BarData data = new BarData(dataset);
                data.setBarWidth((float)0.9);

                // Metemos los datos en la gráfica, notificamos del cambio y actualizamos.
                grafica.setData(data);
                grafica.notifyDataSetChanged();
                grafica.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<BarEntry> listarMejoresProyectos(ArrayList<EntradaProyecto> proyectos, ArrayList<String> labels) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        Collections.sort(proyectos, new Comparator<EntradaProyecto>() {
            @Override
            public int compare(EntradaProyecto ep1, EntradaProyecto ep2) {
                if (ep1.getMedia() == ep2.getMedia()) {
                    return 0;
                } else if (ep1.getMedia() >= ep2.getMedia()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        Iterator<EntradaProyecto> iterador = proyectos.iterator();
        int cont = 0;
        while(iterador.hasNext()) {
            if(cont<5) {
                EntradaProyecto ep = iterador.next();
                //Log.d("Omar", "Proyecto: " + ep.getTitulo() + " Nota Media: " + ep.getMedia());
                if(cont==0) {
                    gestorGraficas.setTxtPrimero(activity.getResources().getString(R.string.primeroGrafica) + ep.getTitulo());
                }
                entries.add(new BarEntry(5-cont, ep.getMedia(), ep.getTitulo()));
                labels.add("");
                cont++;
            } else {
                break;
            }
        }
        Collections.reverse(labels);

        return entries;
    }

}
