package pi.rateusteam.rateus.Controladores;

import android.app.Activity;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class GestorGraficas {

    private BarChart grafica;
    private GestorFirebase gestorFirebase;

    public GestorGraficas(BarChart grafica, Activity activity) {
        this.grafica = grafica;
        gestorFirebase = new GestorFirebase(activity);
    }

    public void initChart() {
        grafica.animateXY(1000, 1000);
        grafica.getDescription().setText("");
        grafica.setBackgroundColor(Color.WHITE);

        gestorFirebase.crearGraficas(grafica);

        /*

        BarDataSet dataset = new BarDataSet(entries, "Proyectos Integrados");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");

        BarData data = new BarData(dataset);
        data.setBarWidth(1);
        grafica.setData(data);
        grafica.setFitBars(true);*/

    }
}
