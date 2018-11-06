package activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;

import adapters.LinesAdapter;
import application.SharedPreferencesUtil;
import ctp_ticket_generator.android.andreibacalu.com.ctpticketgenerator.R;
import others.SmsContainer;
import others.SmsInserter;
import tasks.async.InsertSmsAsyncTask;
import views.AutofitRecyclerView;

public class MainActivity extends Activity implements View.OnClickListener, SmsInserter /*FIXME: think of a better place to implement this interface!!!*/{

    public static final String ADDRESS_KEY = "address";
    public static final String ADDRESS_SENDER_VALUE = "7479";
    public static final String ADDRESS_RECEIVER_VALUE = "7479";
    public static final String DATE_KEY = "date";
    public static final String READ_KEY = "read";
    public static final int READ_VALUE = 1;
    public static final String TYPE_KEY = "type";
    public static final int TYPE_INCOMMING = 1;
    public static final int TYPE_OUTGOING = 2;
    public static final String BODY_KEY = "body";
    public static final String BODY_VALUE = "Biletul pentru linia %1$s a fost activat. Valabil pana la %2$s in %3$s. Cost total:0.60 EUR+Tva. Cod confirmare:%4$s.";
    public static final long MINS_45 = 45 * 60 * 1000;
    public static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
    private static final int REQUEST_CODE_SMS_DEFAULT = 11111;

    private EditText generateInput;
    private Button generateButton;
    private AutofitRecyclerView generatedLines;
    private LinesAdapter generatedLinesAdapter;

    private String defaultSmsApp;
    private String line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initViewListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        generatedLinesAdapter.setLines(SharedPreferencesUtil.getInstance().getLines());
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferencesUtil.getInstance().setLines(generatedLinesAdapter.getLines());
    }

    private void initViews() {
        generateInput = (EditText) findViewById(R.id.generate_input);
        generateButton = (Button) findViewById(R.id.generate_button);
        generatedLinesAdapter = new LinesAdapter();
        generatedLinesAdapter.setSmsInserter(this);
        generatedLines = (AutofitRecyclerView) findViewById(R.id.generated_lines);
        generatedLines.setAdapter(generatedLinesAdapter);
        Button b = new Button(this);
        generatedLines.setColumnWidth((int) (2 * b.getPaddingRight() + b.getPaint().measureText("WWW")));
    }

    private void initViewListeners() {
        generateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    generateButton.setEnabled(true);
                } else {
                    generateButton.setEnabled(false);
                }
            }
        });
        generateButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SMS_DEFAULT) {
            if (resultCode == RESULT_OK) {
                performInsertAsync(this.line);
            } else {
                // TODO: some nice UI message
            }
        }
    }

    @Override
    public void onClick(View v) {
        String line = generateInput.getText().toString().toUpperCase();
        insertSms(line);
        if (v.getId() == generateButton.getId()) {
            generatedLinesAdapter.addLine(line);
        }
    }

    public void insertSms(String line) {
        this.line = line;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
            if (!getPackageName().equals(defaultSmsApp)) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                startActivityForResult(intent, REQUEST_CODE_SMS_DEFAULT);
            }
        } else {
            performInsertAsync(line);
        }
    }

    private void performInsertAsync(String line) {
        InsertSmsAsyncTask insertSmsAsyncTask = new InsertSmsAsyncTask(this, defaultSmsApp);
        SmsContainer[] smsContainers = new SmsContainer[2];
        smsContainers[0] = new SmsContainer(ADDRESS_SENDER_VALUE, line, System.currentTimeMillis(), TYPE_OUTGOING);
        smsContainers[1] = new SmsContainer(ADDRESS_RECEIVER_VALUE, line, System.currentTimeMillis() + 10, TYPE_INCOMMING);
        insertSmsAsyncTask.execute(smsContainers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
