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
import android.widget.LinearLayout;
import android.widget.TextView;

import pi.rateusteam.rateus.Controladores.LectorActivity;
import pi.rateusteam.rateus.R;

import static android.app.Activity.RESULT_OK;

public class VotacionFragment extends Fragment implements View.OnClickListener{

    private static final int ACTIVITY_LECTOR = 2;
    private LinearLayout lEscanear;

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
        lEscanear = v.findViewById(R.id.lEscanear);
        lEscanear.setOnClickListener(this);
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
                    String token = data.getStringExtra("token");
                    Log.d("Omar", "El token es: " + token);
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
        }
    }

    private void iniciarLector(){
        Intent i = new Intent(getContext(), LectorActivity.class);
        startActivityForResult(i, ACTIVITY_LECTOR);
    }
}
