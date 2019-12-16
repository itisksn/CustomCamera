package com.assignment.customcamerasample.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.SparseIntArray;
import android.view.Surface;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.assignment.customcamerasample.R;

import java.io.File;
import java.io.FileOutputStream;

public class Utils {

    public static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public static void createDirectoryAndSaveFile(Context context, Bitmap imageToSave, String fileName) {

        String path = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS;

        File direct = new File(path);

        if (!direct.exists()) {
            File wallpaperDirectory = new File(path);
            wallpaperDirectory.mkdirs();
        }

        File file = new File(path, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                            FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file) : Uri.fromFile(new File(path)),
                    "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            showNotification(context, context.getString(R.string.app_name), "" + context.getString(R.string.image_save_successfully), pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void showNotification(Context context, String title, String body, final PendingIntent intent) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int notificationId = 1;
            String channelId = "channel-01";
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body);
            mBuilder.setContentIntent(intent);
            notificationManager.notify(notificationId, mBuilder.build());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));
            } else {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Check if this device has a camera
     */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public static void fileDelete(String filePath) {
        File files = new File(filePath);
        if (files.exists())
            files.delete();
    }

    public static Bitmap rotateImageAngle(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Bitmap rotateImage(Bitmap bitmap, String photoPath) {
        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImageAngle(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImageAngle(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImageAngle(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    bitmap = bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
