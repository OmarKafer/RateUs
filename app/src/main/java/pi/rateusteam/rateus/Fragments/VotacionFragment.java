package pi.rateusteam.rateus.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.KeyEvent;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;

import pi.rateusteam.rateus.Controladores.GestorErrores;
import pi.rateusteam.rateus.Controladores.GestorFirebase;
import pi.rateusteam.rateus.Controladores.LectorActivity;
import pi.rateusteam.rateus.Modelo.Voto;
import pi.rateusteam.rateus.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static pi.rateusteam.rateus.MainActivity.permitirAtras;

public class VotacionFragment extends Fragment implements View.OnClickListener{

    private static final int ACTIVITY_LECTOR = 2;
    private LinearLayout lEscanear, lVotos, lBoton;
    private GestorFirebase gestorFirebase;
    private GestorErrores gestorErrores;

    private TextView txtTitulo, txtDescripcion;
    private ImageView imgLogo;
    private String votante;
    private RatingBar rbCreatividad, rbViabilidad, rbComunicacion;
    private Button btnVotar;

    public VotacionFragment() {
        // Required empty public constructor
    }

    public static VotacionFragment newInstance(String param1, String param2) {
        VotacionFragment fragment = new VotacionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_votacion, container, false);
        txtTitulo = v.findViewById(R.id.txtTitulo);
        txtDescripcion = v.findViewById(R.id.txtDescripcion);
        imgLogo = v.findViewById(R.id.imgLogo);

        btnVotar = v.findViewById(R.id.btnVotar);
        btnVotar.setOnClickListener(this);

        lEscanear = v.findViewById(R.id.lEscanear);
        lEscanear.setOnClickListener(this);

        lVotos = v.findViewById(R.id.lVotos);
        lBoton = v.findViewById(R.id.lBoton);

        gestorFirebase = new GestorFirebase(getActivity());
        gestorErrores = new GestorErrores(getContext());

        rbCreatividad = v.findViewById(R.id.rbCreatividad);
        rbViabilidad = v.findViewById(R.id.rbViabilidad);
        rbComunicacion = v.findViewById(R.id.rbComunicacion);

        ventanaSinVotos();
        cargarDatosProyecto();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ACTIVITY_LECTOR:
                if(resultCode == RESULT_OK) {
                    votante = data.getStringExtra("token");
                    comprobarVotante();
                } else if(resultCode == RESULT_CANCELED) {
                    ventanaSinVotos();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.lEscanear:
                iniciarLector();
                break;
            case R.id.btnVotar:
                votar();
                ventanaSinVotos();
                break;
        }
    }

    private void iniciarLector(){
        Intent i = new Intent(getContext(), LectorActivity.class);
        startActivityForResult(i, ACTIVITY_LECTOR);
    }

    public void ventanaSinVotos() {
        permitirAtras = true;
        lEscanear.setVisibility(View.VISIBLE);
        lVotos.setVisibility(View.GONE);
        lBoton.setVisibility(View.GONE);
    }

    public void ventanaConVotos() {
        permitirAtras = false;
        rbComunicacion.setRating((float)1);
        rbViabilidad.setRating((float)1);
        rbCreatividad.setRating((float)1);
        lEscanear.setVisibility(View.GONE);
        lVotos.setVisibility(View.VISIBLE);
        lBoton.setVisibility(View.VISIBLE);
    }

    private void votar() {
        Voto v = new Voto(rbCreatividad.getRating(), rbViabilidad.getRating(), rbComunicacion.getRating(), votante);
        gestorFirebase.anyadirVoto(v);
    }

    private void cargarDatosProyecto() {
        gestorFirebase.recuperarProyecto(txtTitulo, txtDescripcion, imgLogo);
    }

    private void comprobarVotante() {
        gestorFirebase.comprobarVotante(votante, this);
    }

}
