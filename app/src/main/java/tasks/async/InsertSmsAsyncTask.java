package tasks.async;

import android.app.AlertDialog;
import android.app.Application;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import activities.MainActivity;
import application.CTPTicketGeneratorApplication;

/**
 * Created by andrei on 19/09/15.
 */
public class InsertSmsAsyncTask extends AsyncTask<MainActivity.SmsContainer, Void, Void> {

    private ProgressDialog progressDialog;
    private Context context;

    public InsertSmsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, null, "Generating...", true);
    }

    @Override
    protected Void doInBackground(MainActivity.SmsContainer[] params) {
        ContentResolver contentResolver = context.getContentResolver();
        if (params != null && params.length > 0) {
            for (MainActivity.SmsContainer smsContainer: params) {
                if (isCancelled()) {
                    return null;
                }
                contentResolver.insert(Uri.parse("content://sms"), smsContainer.toContentValues());
            }
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        dismissProgressDialog();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        dismissProgressDialog();
        Toast.makeText(CTPTicketGeneratorApplication.getInstance(), "Ticket generated successfully :)", Toast.LENGTH_LONG).show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
