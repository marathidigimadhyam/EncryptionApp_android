package com.eduday.fileencrypter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.eduday.fileencrypter.Utils.App_Constant;
import com.eduday.fileencrypter.Utils.CryptoUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

import android.os.Environment;
import android.util.Log;

import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import fr.maxcom.http.CipherFactory;
import fr.maxcom.http.LocalSingleHttpServer;

public class MainActivity extends AppCompatActivity implements File_List_Adapter.CommuInterface {

    private String file_type = "";
    private String pdf_path = "";
    private String file_name = "";
    private Context mContext;
    private DecryptFileListener taskAsyncTask;
    private ListView folder_list;
    private File_List_Adapter folder_list_adapter;
    private ArrayList<File_List_Models> folder_list_data=new ArrayList<File_List_Models>();

    @Override
    public void onMethodCallback(int pos) {
        // do something
        Toast.makeText(mContext, "Starting process...", Toast.LENGTH_SHORT).show();

        writeFile(pos);
    }

    @Override
    public void onViewFile(int pos) {
        Toast.makeText(mContext, "Loading file...", Toast.LENGTH_SHORT).show();
        File file = new File(App_Constant.externalSDCard+"/"+App_Constant.StorageFolderLocation+"/"+folder_list_data.get(pos).getFile_title());
        startDecryptFile(file);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        folder_list = findViewById(R.id.folder_list);
        String[] dd = App_Constant.getExternalSDCardPath(mContext);
        if (dd.length > 0) {
            App_Constant.externalSDCard = dd[0];
        }

        accessStoragePermission();
//        file_name = getIntent().getStringExtra("file_name");
//        file_type = getIntent().getStringExtra("file_type");
//        if(file_type == null || (file_type != null && file_type.isEmpty())){
//
//        }else{
//            file_type = "video";//getIntent().getStringExtra("file_type");
//            String file_path = "11.mp4";//getIntent().getStringExtra("file_path");
//            if(file_path == null || (file_path != null && file_path.isEmpty())){
//                //status.setText("Error : "+"Invalid File !!! File does not exist on desired location.");
//            }else{
//                pdf_path =  file_path;//Environment.getExternalStorageDirectory() + "/test.pdf";
//                accessStoragePermission();
//            }
//        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("dddddd", "onRestart()...");
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private void accessStoragePermission(){
        Activity activity = (Activity) mContext;
        // Check if we have necessary permissions
        int storagePermissionWrite = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storagePermissionRead = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        ArrayList<String> permissions = new ArrayList<>();

        if (storagePermissionWrite != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (storagePermissionRead != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (permissions.size() > 0) {
            String[] permissionArray = new String[permissions.size()];
            permissions.toArray(permissionArray);

            ActivityCompat.requestPermissions(activity, permissionArray, REQUEST_EXTERNAL_STORAGE);
        }else{
            if (App_Constant.uri == null) {
                new AlertDialog.Builder(mContext).setTitle("Grant Permission").setMessage("Please grant the permission").
                        setCancelable(false)
                        .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(mContext, "Permission is not granted.You can not view this document.", Toast.LENGTH_SHORT).show();
                            }
                        }).
                        setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                                Activity activity = ((Activity) mContext);
                                activity.startActivityForResult(intent, 42);
                            }
                        }).create().show();
            }else{
                //Permission Granted
                loadFile();
            }

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (App_Constant.uri == null) {
                    new AlertDialog.Builder(mContext).setTitle("Grant Permission").setMessage("Please grant the permission").
                            setCancelable(false)
                            .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(mContext, "Permission is not granted.You can not view this document.", Toast.LENGTH_SHORT).show();
                                }
                            }).
                            setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                                    Activity activity = ((Activity) mContext);
                                    activity.startActivityForResult(intent, 42);
                                }
                            }).create().show();
                }else{
                    //Permission Granted
                    loadFile();
                }
            }else{
                accessStoragePermission();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            Toast.makeText(mContext, "Oops Permission is not granted.Please grant the permission again.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                DocumentFile pickedDir = DocumentFile.fromTreeUri(mContext, data.getData());
                String pathSelected = pickedDir.getName();
                //Toast.makeText(mContext, "pathSelected"+pathSelected+App_Constant.externalSDCard, Toast.LENGTH_SHORT).show();
                //
                if (App_Constant.externalSDCard.contains(pathSelected)) {
                    App_Constant.uri = data.getData();
                    mContext.grantUriPermission(mContext.getPackageName(), App_Constant.uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    mContext.getContentResolver().takePersistableUriPermission(App_Constant.uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    loadFile();
                } else {
                    Toast.makeText(mContext, "You have not selected External SD Card as Folder due to this Permission is not granted.Please select External SD card directory.", Toast.LENGTH_SHORT).show();
                }

                /*String pathSelected = pickedDir.getName();
                if(pathSelected.equals("edutab")){
                    mContext.grantUriPermission(mContext.getPackageName(), App_Constant.uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    mContext.getContentResolver().takePersistableUriPermission(App_Constant.uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Toast.makeText(mContext, "Good. Permission is granted.Please click on download button again.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "Oops Permission is not granted.Modified changes will not getting saved.", Toast.LENGTH_SHORT).show();
                }*/


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String readFile(File file){
        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return  text.toString();
    }
    private void loadFile(){
        try{

            readSdCardData();
            //writeFile(Environment.getExternalStorageDirectory()+"/"+"11.mp3");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void readSdCardData(){

        try {
            folder_list_data.clear();
            String[] dd = App_Constant.getExternalSDCardPath(mContext);
            if (dd.length > 0) {
                App_Constant.externalSDCard = dd[0];
            }
            if (!App_Constant.externalSDCard.isEmpty()) {
                File file = new File(App_Constant.externalSDCard+"/"+App_Constant.foldername+"/");

                ArrayList<File_List_Models> file_list_arr=new ArrayList<File_List_Models>();
                if(file.exists()){
                    if (file.isDirectory()) {
                        File[] fileArr = file.listFiles();
                        if(fileArr != null) {
                            int length = fileArr.length;
                            for (int i = 0; i < length; i++) {
                                File f = fileArr[i];
                                int folderFilelength = 0;
                                String type = App_Constant.getMimeType(f.getAbsolutePath());
                                if (f.isDirectory()) {

                                }else{
                                    String pathStr = f.getAbsolutePath();
                                    String[] spli = pathStr.split(App_Constant.externalSDCard+"/"+App_Constant.foldername);
                                    String path  = "";

                                    if(f.getParent().equals(App_Constant.externalSDCard+"/"+App_Constant.foldername)){
                                        path = "/";
                                    }else{
                                        path = spli[1];
                                    }
                                    File checkfile = new File(App_Constant.externalSDCard+"/"+App_Constant.StorageFolderLocation+"/"+f.getName());

                                    file_list_arr.add(new File_List_Models(f.getName(), f.getAbsolutePath(), String.valueOf(f.length()), type, checkfile.exists(), String.valueOf(folderFilelength), path));
                                }


                            }
                        }
                    }
                }

              /*  if(file_list_arr.size() > 0){
                    Collections.sort(file_list_arr, byName);
                }

                if(!folderpath.isEmpty()) {
                    if (folder_list_arr.size() > 0) {
                        Collections.sort(folder_list_arr, byName);
                    }
                }*/


                if (file_list_arr.size() > 0) {
                    folder_list_data.addAll(file_list_arr);
                }

            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try{

            folder_list_adapter = new File_List_Adapter(mContext,folder_list_data,R.layout.item_file_list);
            folder_list.setAdapter(folder_list_adapter);

            if(folder_list_data.size() == 0){
                new AlertDialog.Builder(mContext).setTitle("Alert").setMessage("No Data").
                        setCancelable(false).
                        setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create().show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private EncryptFileListener task;
    public void writeFile(int pos) {
        try {


            task = new EncryptFileListener(pos);
            task.execute();


        } catch (Exception e) {
            throw new RuntimeException("Something went wrong : " + e.getMessage(), e);
        }
    }

    private class EncryptFileListener extends AsyncTask<Void, Integer, Void> {

        private int pos;

        public EncryptFileListener(int iPos) {
            super();
            pos = iPos;
        }

        @Override
        protected synchronized Void doInBackground(Void... params) {

            try {

                String folderName = "PDF";

                File inputFile = new File(folder_list_data.get(pos).getFile_path());
                //CryptoUtils.encryptIntoInternalStorage(App_Constant.encryptionkey, inputFile, mContext, folderName);
                CryptoUtils.encryptIntoSDCard(App_Constant.encryptionkey, inputFile, mContext);
                //inputFile.delete();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            // setting progress percentage

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            try {
                folder_list_data.get(pos).setDoneOrNor(true);
                folder_list_adapter.notifyDataSetChanged();
                Toast.makeText(mContext, "Completed", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private void startDecryptFile(File file){
        try {
            if(taskAsyncTask != null && taskAsyncTask.getStatus() == AsyncTask.Status.RUNNING)
                taskAsyncTask.cancel(true);

            taskAsyncTask = new DecryptFileListener(file);
            taskAsyncTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DecryptFileListener extends AsyncTask<Void, Void, Void> {

        private File mainfile;
        private File tempfile;
        private String path = null;
        public DecryptFileListener(File iFilePasses) {
            super();
            mainfile = iFilePasses;
        }
        @Override
        protected Void doInBackground(Void... params) {

            try{

                if(file_type.contains("video")) {

                    try {
                        LocalSingleHttpServer mserver= new LocalSingleHttpServer();
                        mserver.setCipherFactory(new MyCipherFactory());
                        mserver.start();
                        path=mserver.getURL(mainfile.getAbsolutePath());
                        Log.d("dddd",path);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{
                    String _data = Environment.getExternalStorageDirectory().toString()+"/.data";

                    File file = new File(_data);
                    if(!file.exists()){
                        file.mkdir();
                    }
                    _data = file.getAbsolutePath()+"/.secure";

                    file = new File(_data);
                    if(!file.exists()){
                        file.mkdir();
                    }

                    _data = file.getAbsolutePath()+"/.en";

                    file = new File(_data);
                    if(!file.exists()){
                        file.mkdir();
                    }

                    tempfile = new File(file.getAbsolutePath()+"/"+mainfile.getName());
                    try{

                        CryptoUtils.decrypt(mContext, mainfile, tempfile);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }


            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(file_type.contains("video")) {
                loadVideoViewDocument(file_name,path);
            }else{
                loadPDFViewDocument(file_name,tempfile.getAbsolutePath());
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private void loadPDFViewDocument(String fileName,String filePath){
        Intent intent = new Intent(mContext,ActivityPdfViewer.class);
        intent.putExtra("fileName",fileName);
        intent.putExtra("filePath",filePath);
        startActivity(intent);
    }

    private void loadVideoViewDocument(String fileName,String filePath){

        Intent intent1 = new Intent(mContext,ActivityAudioVideoViewer.class);
        intent1.putExtra("fileName",fileName);
        intent1.putExtra("filePath",filePath);
        startActivity(intent1);
    }

    private class MyCipherFactory implements CipherFactory {
        @Override
        public Cipher getCipher() throws GeneralSecurityException {
            // you are free to choose your own Initialization Vector
            byte[] initialIV = new byte[16];
            return rebaseCipher(initialIV);
        }
        @Override
        public Cipher rebaseCipher(byte[] iv) throws GeneralSecurityException {
            final Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
            c.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(App_Constant.videoencryptionkey.getBytes(), "AES"),
                    new IvParameterSpec(iv));
            return c;
        }
    }
//
//    private static class MyCipherFactory implements CipherFactory {
//        @Override
//        public Cipher getCipher()  {
//            // you are free to choose your own Initialization Vector
//            byte[] iv =  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
//            return rebaseCipher(iv);
//        }
//        @Override
//        public Cipher rebaseCipher(byte[] iv) {
//            Cipher c = null;
//            try {
//                c = Cipher.getInstance("AES/CTR/NoPadding", "BC");
//                //Cipher c = Cipher.getInstance(ALGORITHM_VIDEO_ENCRYPTOR);
//                // in.read(iv,0,iv.length);
//                IvParameterSpec paramSpec1 = new IvParameterSpec(iv);
//                //in.skip(16);
//                SecretKeySpec ss = new SecretKeySpec(App_Constant.videoencryptionkey.getBytes(), "AES");
//                //  c.init(Cipher.DECRYPT_MODE, ss,paramSpec1);
//                c.init(Cipher.DECRYPT_MODE, ss,paramSpec1);
//            } catch (Exception e) {
//                //t("Unable to create a cipher", e);
//            }
//            return c;
//        }
//    }

}