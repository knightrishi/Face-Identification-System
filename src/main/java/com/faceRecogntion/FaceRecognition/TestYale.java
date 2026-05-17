package com.faceRecogntion.FaceRecognition;

import java.io.File;

// Standard JavaCV/OpenCV Imports
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer; // IMPORT THE CLASS DIRECTLY

// Static imports for image processing
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

public class TestYale {
    public static void main(String[] args) {
        int totalHits = 0;
        double totalConfidence = 0;
        
        // FIX: Use the class method .create() instead of the global static function
        FaceRecognizer recognizer = LBPHFaceRecognizer.create();
        
        recognizer.read(Config.YALE_CLASSIFIER_PATH);

        File directory = new File(Config.YALE_TRAINING_PATH);
        File[] files = directory.listFiles();
        
        if (files == null) {
            System.out.println("No files found in " + Config.YALE_TRAINING_PATH);
            return;
        }

        for (File image : files) {            
            Mat photo = imread(image.getAbsolutePath(), IMREAD_GRAYSCALE);
            
            try {
                // Parsing logic: Assumes filename format allows this substring to be an Integer
                // If your files are like "subject01.gif", substring(5,10) might fail if name is short.
                // Using a safe try-catch block here.
                int personId = Integer.parseInt(image.getName().substring(7, 9)); // Adjusted to standard Yale format, tweak if needed          
                
                resize(photo, photo, new Size(160, 160));
    
                IntPointer label = new IntPointer(1);
                DoublePointer confidence = new DoublePointer(1);
                
                recognizer.predict(photo, label, confidence);
                
                int selection = label.get(0); 
                
                System.out.println("Actual: " + personId + " | Predicted: " + selection);
                
                if (personId == selection) {
                    totalHits++;
                    totalConfidence += confidence.get(0);
                }
            } catch (Exception e) {
                // Ignore files that don't match the naming convention
            }
        }
        
        System.out.println("Total hits: " + totalHits);
    }
}