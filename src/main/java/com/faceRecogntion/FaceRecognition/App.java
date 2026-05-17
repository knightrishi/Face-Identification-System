package com.faceRecogntion.FaceRecognition;

public class App extends javax.swing.JFrame {

    public App() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // ... (Keep your existing initComponents code largely the same) ...
        // OR Just replace the ActionPerformed methods below:
        
        // RE-GENERATE or COPY your existing Layout Code here if replacing file
        // For simplicity, just update the methods below in your existing App.java
        
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        CaptureButton = new javax.swing.JButton();
        RecognizeButton = new javax.swing.JButton();
        TrainButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        // ... (Layout code hidden for brevity, use your existing layout) ...
        // ...
        
        CaptureButton.setText("Capture & Train Faces");
        CaptureButton.addActionListener(evt -> CaptureButtonActionPerformed(evt));

        RecognizeButton.setText("Recognize Faces");
        RecognizeButton.addActionListener(evt -> RecognizeButtonActionPerformed(evt));

        TrainButton.setText("Force Retrain");
        TrainButton.addActionListener(evt -> TrainButtonActionPerformed(evt));
        
        // Layout Config (Simple version)
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addGap(50).addComponent(CaptureButton).addGap(20).addComponent(RecognizeButton).addGap(20).addComponent(TrainButton)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addGap(50).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(CaptureButton).addComponent(RecognizeButton).addComponent(TrainButton))));
        pack();
    }

    // --- UPDATED BUTTON ACTIONS ---

    private void CaptureButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Open the NEW GUI
        java.awt.EventQueue.invokeLater(() -> {
            new CaptureGUI().setVisible(true);
        });
    }

    private void RecognizeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Open the NEW GUI
        java.awt.EventQueue.invokeLater(() -> {
            new RecognitionGUI().setVisible(true);
        });
    }

    private void TrainButtonActionPerformed(java.awt.event.ActionEvent evt) {
        new Thread(() -> {
            Training t = new Training();
            t.training();
        }).start();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new App().setVisible(true));
    }
    
    // Variables
    private javax.swing.JButton CaptureButton;
    private javax.swing.JButton RecognizeButton;
    private javax.swing.JButton TrainButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
}