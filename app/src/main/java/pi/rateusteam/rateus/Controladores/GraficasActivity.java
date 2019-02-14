package pi.rateusteam.rateus.Controladores;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import pi.rateusteam.rateus.R;

public class GraficasActivity extends AppCompatActivity {

    private GestorGraficas gestorGraficas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficas);

        BarChart grafica = (BarChart) findViewById(R.id.grafica);
        gestorGraficas = new GestorGraficas(grafica, this);
        gestorGraficas.initChart();
    }
}
