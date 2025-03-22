package com.akila.treasureplant.model;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class DataClearer {

    private static final String TAG = "DataClearer";

    public static boolean clearAppData(Context context) {
        try {
            // Option 1: Using PackageManager (more thorough)
            String packageName = context.getPackageName();
            Process process = Runtime.getRuntime().exec("pm clear " + packageName);
            int result = process.waitFor();

            if (result == 0) {
                Log.i(TAG, "App data cleared successfully using PackageManager.");
                return true;
            } else {
                String errorStream = new java.util.Scanner(process.getErrorStream()).useDelimiter("\\A").next();
                Log.e(TAG, "Error clearing app data using PackageManager: " + errorStream);
            }


            // Option 2:  Deleting files directly (less reliable, requires storage permission)
            //  Use with extreme caution! Incorrect paths can damage the system.
            //  This method is generally NOT recommended.

            //  Caution: This requires WRITE_EXTERNAL_STORAGE permission (and may not work on newer Android versions).
            //  It's also very risky if you don't use the correct paths.  Don't use this unless you absolutely
            //  understand the implications.  The PackageManager method is preferred.

            /*
            File dataDir = new File(context.getApplicationInfo().dataDir);
            if (dataDir.exists() && dataDir.isDirectory()) {
                deleteDir(dataDir);
                Log.i(TAG, "App data cleared successfully by deleting files (use with care!).");
                return true;
            } else {
                Log.e(TAG, "Data directory not found or is not a directory.");
            }
            */

        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Error clearing app data: " + e.getMessage());
        }
        return false;
    }


    // Recursive function to delete directories and files (for the risky file deletion method only)
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {  // important check, dir.list() can return null
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }
}
