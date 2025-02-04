package com.example.podometre;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.Toast;

// implementa SensorEvenListener per escoltar qualsevol canvi del sensor
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // comptador de passos per actualitzar el TextView
    private int comptador = 0;
    private TextView msg;
    // declaro una classe privada de SensorManager per gestionar un tipus de sensor per comptar passos
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float eixZIni = 0;
    private boolean primeraLectura = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        msg = findViewById(R.id.steps);
        // Inicialitzem el sensorManager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Obtinc l'acceleròmetre
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Si tenim aquest recurs disponible, registra la classe actual this que reb dades del sensor
        // com un Listener pel sensor acceleròmetre i definim la velocitat en que es reb les actualitzacions
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "El teu dispositiu no té acceleròmetre!", Toast.LENGTH_LONG).show();
        }
    }
    public void resetComptador(View view) {
        comptador = 0;
        msg.setText("Passos donats: " + comptador);
        Toast.makeText(this, "Podòmetre reiniciat.", Toast.LENGTH_SHORT).show();
    }
    // Per cada esdeveniment canviat detectat pel Sensor i es de tipus acceleròmetre
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // obté els valors de cada eixos per comprobar si s'ha mogut
            float z = event.values[2];

            // Prevé errors en la primera lectura
            if (primeraLectura) {
                eixZIni = z;
                primeraLectura = false;
                return;
            }
            float desplacament = Math.abs(z - eixZIni);
            if (desplacament > 2.5) {
            // pasos que es tindràn en compte
                comptador++;
                msg.setText("Passos donats: " + comptador);
                if (comptador == 100) {
                    Toast.makeText(this, "Felicitats! Has arribat als 100 passos, continúa així!", Toast.LENGTH_LONG).show();
                }
                Log.d("Accelerometer", "Z: " + z);
            }
            eixZIni = z;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Gestionar canvis de precisió si cal
        if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.w("Accelerometer", "¡Precisión del sensor baja!");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrem el Listener per estalviar la bateria tancant el recurs
        if (sensorManager != null) sensorManager.unregisterListener(this);
    }
}