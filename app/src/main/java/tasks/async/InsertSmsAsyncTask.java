package tasks.async;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.text.TextUtils;
import android.widget.Toast;

import application.CTPTicketGeneratorApplication;
import others.SmsContainer;

/**
 * Created by andrei on 19/09/15.
 */
public class InsertSmsAsyncTask extends AsyncTask<SmsContainer, Void, Void> {

    private String defaultSmsApp;
    private ProgressDialog progressDialog;
    private Context context;

    public InsertSmsAsyncTask(Context context, String defaultSmsApp) {
        this.context = context;
        this.defaultSmsApp = defaultSmsApp;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, null, "Generating...", true);
    }

    @Override
    protected Void doInBackground(SmsContainer[] params) {
        ContentResolver contentResolver = context.getContentResolver();
        if (params != null && params.length > 0) {
            for (SmsContainer smsContainer: params) {
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
        if (!TextUtils.isEmpty(defaultSmsApp)) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
            context.startActivity(intent);
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
