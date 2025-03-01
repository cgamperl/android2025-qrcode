package at.wifi.swdev.qrcodescanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import at.wifi.swdev.qrcodescanner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActivityResultLauncher<String> launcher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                // Hat der Benutzer den Zugriff erlaubt bzw. die Berechtigung erteilt?
                if (granted) {
                    // Ja, hat er
                    startCamera();
                } else {
                    // Nein, hat nicht...
                    // -> wir dürfen nicht auf die Kamera zugreifen und können daher kein Barcodes erkennen...
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Prüfen, ob Berechtigung für Kamera vorhanden
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Wir haben die Berechtigung bereits
            startCamera();
        } else {
            // Wir die Berechtigung noch nicht --> Benutzer darum bitten
            launcher.launch(Manifest.permission.CAMERA);
        }


    }

    private void startCamera() {

        // Kamera Stream in PreviewView anzeigen
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            // "Arbeitsauftrag" für Executor (Thread, zweiter Parameter)

            ProcessCameraProvider cameraProvider = null;

            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException | InterruptedException exception) {
                Log.e("", "Error while accessing camera");
                exception.printStackTrace();
                return;
            }

            // Ab hier haben wir Zugriff auf den CameraProvider
            // -> Vorschau der Kamera in userer Activity anzeigen
            // 1. Vorschau (Preview) erzeugen
            Preview preview = new Preview.Builder().build();
            // 2. Vorschau im dafür vorgesehenen PreviewView anzeigen
            preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

            // Später: Barcode erkennen


            // Welche Kamera soll das Bild liefern?
            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
            try {
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview); // TODO: Bildanalyse als Usecase hinzufügen
            } catch (Exception e) {
                Log.e("", "Error assigning use cases");
                e.printStackTrace();
            }


        }, ContextCompat.getMainExecutor(this));

    }
}