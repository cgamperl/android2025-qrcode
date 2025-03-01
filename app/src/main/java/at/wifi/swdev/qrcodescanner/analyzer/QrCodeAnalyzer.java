package at.wifi.swdev.qrcodescanner.analyzer;

import android.content.Context;
import android.media.Image;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

public class QrCodeAnalyzer implements ImageAnalysis.Analyzer {
    private final Context context;

    public QrCodeAnalyzer(Context context) {
        this.context = context;
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {

        Image image = imageProxy.getImage();

        if (image != null) {
            // Wir haben ein Bild, das wir analysieren können

            // Schritt 1: Setup - Objekt für Analyse zusammenbauen
            InputImage inputImage = InputImage.fromMediaImage(image, imageProxy.getImageInfo().getRotationDegrees());

            // Barcode-Erkennung konfigurieren
            BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build();

            // Barcode-Scanner bauen
            BarcodeScanner scanner = BarcodeScanning.getClient(options);

            // Bilderkennung
            scanner.process(inputImage).addOnSuccessListener(barcodes -> {

                // In dem Bild können mehrere Barcodes erkannt worden sein
                for (Barcode barcode : barcodes) {
                    Toast.makeText(context, "Barcode erkannt:" + barcode.getRawValue(), Toast.LENGTH_SHORT).show();
                }
            });

            imageProxy.close();
        }
    }
}
