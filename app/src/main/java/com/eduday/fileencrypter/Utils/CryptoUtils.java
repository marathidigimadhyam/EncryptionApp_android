package com.eduday.fileencrypter.Utils;

import android.content.Context;
import android.os.Environment;

import androidx.documentfile.provider.DocumentFile;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by RIITLP on 1/26/2017.
 */

public class CryptoUtils {

    private static Crypto crypto;

    public static void decrypt(Context mContext,File inputFile, File outputFile) {
        doCrypto(inputFile, outputFile, mContext);
    }


    private static void doCrypto(File inputFile,
                                 File outputFile, Context mContext) {
        try {
            decryptRoutine(mContext,inputFile, outputFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    public static void encryptIntoInternalStorage(String key, File inputFile,
                                                  Context mContext, String mode) {


        try {

            String filePath = inputFile.getAbsolutePath();
            String[] splitStr = filePath.split("/");
            String fileExtension = splitStr[(splitStr.length - 1)].substring(splitStr[(splitStr.length - 1)].lastIndexOf(".") + 1, splitStr[(splitStr.length - 1)].length());
            String[] split1 = splitStr[(splitStr.length - 1)].split("." + fileExtension);
            String newFIleName = split1[0];

            // Creates a new Crypto object with default implementations of a key chain
            SharedPrefsBackedKeyChain keyChain = new SharedPrefsBackedKeyChain(mContext, CryptoConfig.KEY_256);
            crypto = AndroidConceal.get().createDefaultCrypto(keyChain);

            // Check for whether the crypto functionality is available
            // This might fail if Android does not load libaries correctly.
            if (!crypto.isAvailable()) {
                return;
            }

            FileInputStream inputStream = new FileInputStream(inputFile);

            File folder = new File(Environment.getExternalStorageDirectory().toString() + "/"+mode+"/");
            folder.mkdir();

            //CipherInputStream cinputStream = new CipherInputStream(inputStream,cipher);
            File outputFile = new File(folder.getAbsolutePath(), newFIleName + "." + fileExtension);
            OutputStream outputStream = new FileOutputStream(outputFile);

            outputStream = crypto.getCipherOutputStream(
                    outputStream,
                    Entity.create(key));
            try {
                int read;
                byte[] buffer = new byte[1024 * 1024];
                // outputStream.write(outputBytes);
                // write the image content
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //cinputStream.close();
                inputStream.close();
                outputStream.close();


            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Write plaintext to it.

    }

    public static void encryptIntoSDCard(String key, File inputFile,
                                         Context mContext) {

        try {

            String filePath = inputFile.getAbsolutePath();
            String[] splitStr = filePath.split("/");
            String fileExtension = splitStr[(splitStr.length - 1)].substring(splitStr[(splitStr.length - 1)].lastIndexOf(".") + 1, splitStr[(splitStr.length - 1)].length());
            String[] split1 = splitStr[(splitStr.length - 1)].split("." + fileExtension);
            String newFIleName = split1[0];

            // Creates a new Crypto object with default implementations of a key chain
            SharedPrefsBackedKeyChain keyChain = new SharedPrefsBackedKeyChain(mContext, CryptoConfig.KEY_256);
            crypto = AndroidConceal.get().createDefaultCrypto(keyChain);

            // Check for whether the crypto functionality is available
            // This might fail if Android does not load libaries correctly.
            if (!crypto.isAvailable()) {
                return;
            }

            FileInputStream inputStream = new FileInputStream(inputFile);
            DocumentFile pickedDir = DocumentFile.fromTreeUri(mContext, App_Constant.uri);

            String mimeType = "";
            switch (fileExtension.toLowerCase()) {
                case "pdf":
                    mimeType = "application/pdf";
                    break;
            }
            DocumentFile nextDocument = pickedDir.findFile(App_Constant.StorageFolderLocation);

            if (nextDocument == null) {
                nextDocument = pickedDir.createDirectory(App_Constant.StorageFolderLocation);
            }
            DocumentFile file;
            DocumentFile childFile_sub = nextDocument.findFile(newFIleName + "." + fileExtension);
            if (childFile_sub != null) {
                childFile_sub.delete();
            }
            file = nextDocument.createFile(mimeType, newFIleName + "." + fileExtension);

            OutputStream outputStream = mContext.getContentResolver().openOutputStream(file.getUri());
            outputStream = crypto.getCipherOutputStream(outputStream, Entity.create(key));

            try {
                int read;
                byte[] buffer = new byte[1024 * 1024];
                // write the image content
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                inputStream.close();
                outputStream.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static void decryptRoutine(Context mContext, File inputFile, File outputFile) {


        try {

            // Creates a new Crypto object with default implementations of a key chain
            SharedPrefsBackedKeyChain keyChain = new SharedPrefsBackedKeyChain(mContext, CryptoConfig.KEY_256);

            crypto = AndroidConceal.get().createDefaultCrypto(keyChain);

            if (!crypto.isAvailable()) {
                return;
            }

            // Get the file to which ciphertext has been written.
            FileInputStream fileStream = new FileInputStream(inputFile);
            //file = new File(getFilesDir(), "temp.temp");
            // Creates an input stream which decrypts the data as
            // it is read from it.
            InputStream inputStream = null;
            FileOutputStream out = new FileOutputStream(outputFile);

            inputStream = crypto.getCipherInputStream(
                    fileStream,
                    Entity.create(App_Constant.encryptionkey));


            // Read into a byte array.
            int read;
            byte[] buffer = new byte[1024*1024];
            // You must read the entire stream to completion.
            // The verification is done at the end of the stream.
            // Thus not reading till the end of the stream will cause
            // a security bug. For safety, you should not
            // use any of the data until it's been fully read or throw
            // away the data if an exception occurs.
            while ((read = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
