package com.faceRecogntion.FaceRecognition;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

// Import the class for static methods
import org.bytedeco.opencv.global.opencv_imgproc;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.putText;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.FONT_HERSHEY_PLAIN;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;

public class RecognitionGUI extends JFrame {
    
    private JLabel screen;
    private JButton btnLive;
    private JButton btnUpload;
    private JButton btnStop;
    
    private boolean isRunning = false;
    private OpenCVFrameGrabber grabber;
    private FaceRecognizer recognizer;
    private HashMap<String, String> namesMap;

    public RecognitionGUI() {
        setTitle("Identify Criminal - Recognition");
        setLayout(new BorderLayout());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        screen = new JLabel("Click 'Start Live Camera' or 'Upload Image'", JLabel.CENTER);
        add(screen, BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout());
        btnLive = new JButton("Start Live Camera");
        btnUpload = new JButton("Upload Image to Check");
        btnStop = new JButton("Stop");
        
        panel.add(btnLive);
        panel.add(btnUpload);
        panel.add(btnStop);
        add(panel, BorderLayout.SOUTH);

        loadModelAndData();

        btnLive.addActionListener(e -> startLiveCamera());
        btnUpload.addActionListener(e -> uploadAndCheck());
        btnStop.addActionListener(e -> stopCamera());
    }

    private void loadModelAndData() {
        try {
            recognizer = LBPHFaceRecognizer.create();
            recognizer.read(Config.BASE_PATH + "\\src\\main\\resources\\classifierLBPH.yml");
            recognizer.setThreshold(80.0);

            namesMap = new HashMap<>();
            Path path = Paths.get(Config.BASE_PATH + "\\src\\main\\resources\\namedata.csv");
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) namesMap.put(parts[0], parts[1]);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading model. Did you Train first?");
        }
    }

    private void startLiveCamera() {
        if (isRunning) return;
        new Thread(() -> {
            try {
                grabber = new OpenCVFrameGrabber(Config.CAMERA_ID);
                grabber.setImageWidth(Config.FRAME_WIDTH);
                grabber.setImageHeight(Config.FRAME_HEIGHT);
                grabber.start();
                isRunning = true;

                Java2DFrameConverter paintConverter = new Java2DFrameConverter();
                org.bytedeco.javacv.OpenCVFrameConverter.ToMat cvt = new org.bytedeco.javacv.OpenCVFrameConverter.ToMat();
                CascadeClassifier detector = new CascadeClassifier(Config.HAAR_CASCADE_PATH);

                while (isRunning) {
                    Frame frame = grabber.grab();
                    if (frame == null) continue;

                    Mat mat = cvt.convert(frame);
                    processAndDraw(mat, detector);

                    BufferedImage img = paintConverter.convert(cvt.convert(mat));
                    final ImageIcon icon = new ImageIcon(img);
                    SwingUtilities.invokeLater(() -> screen.setIcon(icon));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void uploadAndCheck() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            
            Mat mat = imread(file.getAbsolutePath());
            if (mat.empty()) {
                JOptionPane.showMessageDialog(this, "Could not read image!");
                return;
            }

            CascadeClassifier detector = new CascadeClassifier(Config.HAAR_CASCADE_PATH);
            processAndDraw(mat, detector);
            
            org.bytedeco.javacv.OpenCVFrameConverter.ToMat cvt = new org.bytedeco.javacv.OpenCVFrameConverter.ToMat();
            Java2DFrameConverter paintConverter = new Java2DFrameConverter();
            BufferedImage img = paintConverter.convert(cvt.convert(mat));
            
            JLabel resultLabel = new JLabel(new ImageIcon(img));
            JOptionPane.showMessageDialog(this, resultLabel, "Identification Result", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void processAndDraw(Mat colorImg, CascadeClassifier detector) {
        Mat gray = new Mat();
        cvtColor(colorImg, gray, COLOR_BGRA2GRAY);
        
        RectVector faces = new RectVector();
        detector.detectMultiScale(gray, faces, 1.1, 1, 0, new org.bytedeco.opencv.opencv_core.Size(150, 150), new org.bytedeco.opencv.opencv_core.Size(500, 500));

        for (int i = 0; i < faces.size(); i++) {
            Rect face = faces.get(i);
            rectangle(colorImg, face, new Scalar(0, 0, 255, 0));

            Mat capturedFace = new Mat(gray, face);
            
            // FIX: Use opencv_imgproc.resize to avoid conflict with JFrame.resize
            opencv_imgproc.resize(capturedFace, capturedFace, new org.bytedeco.opencv.opencv_core.Size(160, 160));

            IntPointer label = new IntPointer(1);
            DoublePointer confidence = new DoublePointer(1);
            recognizer.predict(capturedFace, label, confidence);

            int prediction = label.get(0);
            String name = "Unknown";
            
            if (prediction != -1 && namesMap.containsKey(String.valueOf(prediction))) {
                name = namesMap.get(String.valueOf(prediction));
            }
            
            int x = Math.max(face.tl().x() - 10, 0);
            int y = Math.max(face.tl().y() - 10, 0);
            putText(colorImg, name, new Point(x, y), FONT_HERSHEY_PLAIN, 2.0, new Scalar(0, 255, 0, 0));
        }
    }

    private void stopCamera() {
        isRunning = false;
        try {
            if (grabber != null) {
                grabber.stop();
                grabber.close();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}