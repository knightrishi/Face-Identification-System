package com.faceRecogntion.FaceRecognition;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;
import javax.swing.JOptionPane;

// --- NEW IMPORTS (Standardized) ---
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;

// Static imports for Constants and Functions
import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

public class Training {

    public static void main(String[] args) {
        Training t = new Training();
        t.training();
    }

    public void training() {
        // 1. Use Dynamic Path from Config
        File directory = new File(Config.PHOTOS_DIR);
        
        // Ensure directory exists
        if (!directory.exists()) {
            JOptionPane.showMessageDialog(null, "Photos directory not found: " + Config.PHOTOS_DIR);
            return;
        }

        FilenameFilter imageFilter = new FilenameFilter() {   // filter image type
            public boolean accept(File dir, String name) {
                return name.endsWith(".jpg") || name.endsWith(".gif") || name.endsWith(".png");
            }
        }; 

        File[] files = directory.listFiles(imageFilter);
        
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(null, "No photos found to train! Please capture faces first.");
            return;
        }

        MatVector photos = new MatVector(files.length);
        Mat labels = new Mat(files.length, 1, CV_32SC1);
        IntBuffer bufferLabels = labels.createBuffer();
        int counter = 0;

        for (File image : files) {
            // 2. Read Image (GrayScale)
            Mat photo = imread(image.getAbsolutePath(), IMREAD_GRAYSCALE);
            
            try {
                // Parse ID from filename: name.id.sample.jpg (e.g., Arnal.1.1.jpg)
                // split(".")[1] gets the ID
                int personId = Integer.parseInt(image.getName().split("\\.")[1]);
                
                resize(photo, photo, new Size(160, 160));
                photos.put(counter, photo);
                bufferLabels.put(counter, personId);
                counter++;
            } catch (Exception e) {
                System.out.println("Skipping malformed filename: " + image.getName());
            }
        }

        // 3. Create Recognizer (New Syntax)
        FaceRecognizer lbph = LBPHFaceRecognizer.create();

        // 4. Train
        lbph.train(photos, labels);
        
        // 5. Save Model Dynamically
        // Saves to: your_project/src/main/resources/classifierLBPH.yml
        String savePath = Config.BASE_PATH + "\\src\\main\\resources\\classifierLBPH.yml";
        lbph.save(savePath);
        
        JOptionPane.showMessageDialog(null, "Training Faces Done! Model Saved.", "FACE RECOGNITION", JOptionPane.INFORMATION_MESSAGE);
    }
}