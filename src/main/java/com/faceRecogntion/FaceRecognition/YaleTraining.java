package com.faceRecogntion.FaceRecognition;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;
import javax.swing.JOptionPane;

// --- NEW IMPORTS ---
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;

// Correct Static Imports
import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE; 
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;

public class YaleTraining {

    public void yaleTraining() {
        
        // Use Config path
        File directory = new File(Config.YALE_TRAINING_PATH);
        
        FilenameFilter imageFilter = (dir, name) -> name.endsWith(".jpg") || name.endsWith(".gif") || name.endsWith(".png");
        
        File[] files = directory.listFiles(imageFilter);
        
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(null, "No training files found in: " + directory.getAbsolutePath());
            return;
        }

        MatVector photos = new MatVector(files.length);
        Mat labels = new Mat(files.length, 1, CV_32SC1);
        IntBuffer bufferLabels = labels.createBuffer();
        int counter = 0;
      
        for (File image : files) {
            // Use IMREAD_GRAYSCALE instead of the old constant
            Mat photo = imread(image.getAbsolutePath(), IMREAD_GRAYSCALE);
            
            try {
                // Ensure filename format is correct (e.g., "subject01.jpg") to parse ID
                // Adjust this substring logic if your files are named differently!
                int personId = Integer.parseInt(image.getName().split("\\.")[1]); 
                
                resize(photo, photo, new Size(160, 160));
                photos.put(counter, photo);
                bufferLabels.put(counter, personId);
                counter++;
            } catch (Exception e) {
                System.out.println("Skipping file (bad format): " + image.getName());
            }
        }
        
        FaceRecognizer lbph = LBPHFaceRecognizer.create();
        lbph.train(photos, labels);
        lbph.save(Config.YALE_CLASSIFIER_PATH);
        
        JOptionPane.showMessageDialog(null, "Training Complete! Model Saved.");
    }
}