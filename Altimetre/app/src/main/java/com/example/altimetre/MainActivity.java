package com.example.altimetre;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensorPresion;
    private double temperatura = 288.15;
    private double P0 = 1023.0;
    private TextView textoAltura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // S'inicialitzen els sensors i la variable textoAltura s'enllaça amb l'XML
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorPresion = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        textoAltura = findViewById(R.id.textoAltura);

        // Verifica si el dispositiu té un sensor de pressió atmosfèrica
        if (sensorPresion != null) {
            // Registra el listener per obtenir dades del sensor en temps real
            sensorManager.registerListener(this, sensorPresion, SensorManager.SENSOR_DELAY_NORMAL);
        }else {
            textoAltura.setText(R.string.error_al_encontrar_la_altura);
        }
    }

    // Aquest mètode s'executarà automàticament quan
    // hi hagi canvis en les dades del sensor
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            double presion = event.values[0]; // Obté el valor de pressió en hPa
            double altura = calculaAltura(presion);
            textoAltura.setText(String.format("La altura actual es de %.2f m", altura));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // Amb l'ajuda del valor del paràmetre es calcula l'altura
    public double calculaAltura(double P) {
        return (temperatura / 0.0065) * (1 - Math.pow(P / P0, 0.1903));
    }

}