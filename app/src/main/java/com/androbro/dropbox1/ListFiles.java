package com.androbro.dropbox1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.util.ArrayList;

/**
 * Created by user on 3/18/2016.
 */
public class ListFiles extends AsyncTask<Void, Void, ArrayList> {

    private DropboxAPI dropboxAPI;
    private String path;
    private Handler handler;

    public ListFiles(DropboxAPI dropboxAPI, String path, Handler handler) {
        this.dropboxAPI = dropboxAPI;
        this.path = path;
        this.handler = handler;
    }

    @Override
    protected ArrayList doInBackground(Void... params) {

        ArrayList files = new ArrayList();

        try {

            DropboxAPI.Entry directory = dropboxAPI.metadata(path, 1000, null, true, null);
            for (DropboxAPI.Entry entry: directory.contents){
                files.add(entry.fileName());
            }

        } catch (DropboxException de){

        }

        return files;
    }

    @Override
    protected void onPostExecute(ArrayList arrayList) {
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("data", arrayList);
        message.setData(bundle);
        handler.sendMessage(message);
    }
}
