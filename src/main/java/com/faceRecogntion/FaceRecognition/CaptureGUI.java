package com.faceRecogntion.FaceRecognition;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_core.Scalar;

// Import specific OpenCV methods to avoid conflicts
import org.bytedeco.opencv.global.opencv_imgproc;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

public class CaptureGUI extends JFrame {

    private JLabel cameraScreen;
    private JButton btnStart;
    private JButton btnCapture;
    private JButton btnStop;

    private OpenCVFrameGrabber grabber;
    private boolean isRunning = false;
    private boolean saveRequest = false;

    private String personName;
    private String personId;
    private int sampleCount = 0;
    private final int MAX_SAMPLES = 30;

    public CaptureGUI() {
        setTitle("Capture Face - Criminal Database");
        setLayout(new BorderLayout());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 1. Camera Area (Center)
        cameraScreen = new JLabel("Camera is OFF. Click 'Start Camera'.", JLabel.CENTER);
        cameraScreen.setFont(new Font("Arial", Font.BOLD, 18));
        cameraScreen.setPreferredSize(new Dimension(640, 480)); // FIX: Give it a size!
        cameraScreen.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(cameraScreen, BorderLayout.CENTER);

        // 2. Control Panel (Bottom)
        JPanel buttonPanel = new JPanel(new FlowLayout());

        btnStart = new JButton("1. Start Camera");
        btnCapture = new JButton("2. Capture Photos");
        btnStop = new JButton("3. Stop & Train");

        btnCapture.setEnabled(false); // Disable until camera starts
        btnStop.setEnabled(false);

        buttonPanel.add(btnStart);
        buttonPanel.add(btnCapture);
        buttonPanel.add(btnStop);
        add(buttonPanel, BorderLayout.SOUTH);

        // 3. Actions
        btnStart.addActionListener(e -> startCamera());

        btnCapture.addActionListener(e -> {
            if (personName == null || personId == null) {
                personName = JOptionPane.showInputDialog("Enter Criminal Name:");
                personId = JOptionPane.showInputDialog("Enter ID (Number):");

                if (personName != null && personId != null) {
                    saveToCSV(personId, personName);
                    saveRequest = true; // Start saving frames
                    btnCapture.setEnabled(false); // Don't click again
                }
            }
        });

        btnStop.addActionListener(e -> stopCameraAndTrain());
    }

    public void startCamera() {
        btnStart.setEnabled(false);
        cameraScreen.setText("Starting Camera...");

        new Thread(() -> {
            try {
                grabber = new OpenCVFrameGrabber(Config.CAMERA_ID); // Ensure Config.CAMERA_ID is 0
                grabber.setImageWidth(640);
                grabber.setImageHeight(480);
                grabber.start();

                isRunning = true;
                SwingUtilities.invokeLater(() -> {
                    btnCapture.setEnabled(true);
                    btnStop.setEnabled(true);
                    cameraScreen.setText(""); // Remove text
                });

                Java2DFrameConverter paintConverter = new Java2DFrameConverter();
                org.bytedeco.javacv.OpenCVFrameConverter.ToMat cvt = new org.bytedeco.javacv.OpenCVFrameConverter.ToMat();

                CascadeClassifier detector = new CascadeClassifier(Config.HAAR_CASCADE_PATH);

                Mat frameMat;
                Mat grayMat = new Mat();

                while (isRunning) {
                    Frame frame = grabber.grab();
                    if (frame == null) {
                        continue;
                    }

                    frameMat = cvt.convert(frame);
                    cvtColor(frameMat, grayMat, COLOR_BGRA2GRAY);

                    RectVector faces = new RectVector();
                    // Detect Face
                    detector.detectMultiScale(grayMat, faces, 1.1, 1, 0, new org.bytedeco.opencv.opencv_core.Size(150, 150), new org.bytedeco.opencv.opencv_core.Size(500, 500));

                    for (int i = 0; i < faces.size(); i++) {
                        Rect face = faces.get(i);
                        rectangle(frameMat, face, new Scalar(0, 255, 0, 0)); // Green Box

                        // Save Logic
                        if (saveRequest && sampleCount < MAX_SAMPLES) {
                            Mat capturedFace = new Mat(grayMat, face);

                            // RESIZE using opencv_imgproc
                            opencv_imgproc.resize(capturedFace, capturedFace, new org.bytedeco.opencv.opencv_core.Size(160, 160));

                            sampleCount++;
                            String filename = Config.PHOTOS_DIR + personName + "." + personId + "." + sampleCount + ".jpg";
                            imwrite(filename, capturedFace);
                            System.out.println("Saved photo: " + sampleCount);
                        }
                    }

                    // Draw to Screen
                    BufferedImage img = paintConverter.convert(cvt.convert(frameMat));
                    final ImageIcon icon = new ImageIcon(img);
                    SwingUtilities.invokeLater(() -> cameraScreen.setIcon(icon));

                    // Check if done saving
                    if (saveRequest && sampleCount >= MAX_SAMPLES) {
                        saveRequest = false;
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "30 Photos Captured! Click 'Stop & Train' now."));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Camera Error: " + e.getMessage()));
            }
        }).start();
    }

    public void stopCameraAndTrain() {
        isRunning = false;
        try {
            if (grabber != null) {
                grabber.stop();
                grabber.close();
            }
            dispose(); // Close Window

            // Only train if we actually took photos
            if (sampleCount > 0) {
                Training t = new Training();
                t.training();
            } else {
                System.out.println("No photos taken, skipping training.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToCSV(String id, String name) {
        try {
            Config.setupDirectories();
            FileWriter writer = new FileWriter(Config.BASE_PATH + "\\src\\main\\resources\\namedata.csv", true);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.append(id + "," + name);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
