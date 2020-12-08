package com.example.cbest.jarredcolinfinalproject
//Author: Colin Best and Jarred Brown
//Date: December 7th, 2020
//Purpose: To Scan an artist's name, and find them on spotify
// Mini Citation: https://codelabs.developers.google.com/codelabs/camerax-getting-started#0
// This helped a lot with getting the camera working
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewFinder: PreviewView
    private lateinit var filepath: Uri
    private val mAccessToken: String = "BQDa2nbH3626ONTFFpNg9cPULCBrK5az-nOem1tT0eAqyGehx07tybbjN23eihgBZSxuQv5hot7sDWPO7PSQlLBohvPpq15yUah7mQktoyG19pJR1zkqAmQaht51L1agViI-StDo4lhsr7lwAI3zx9Oq3uOA458"
    val recognizer = TextRecognition.getClient()

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
            // setup the textView to display text found
            var textView: TextView;
            viewFinder = findViewById<PreviewView>(R.id.viewFinder);
            // the on-device model for text recognition
        outputDirectory = getOutputDirectory()
            val recognizer = TextRecognition.getClient()
        cameraExecutor = Executors.newSingleThreadExecutor()
        } // OnCreate

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private class ImageAnalyzer : ImageAnalysis.Analyzer {
        @SuppressLint("UnsafeExperimentalUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                proxImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            }
            //imageProxy.close();
        }
    }
    private fun takePhoto() { // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
                outputDirectory,
                SimpleDateFormat(FILENAME_FORMAT, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg")
        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                filepath = Uri.fromFile(photoFile)
                val savedUri = Uri.fromFile(photoFile)
                val msg = "Photo capture succeeded: $savedUri"
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                Log.d(TAG, msg)
            }
        })
    } // TakePhoto

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                    }

            imageCapture = ImageCapture.Builder()
                    .build()


            val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, ImageAnalyzer())
                    }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, imageAnalyzer, preview, imageCapture)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults:
            IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnScan -> {
                takePhoto()
            } // Scan Text from Camera
            R.id.btnFind -> {
                val image: InputImage
                try {
                    image = InputImage.fromFilePath(this, filepath)

                    // YourImageAnalyzer().analyze(proxImage)
                    Log.d(TAG, "You pressed button")
                    if (!proxImage.equals(null)) {
                        val result = recognizer.process(image)
                                .addOnSuccessListener { visionText ->
                                    Log.d(TAG, "We GOT TEXT : " + visionText.text)
                                    for (block in visionText.textBlocks) {
                                        val blockText = block.text
                                        val blockCornerPoints = block.cornerPoints
                                        val blockFrame = block.boundingBox

                                        Log.e(TAG, "BIG NERD" + block.text)
                                        /*for(line in block.lines) {
                                    val lineText = line.text
                                    val lineCornerPoints = line.cornerPoints
                                    val lineFrame = line.boundingBox

                                    for (element in line.elements) {

                                        val elementText = element.text
                                        val elementCornerPoints = element.cornerPoints
                                        val elementFrame = element.boundingBox
                                }
                            }*/
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, e.toString() + " ML KIT FAILURE LOSER")
                                }
                    } else {
                        Log.d(TAG, "Prox Image was null")
                    }
                } // Find Artist based on Scanned text/image whatever
                catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    companion object {
        private lateinit var proxImage: InputImage
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
} // End Main
