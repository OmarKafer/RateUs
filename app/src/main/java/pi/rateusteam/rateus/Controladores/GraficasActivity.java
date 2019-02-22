package pi.rateusteam.rateus.Controladores;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
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
    private Spinner spinnerCategoriaGraficas;
    private TextView txtPrimero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficas);

        txtPrimero = findViewById(R.id.txtPrimero);

        spinnerCategoriaGraficas = findViewById(R.id.spinnerCategoriaGraficas);
        spinnerCategoriaGraficas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gestorGraficas.setCiclo(spinnerCategoriaGraficas.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        HorizontalBarChart grafica = (HorizontalBarChart) findViewById(R.id.grafica);
        gestorGraficas = new GestorGraficas(grafica, this, spinnerCategoriaGraficas.getSelectedItem().toString(), txtPrimero);
        gestorGraficas.initChart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.d("Omar", "Sales del activity de las gráficas");
            gestorGraficas.matarHilo();
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
