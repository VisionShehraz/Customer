package com.gotaxiride.driver.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * Created by GagahIB on 27/11/2016.
 */

public class AsyncTaskHelper extends AsyncTask<Void, Void, String> {

    public ProgressDialog dialog;
    public Activity activity;
    public boolean isDialogShow;

    OnAsyncTaskListener mCallback;

    public AsyncTaskHelper(Activity activity, boolean isDialogShow) {
        this.activity = activity;
        this.isDialogShow = isDialogShow;
    }

    public void setAsyncTaskListener(OnAsyncTaskListener listener) {
        try {
            mCallback = listener;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString() + "Did not implement OnMGAsyncTaskListener");
        }
    }

    @Override
    protected void onPreExecute() {
        // Things to be done before execution of long running operation. For example showing ProgessDialog
        if (isDialogShow) {
            dialog = ProgressDialog.show(activity, "", "Loading...", true);
        }
        mCallback.onAsyncTaskPreExecute(this);
    }

    @Override
    protected String doInBackground(Void... params) {
        mCallback.onAsyncTaskDoInBackground(this);
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation. parse json data
        if (isDialogShow) {
            dialog.dismiss();
        }
        mCallback.onAsyncTaskPostExecute(this);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // Things to be done while execution of long running operation is in progress. For example updating ProgessDialog
        mCallback.onAsyncTaskProgressUpdate(this);
    }

    public interface OnAsyncTaskListener {
        void onAsyncTaskDoInBackground(AsyncTaskHelper asyncTask);

        void onAsyncTaskProgressUpdate(AsyncTaskHelper asyncTask);

        void onAsyncTaskPostExecute(AsyncTaskHelper asyncTask);

        void onAsyncTaskPreExecute(AsyncTaskHelper asyncTask);
    }
}
