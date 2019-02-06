package pi.rateusteam.rateus.Fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.lang.reflect.Field;

import pi.rateusteam.rateus.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link LectorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LectorFragment extends Fragment implements View.OnClickListener {

    private String mParam1;
    private String mParam2;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 123;
    private String token = "";
    private String tokenanterior = "";
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private EditText txtUno, txtDos, txtTres, txtCuatro;
    private Button btnCero, btnUno, btnDos, btnTres, btnCuatro, btnCinco, btnSeis, btnSiete, btnOcho, btnNueve;
    private ImageButton btnLimpiar;


    public LectorFragment() {
        // Required empty public constructor
    }

    public static LectorFragment newInstance(String param1, String param2) {
        LectorFragment fragment = new LectorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //comentario de Jorge -------------------
        // Inflate the layout for this fragment

        // Comentario de Omar
        View v = inflater.inflate(R.layout.fragment_lector, container, false);

        cameraView = (SurfaceView) v.findViewById(R.id.camera_view);
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT > 22) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA))
                    Toast.makeText(getContext(), "Esta aplicación necesita acceder a la cámara para funcionar", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        txtUno = (EditText) v.findViewById(R.id.txtUno);
        txtDos = (EditText) v.findViewById(R.id.txtDos);
        txtTres = (EditText) v.findViewById(R.id.txtTres);
        txtCuatro = (EditText) v.findViewById(R.id.txtCuatro);
        cameraView = (SurfaceView) v.findViewById(R.id.camera_view);
        btnCero = (Button) v.findViewById(R.id.btnCero);
        btnUno = (Button) v.findViewById(R.id.btnUno);
        btnDos = (Button) v.findViewById(R.id.btnDos);
        btnTres = (Button) v.findViewById(R.id.btnTres);
        btnCuatro = (Button) v.findViewById(R.id.btnCuatro);
        btnCinco = (Button) v.findViewById(R.id.btnCinco);
        btnSeis = (Button) v.findViewById(R.id.btnSeis);
        btnSiete = (Button) v.findViewById(R.id.btnSiete);
        btnOcho = (Button) v.findViewById(R.id.btnOcho);
        btnNueve = (Button) v.findViewById(R.id.btnNueve);
        btnLimpiar = (ImageButton) v.findViewById(R.id.btnLimpiar);

        btnCero.setOnClickListener(this);
        btnUno.setOnClickListener(this);
        btnDos.setOnClickListener(this);
        btnTres.setOnClickListener(this);
        btnCuatro.setOnClickListener(this);
        btnCinco.setOnClickListener(this);
        btnSeis.setOnClickListener(this);
        btnSiete.setOnClickListener(this);
        btnOcho.setOnClickListener(this);
        btnNueve.setOnClickListener(this);
        btnLimpiar.setOnClickListener(this);
        cameraView.setOnClickListener(this);

        initQR();

        return v;
    }

    private void initQR() {

        //Creamos el lector de QR
        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        //Creama la camara
        cameraSource = new CameraSource
                .Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(620, 5000)
                .build();

        // Prepara el lector de qr
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                //Verifica si el usuario ha dado permiso para la camara
                if (ContextCompat.checkSelfPermission(getActivity().getBaseContext(),  android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // preparo el detector de QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    // obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString();

                    // verificamos que el token anterior no se igual al actual
                    // esto es util para evitar multiples llamadas empleando el mismo token
                    if (!token.equals(tokenanterior)) {

                        // guardamos el ultimo token procesado
                        tokenanterior = token;
                        rellenarCampos(token);
                        Log.i("Omar", token);


                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(2000);
                                        // limpiamos el token
                                        tokenanterior = "";
                                    }
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    Log.e("Error", "Waiting didnt work!!");
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }
            }
        });

    }

    private void rellenarCampos(String token) {
        if(comprobarCampos()==1) {
            txtUno.setText(token.charAt(0)+"");
            txtDos.setText(token.charAt(1)+"");
            txtTres.setText(token.charAt(2)+"");
            txtCuatro.setText(token.charAt(3)+"");
        }
    }

    private int comprobarCampos() {
        if (txtUno.getText().toString().compareToIgnoreCase("") == 0) {
            return 1;
        } else if (txtDos.getText().toString().compareToIgnoreCase("") == 0) {
            return 2;
        } else if (txtTres.getText().toString().compareToIgnoreCase("") == 0) {
            return 3;
        } else if (txtCuatro.getText().toString().compareToIgnoreCase("") == 0) {
            return 4;
        } else {
            return 0;
        }
    }

    private void limpiarCampos() {
        txtUno.setText("");
        txtDos.setText("");
        txtTres.setText("");
        txtCuatro.setText("");
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
        int campoRellenar;
        switch (v.getId()) {
            case R.id.camera_view:
                    cameraFocus(cameraSource, Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                break;
            case R.id.btnCero:
                campoRellenar = comprobarCampos();
                if(campoRellenar != 0) {
                    switch (campoRellenar) {
                        case 1:
                            txtUno.setText("0");
                            break;
                        case 2:
                            txtDos.setText("0");
                            break;
                        case 3:
                            txtTres.setText("0");
                            break;
                        case 4:
                            txtCuatro.setText("0");
                            break;
                    }
                }
                break;
            case R.id.btnUno:
                campoRellenar = comprobarCampos();
                if(campoRellenar != 0) {
                    switch (campoRellenar) {
                        case 1:
                            txtUno.setText("1");
                            break;
                        case 2:
                            txtDos.setText("1");
                            break;
                        case 3:
                            txtTres.setText("1");
                            break;
                        case 4:
                            txtCuatro.setText("1");
                            break;
                    }
                }
                break;
            case R.id.btnDos:
                campoRellenar = comprobarCampos();
                if(campoRellenar != 0) {
                    switch (campoRellenar) {
                        case 1:
                            txtUno.setText("2");
                            break;
                        case 2:
                            txtDos.setText("2");
                            break;
                        case 3:
                            txtTres.setText("2");
                            break;
                        case 4:
                            txtCuatro.setText("2");
                            break;
                    }
                }
                break;
            case R.id.btnTres:
                campoRellenar = comprobarCampos();
                if(campoRellenar != 0) {
                    switch (campoRellenar) {
                        case 1:
                            txtUno.setText("3");
                            break;
                        case 2:
                            txtDos.setText("3");
                            break;
                        case 3:
                            txtTres.setText("3");
                            break;
                        case 4:
                            txtCuatro.setText("3");
                            break;
                    }
                }
                break;
            case R.id.btnCuatro:
                campoRellenar = comprobarCampos();
                if(campoRellenar != 0) {
                    switch (campoRellenar) {
                        case 1:
                            txtUno.setText("4");
                            break;
                        case 2:
                            txtDos.setText("4");
                            break;
                        case 3:
                            txtTres.setText("4");
                            break;
                        case 4:
                            txtCuatro.setText("4");
                            break;
                    }
                }
                break;
            case R.id.btnCinco:
                campoRellenar = comprobarCampos();
                if(campoRellenar != 0) {
                    switch (campoRellenar) {
                        case 1:
                            txtUno.setText("5");
                            break;
                        case 2:
                            txtDos.setText("5");
                            break;
                        case 3:
                            txtTres.setText("5");
                            break;
                        case 4:
                            txtCuatro.setText("5");
                            break;
                    }
                }
                break;
            case R.id.btnSeis:
                campoRellenar = comprobarCampos();
                if(campoRellenar != 0) {
                    switch (campoRellenar) {
                        case 1:
                            txtUno.setText("6");
                            break;
                        case 2:
                            txtDos.setText("6");
                            break;
                        case 3:
                            txtTres.setText("6");
                            break;
                        case 4:
                            txtCuatro.setText("6");
                            break;
                    }
                }
                break;
            case R.id.btnSiete:
                campoRellenar = comprobarCampos();
                if(campoRellenar != 0) {
                    switch (campoRellenar) {
                        case 1:
                            txtUno.setText("7");
                            break;
                        case 2:
                            txtDos.setText("7");
                            break;
                        case 3:
                            txtTres.setText("7");
                            break;
                        case 4:
                            txtCuatro.setText("7");
                            break;
                    }
                }
                break;
            case R.id.btnOcho:
                campoRellenar = comprobarCampos();
                if(campoRellenar != 0) {
                    switch (campoRellenar) {
                        case 1:
                            txtUno.setText("8");
                            break;
                        case 2:
                            txtDos.setText("8");
                            break;
                        case 3:
                            txtTres.setText("8");
                            break;
                        case 4:
                            txtCuatro.setText("8");
                            break;
                    }
                }
                break;
            case R.id.btnNueve:
                campoRellenar = comprobarCampos();
                if(campoRellenar != 0) {
                    switch (campoRellenar) {
                        case 1:
                            txtUno.setText("9");
                            break;
                        case 2:
                            txtDos.setText("9");
                            break;
                        case 3:
                            txtTres.setText("9");
                            break;
                        case 4:
                            txtCuatro.setText("9");
                            break;
                    }
                }
                break;
            case R.id.btnLimpiar:
                limpiarCampos();
                break;
        }
    }

    private static boolean cameraFocus(@NonNull CameraSource cameraSource, @NonNull String focusMode) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(focusMode);
                        camera.setParameters(params);
                        return true;
                    }

                    return false;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
        return false;
    }
}
