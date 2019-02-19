package pi.rateusteam.rateus.Controladores;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

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

    public GestorGraficas(HorizontalBarChart grafica, Activity activity) {
        this.grafica = grafica;
        gestorFirebase = new GestorFirebase(activity, this);
    }

    public void initChart() {
        grafica.animateXY(1000, 1000);
        grafica.getDescription().setText("");
        grafica.setBackgroundColor(Color.WHITE);
        grafica.getAxisRight().setDrawGridLines(false);
        grafica.getAxisLeft().setDrawGridLines(false);
        grafica.getXAxis().setDrawGridLines(false);

        iniciarHilo();

    }

    private void iniciarHilo() {
        hilo = new Thread(new Runnable() {
            public void run() {
                try {
                    synchronized (this) {
                        gestorFirebase.cargarAxisX();
                        while(true) {
                            wait(2000);
                            gestorFirebase.cargarGraficas(grafica);
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
