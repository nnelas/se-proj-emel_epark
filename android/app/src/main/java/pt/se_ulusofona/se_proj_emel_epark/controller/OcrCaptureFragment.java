package pt.se_ulusofona.se_proj_emel_epark.controller;

/**
 * Created by nunonelas on 05/06/17.
 */

/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pt.se_ulusofona.se_proj_emel_epark.R;
import pt.se_ulusofona.se_proj_emel_epark.model.Carro;
import pt.se_ulusofona.se_proj_emel_epark.model.Registo;
import pt.se_ulusofona.se_proj_emel_epark.utils.CameraSource;
import pt.se_ulusofona.se_proj_emel_epark.utils.CameraSourcePreview;
import pt.se_ulusofona.se_proj_emel_epark.utils.GraphicOverlay;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Activity for the multi-tracker app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */

public final class OcrCaptureFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "OcrCaptureActivity";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private OcrDetectorProcessor ocrDetectorProcessor;

    public OcrCaptureFragment() {
    }

    private String matricula = null;
    private String matriculaCheck = null;

    private int a;

    private Carro carro;
    private Registo registo;

    private List<Registo> registos;

    private DatabaseReference mDatabaseCarros;
    private DatabaseReference mDatabaseRegistos;

    private String lastRua = "";
    private String lastZona = "";
    private String lastData = "";

    private String lastRua2 = "";
    private String lastData2 = "";

    private String lastRua3 = "";
    private String lastData3 = "";

    private String messageRegisto;
    private String messageInfo;

    private boolean existeMatriculaBD;
    private boolean matriculaChecked = false;
    private boolean aDetectar;


    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        mPreview = (CameraSourcePreview) view.findViewById(R.id.preview);
        mGraphicOverlay = view.findViewById(R.id.graphicOverlay);

        mDatabaseCarros = FirebaseDatabase.getInstance().getReference("carros");
        mDatabaseRegistos = FirebaseDatabase.getInstance().getReference("registos");

        // read parameters from the intent used to launch the activity.
        boolean autoFocus = true;
        boolean useFlash = false;

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        // getLocation on start
        ((MainActivity) getActivity()).fetchAddressButtonHandler(view);

        ImageButton btn_faq = (ImageButton) view.findViewById(R.id.btn_faq);
        btn_faq.setOnClickListener(this);

        aDetectar = true;
        runThread();

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_faq:
                Intent intent = new Intent(getActivity(), FAQActivity.class);
                startActivity(intent);
                break;

        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(getActivity(), permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     * <p>
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getContext();

        // A text recognizer is created to find text.  An associated processor instance
        // is set to receive the text recognition results and display graphics for each text block
        // on screen.
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        ocrDetectorProcessor = new OcrDetectorProcessor(mGraphicOverlay);

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource =
                new CameraSource.Builder(getContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(2.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .build();

        textRecognizer.setProcessor(ocrDetectorProcessor);
    }

    /**
     * Restarts the camera.
     */
    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
            aDetectar = false;
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            boolean autoFocus = true;
            boolean useFlash = false;
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    public void checkMatriculaFirebase() {
        mDatabaseCarros.child(matricula).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                carro = dataSnapshot.getValue(Carro.class);
                checkCarroIsNull(carro);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void checkCarroIsNull(Carro carro) {
        if (carro == null) {
            Toast.makeText(getActivity(), "A matrícula " + matricula + " não foi encontrada na base de dados!",
                    Toast.LENGTH_SHORT).show();
            existeMatriculaBD = false;
        } else {
            existeMatriculaBD = true;
        }
    }

    public void checkRegistoFirebase() {
        mDatabaseRegistos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                registos = new ArrayList<>();

                for (DataSnapshot registosSnapshot : dataSnapshot.getChildren()) {
                    registo = registosSnapshot.getValue(Registo.class);
                    if (registo.getMatricula().equals(matricula)) {
                        checkRegistoIsNull(registo);
                    }
                }
                appendListToVar();


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void checkRegistoIsNull(Registo registo) {
        if (registo == null) {
            Toast.makeText(getActivity(), "Não existe registo referente à matrícula " + matricula + " na base de dados!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        registos.add(new Registo(registo.getMatricula(), registo.getData(), registo.getRua(), registo.getZona()));
    }

    public void appendListToVar() {
        Iterator itr = registos.iterator();
        a = 0;

        while (itr.hasNext()) {
            a++;
            Registo st = (Registo) itr.next();
            System.out.println(st.getRua() + " " + st.getZona() + " " + st.getData());

            if (a == 1) {
                lastData = st.getData();
                lastRua = st.getRua();
                lastZona = st.getZona();
            } else if (a == 3) {
                lastData2 = st.getData();
                lastRua2 = st.getRua();
            } else if (a == 5) {
                lastData3 = st.getData();
                lastRua3 = st.getRua();
                break;
            }

            a++;
        }

        setMessageToAlert();
    }

    public void setMessageToAlert() {
        if (a == 0) {
            messageRegisto = "Não há registos nesta matrícula";
            messageInfo = "Não há registos nesta matrícula";
        } else if (a == 1) {
            messageRegisto = (lastRua + " , no dia " + lastData);
        } else if (a == 3) {
            messageRegisto = ((lastRua + " , no dia " + lastData) + "\n" +
                    (lastRua2 + " , no dia " + lastData2));
            messageInfo = ("Zona: " + lastZona + "\nRua: " + lastRua + "\nParque pago até: " + lastData);
        } else if (a == 5) {
            messageRegisto = ((lastRua + " , no dia " + lastData) + "\n" +
                    (lastRua2 + " , no dia " + lastData2) + "\n" +
                    (lastRua3 + " , no dia " + lastData3));
            messageInfo = ("Zona: " + lastZona + "\nRua: " + lastRua + "\nParque pago até: " + lastData);
        }

        if (existeMatriculaBD) {
            showInfoPay();
        }
    }

    public void showInfoPay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true)
                .setTitle(matricula)
                .setMessage(messageInfo)
                .setNeutralButton("+", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showRegisto();
                    }
                })
                .setNegativeButton(getActivity().getString(R.string.txt_coima), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Toast.makeText(getActivity(), "A imprimir ticket de coima para a matrícula: " + matricula,
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setPositiveButton(getActivity().getString(R.string.txt_aviso), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Toast.makeText(getActivity(), "A imprimir ticket de aviso para a matrícula: " + matricula,
                                Toast.LENGTH_LONG).show();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showRegisto() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true)
                .setTitle(getActivity().getString(R.string.txt_registo))
                .setMessage(messageRegisto)
                .setNeutralButton("+", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showInfoCar();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showInfoCar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true)
                .setTitle(getActivity().getString(R.string.txt_infoAdicional))
                .setMessage(getActivity().getString(R.string.txt_marca) + " " + carro.getMarca() + "\n" +
                        getActivity().getString(R.string.txt_modelo) + " " + carro.getModelo() + "\n" +
                        getActivity().getString(R.string.txt_tipo) + " " + carro.getTipo() + "\n" +
                        getActivity().getString(R.string.txt_data) + " " + carro.getDataRegistoAutomovel() + "\n" +
                        getActivity().getString(R.string.txt_cor) + " " + carro.getCor());

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void runThread() { // THREADS R LOVE THREADS R LIFE

        Thread thread = new Thread() {
            public void run() {
                while (aDetectar) {
                    try {
                        try {
                            matricula = null;
                            matricula = ocrDetectorProcessor.getMatricula();
                            if (!matricula.equals(matriculaCheck)) {    //caso a matricula seja diferente da anterior
                                matriculaChecked = false;               //ainda não foi visualizada
                            }
                            if (!matriculaChecked) {            //caso nao tenha sido visualizada
                                // e seja diferente de null
                                matriculaCheck = matricula;
                                Log.d(TAG, matricula);
                                checkMatriculaFirebase();
                                checkRegistoFirebase();
                                matriculaChecked = true;                //ja foi visualizada
                            }
                        } catch (NullPointerException e){
                            Log.d(TAG, "Attempt to invoke virtual method on a null object reference");
                        }
                        Thread.sleep(50);
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        };
        thread.start();
    }
}
