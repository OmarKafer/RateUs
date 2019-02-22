package pi.rateusteam.rateus.Controladores;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class GestorGraficas {

    private Thread hilo;
    private HorizontalBarChart grafica;
    private GestorFirebase gestorFirebase;
    private String ciclo;
    private TextView txtPrimero;

    public GestorGraficas(HorizontalBarChart grafica, Activity activity, String ciclo, TextView txtPrimero) {
        this.grafica = grafica;
        gestorFirebase = new GestorFirebase(activity, this);
        this.ciclo = ciclo;
        this.txtPrimero = txtPrimero;
    }

    public void setTxtPrimero(String txt) {
        txtPrimero.setText(txt);
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public void initChart() {
        iniciarHilo();
    }

    private void iniciarHilo() {
        hilo = new Thread(new Runnable() {
            public void run() {
                try {
                    synchronized (this) {
                        //gestorFirebase.cargarAxisX();
                        while(true) {
                            wait(2000);
                            gestorFirebase.cargarGraficas(grafica, ciclo);
                            Thread.interrupted();
                        }
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    Log.e("Error", "Waiting didnt work!!");
                    e.printStackTrace();
                }
            }
        });
        hilo.start();
    }

    public void matarHilo() {
        hilo.interrupt();
    }
}
