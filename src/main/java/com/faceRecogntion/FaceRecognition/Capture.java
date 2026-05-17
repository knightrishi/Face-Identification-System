package com.faceRecogntion.FaceRecognition;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

// --- IMPORTS ---
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

// Static imports
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

public class Capture {

    public void capture() throws Exception, InterruptedException {
        System.out.println("--- STARTING CAPTURE ---");

        // 1. User Input (Before opening camera to avoid freezing)
        String name = JOptionPane.showInputDialog("Enter Face Name");
        if (name == null || name.isEmpty()) return; // Cancel if empty
        
        String personId = JOptionPane.showInputDialog("Enter Face ID (Number)");
        if (personId == null || personId.isEmpty()) return;

        // 2. Setup Camera
        // FIX: Force specific resolution to prevent "tiny window"
        OpenCVFrameGrabber camera1 = new OpenCVFrameGrabber(Config.CAMERA_ID); 
        camera1.setImageWidth(Config.FRAME_WIDTH);  // 640
        camera1.setImageHeight(Config.FRAME_HEIGHT); // 480
        camera1.start();
        
        System.out.println("Camera Started!");

        // 3. Setup Window
        // FIX: Use 1.0 gamma (simple) and set size immediately
        CanvasFrame cFrame = new CanvasFrame("Capture - Face Recognition", 1.0);
        cFrame.setCanvasSize(Config.FRAME_WIDTH, Config.FRAME_HEIGHT);
        cFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);

        // 4. Setup Face Detector
        OpenCVFrameConverter.ToMat convertMat = new OpenCVFrameConverter.ToMat();
        CascadeClassifier faceDetector = new CascadeClassifier(Config.HAAR_CASCADE_PATH);

        Frame capturedFrame = null;
        Mat colorImage = new Mat();
        Mat gray = new Mat();
        
        int sampleNumber = 30;
        int sample = 1;

        // 5. Update CSV
        try {
            Config.setupDirectories(); 
            FileWriter writer = new FileWriter(Config.BASE_PATH + "\\src\\main\\resources\\namedata.csv", true);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.append(personId + "," + name);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Starting Loop...");

        // 6. Main Capture Loop
        while ((capturedFrame = camera1.grab()) != null) {
            
            // Check if window is closed
            if (!cFrame.isVisible()) {
                break;
            }

            // Show video immediately so user sees something
            cFrame.showImage(capturedFrame);

            // Convert and Detect
            colorImage = convertMat.convert(capturedFrame);
            cvtColor(colorImage, gray, COLOR_BGRA2GRAY);
            
            RectVector detectedFaces = new RectVector();
            faceDetector.detectMultiScale(gray, detectedFaces, 1.1, 1, 0, new Size(150, 150), new Size(500, 500));

            // Save Face if found
            if (detectedFaces.size() > 0 && sample <= sampleNumber) {
                Rect faceData = detectedFaces.get(0);
                
                // Crop & Resize
                Mat capturedFace = new Mat(gray, faceData);
                resize(capturedFace, capturedFace, new Size(160, 160));
                
                // Save
                String filename = Config.PHOTOS_DIR + name + "." + personId + "." + sample + ".jpg";
                imwrite(filename, capturedFace);
                
                System.out.println("Saved Photo: " + sample + "/" + sampleNumber);
                sample++;
            }

            if (sample > sampleNumber) {
                System.out.println("Capture Complete.");
                JOptionPane.showMessageDialog(null, "Face Captured Successfully! Training will start now.");
                break;
            }
        }

        // 7. Cleanup
        cFrame.dispose();
        camera1.stop();
        camera1.close();
        faceDetector.close();
        System.out.println("Camera Closed.");
    }
}