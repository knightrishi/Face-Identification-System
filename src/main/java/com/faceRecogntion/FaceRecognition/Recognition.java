package com.faceRecogntion.FaceRecognition;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import static org.bytedeco.opencv.global.opencv_imgproc.putText;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.FONT_HERSHEY_PLAIN;

public class Recognition {
    
    static String getValue(HashMap<String, String> data, int selection) {
        return data.get(String.valueOf(selection));
    }

    public void recog() throws Exception, InterruptedException {
        System.out.println("--- STARTING RECOGNITION ---");

        // 1. Setup Camera
        OpenCVFrameGrabber camera1 = new OpenCVFrameGrabber(Config.CAMERA_ID);
        // FIX: Force Resolution
        camera1.setImageWidth(Config.FRAME_WIDTH);
        camera1.setImageHeight(Config.FRAME_HEIGHT);
        camera1.start();
        
        System.out.println("Camera Started!");

        // 2. Load Data
        HashMap<String, String> data = new HashMap<>();
        String csvPath = Config.BASE_PATH + "\\src\\main\\resources\\namedata.csv";
        
        try {
            Path path = Paths.get(csvPath);
            if (Files.exists(path)) {
                List<String> list = Files.readAllLines(path);
                for (String line : list) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        data.put(parts[0], parts[1]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 3. Setup Recognizer
        CascadeClassifier faceDetector = new CascadeClassifier(Config.HAAR_CASCADE_PATH);
        FaceRecognizer recognizer = LBPHFaceRecognizer.create();
        
        String classifierPath = Config.BASE_PATH + "\\src\\main\\resources\\classifierLBPH.yml";
        File modelFile = new File(classifierPath);
        
        if (modelFile.exists()) {
            recognizer.read(classifierPath);
            recognizer.setThreshold(80.0);
        } else {
            System.out.println("WARNING: No training model found at " + classifierPath);
        }

        // 4. Setup Window
        // FIX: Simple Gamma and Size
        CanvasFrame cFrame = new CanvasFrame("Recognition - Face Recognition", 1.0);
        cFrame.setCanvasSize(Config.FRAME_WIDTH, Config.FRAME_HEIGHT);
        cFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        
        Frame capturedFrame = null;
        Mat colorImage = new Mat();
        Mat grayImage = new Mat();
        OpenCVFrameConverter.ToMat convertMat = new OpenCVFrameConverter.ToMat();

        // 5. Main Loop
        while ((capturedFrame = camera1.grab()) != null) {
            
            if (!cFrame.isVisible()) break;

            colorImage = convertMat.convert(capturedFrame);
            cvtColor(colorImage, grayImage, COLOR_BGRA2GRAY);
            
            RectVector detectedFaces = new RectVector();
            faceDetector.detectMultiScale(grayImage, detectedFaces, 1.1, 1, 0, new Size(150, 150), new Size(500, 500));

            for (int i = 0; i < detectedFaces.size(); i++) {
                Rect faceData = detectedFaces.get(i);
                rectangle(colorImage, faceData, new Scalar(0, 0, 255, 0)); 

                Mat capturedFace = new Mat(grayImage, faceData);
                resize(capturedFace, capturedFace, new Size(160, 160));

                if (modelFile.exists()) {
                    IntPointer label = new IntPointer(1);
                    DoublePointer confidence = new DoublePointer(1);
                    recognizer.predict(capturedFace, label, confidence);
                    
                    int selection = label.get(0);
                    String name = "Unknown";
                    
                    if (selection != -1) {
                        String foundName = getValue(data, selection);
                        if (foundName != null) {
                            name = foundName; 
                        }
                    }
                    
                    int x = Math.max(faceData.tl().x() - 10, 0);
                    int y = Math.max(faceData.tl().y() - 10, 0);
                    putText(colorImage, name, new Point(x, y), FONT_HERSHEY_PLAIN, 1.4, new Scalar(0, 255, 0, 0));
                }
            }

            cFrame.showImage(convertMat.convert(colorImage));
        }
        
        cFrame.dispose();
        camera1.stop();
        camera1.close();
        faceDetector.close();
    }
}