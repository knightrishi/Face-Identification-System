/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.faceRecogntion.FaceRecognition;

import java.io.File;

/**
 * Global Configuration constants for the Face Recognition Project.
 * @author Arnav Singh
 */
public class Config {

    // ==========================================
    // File Paths & Resources
    // ==========================================
    // Gets the project root directory dynamically
    public static final String BASE_PATH = System.getProperty("user.dir");
    
    // FIX: Point to src/main/resources where Maven keeps files
    public static final String HAAR_CASCADE_PATH = BASE_PATH + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "haarcascade_frontalface_alt.xml";
    
    public static final String YALE_TRAINING_PATH = BASE_PATH + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "faces" + File.separator + "training";
    
    public static final String YALE_CLASSIFIER_PATH = BASE_PATH + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "classifierLBPHYale.yml";
    
    // Directory where user face images will be saved (Create this folder manually if it crashes!)
    public static final String PHOTOS_DIR = BASE_PATH + File.separator + "captured_photos" + File.separator;

    // ==========================================
    // Camera / Webcam Settings
    // ==========================================
    public static final int CAMERA_ID = 0; // Try 1 if 0 gives a black screen
    public static final int FRAME_WIDTH = 640;
    public static final int FRAME_HEIGHT = 480;
    
    // ==========================================
    // Detection Parameters
    // ==========================================
    public static final double CONFIDENCE_THRESHOLD = 0.6;
    public static final int MIN_FACE_SIZE = 100;

    // ==========================================
    // Database Configuration
    // ==========================================
    public static final String DB_HOST = "localhost";
    public static final String DB_PORT = "3306";
    public static final String DB_NAME = "face_recognition_db";
    public static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = ""; 

    // ==========================================
    // Utility Methods
    // ==========================================
    public static void setupDirectories() {
        File photosDir = new File(PHOTOS_DIR);
        if (!photosDir.exists()) {
            boolean created = photosDir.mkdirs();
            if(created) System.out.println("Created directory: " + PHOTOS_DIR);
        }
    }
}