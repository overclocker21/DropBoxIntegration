package com.androbro.dropbox1;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by user on 3/18/2016.
 */
public class UploadFile extends AsyncTask<Void, Void, Boolean> {

    private DropboxAPI dropboxAPI;
    private String path;
    private Context context;

    public UploadFile(Context context, DropboxAPI dropboxAPI, String path) {
        this.dropboxAPI = dropboxAPI;
        this.path = path;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        final File tempSropBoxDirectory = context.getCacheDir();
        File tempFileToUpload;
        FileWriter fileWriter = null;

        try{

            tempFileToUpload = File.createTempFile("file", ".txt", tempSropBoxDirectory);
            fileWriter = new FileWriter(tempFileToUpload);
            fileWriter.write("Hello World");
            fileWriter.close();

            FileInputStream fileInputStream = new FileInputStream(tempFileToUpload);
            dropboxAPI.putFile(path + "helloworld.txt", fileInputStream,
                    tempFileToUpload.length(), null, null);
            tempFileToUpload.delete();
            return true;

        } catch (IOException e){

        } catch (DropboxException de){

        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result){
            Toast.makeText(context, "File has been uploaded", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context, "Error occured!", Toast.LENGTH_LONG).show();
        }
    }
}
