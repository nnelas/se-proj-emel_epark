package pt.se_ulusofona.se_proj_emel_epark.controller;

/**
 * Created by jorgeloureiro on 21/06/17.
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

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import pt.se_ulusofona.se_proj_emel_epark.utils.GraphicOverlay;

/**
 * A very simple Processor which receives detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */

public class OcrDetectorProcessor extends FragmentActivity implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    private static final String TAG = "OcrCaptureActivity";

    private String matricula;


    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {

        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); i++) {
            try {
                int numbers = 0;
                int letters = 0;

                TextBlock item = items.valueAt(i);
                /****************************************************************/
                StringBuilder textSB = new StringBuilder(item.getValue());
                if(textSB.length() == 8 || textSB.length() == 9) {
                    if(textSB.length() == 8) {
                        textSB.deleteCharAt(2); // replace with " "
                        textSB.deleteCharAt(4); // replace with " "
                        textSB.toString().replaceAll("\\W", ""); // remove char indesejados
                    } else if (textSB.length() == 9) {
                        if (textSB.charAt(0) == 'P'){   // P de Portugal, zona Azul
                            textSB.deleteCharAt(0); // replace with " "
                            textSB.deleteCharAt(2); // replace with " "
                            textSB.deleteCharAt(4); // replace with " "
                            textSB.toString().replaceAll("\\W", ""); // remove char indesejados
                        }
                    }
                    if(textSB.length() == 6) {
                        // Conta num, uma matricula obrigatoriamente tem 4 digitos e duas letras
                        for (int x = 0; x < textSB.length(); x++) {
                            if (Character.isDigit(textSB.charAt(x))) {
                                numbers++;
                            } else if (Character.isLetter(textSB.charAt(x))) {
                                letters++;
                            }
                        }
                        /****************************************************************/
                        // Matricula tem de ter 4 digitos e o tamanho da string 8 (porque input tem " " || "." || "-" || ";")
                        if (numbers == 4 && letters == 2 && textSB.length() == 6 &&
                                (Character.isLetter(textSB.charAt(0)) && Character.isLetter(textSB.charAt(1)) ||
                                        Character.isLetter(textSB.charAt(2)) && Character.isLetter(textSB.charAt(3)) ||
                                        Character.isLetter(textSB.charAt(4)) && Character.isLetter(textSB.charAt(5)))) {
                            //apenas entra formato valido AA-00-00 || 00-AA-00 || 00-00-AA
                            Log.d(TAG, "Matricula -> "+textSB.toString());
                            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                            mGraphicOverlay.add(graphic);
                            matricula = (new StringBuilder().append(textSB.charAt(0)).append(textSB.charAt(1)).
                                    append("-").append(textSB.charAt(2)).append(textSB.charAt(3)).append("-").
                                    append(textSB.charAt(4)).append(textSB.charAt(5))).toString();
                            /**/
                            // Check na Base Dados
                        }
                    }
                }
            } catch (StringIndexOutOfBoundsException e){
                Log.d(TAG, "Formato invalido.");
            }
        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }

    public String getMatricula(){
        return matricula;
    }

}
