package activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import ctp_ticket_generator.android.andreibacalu.com.ctpticketgenerator.R;

public class MainActivity extends Activity {

    public static final String ADDRESS_KEY = "address";
    public static final String ADDRESS_VALUE = "7479";
    public static final String DATE_KEY = "date";
    public static final String READ_KEY = "read";
    public static final int READ_VALUE = 1;
    public static final String TYPE_KEY = "type";
    public static final int TYPE_INCOMMING = 2;
    public static final int TYPE_OUTGOING = 1;
    public static final String BODY_KEY = "body";
    public static final String BODY_VALUE = "Biletul pentru linia %1$s a fost activat. Valabil pana la %2$s in %3$s. Cost total:0.50 EUR+Tva. Cod confirmare:%4$s.";
    public static final long MINS_45 = 45 * 60 * 1000;
    public static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");

    private EditText generateInput;
    private Button generateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initViewListeners();
    }

    private void initViews() {
        generateInput = (EditText) findViewById(R.id.generate_input);
        generateButton = (Button) findViewById(R.id.generate_button);
        if (generateButton == null && generateInput == null) {
            finish();
        }
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
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSms();
            }
        });
    }

    private void insertSms() {
        ContentResolver contentResolver = getContentResolver();
        insertOutgoingSMS(contentResolver);
        insertIncommingSMS(contentResolver);
    }

    private void insertOutgoingSMS(ContentResolver contentResolver) {
        SmsContainer smsContainer = new SmsContainer("7479", generateInput.getText().toString(), System.currentTimeMillis(), TYPE_OUTGOING);
        contentResolver.insert(Uri.parse("content://sms"), smsContainer.toContentValues());
    }

    private void insertIncommingSMS(ContentResolver contentResolver) {
        SmsContainer smsContainer = new SmsContainer("7479", generateInput.getText().toString(), System.currentTimeMillis(), TYPE_INCOMMING);
        contentResolver.insert(Uri.parse("content://sms"), smsContainer.toContentValues());
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

    private static class SmsContainer {

        private String address;
        private String lineNumber;
        private String message;
        private long date;
        private int type;

        public SmsContainer(String address, String lineNumber, long date, int type) {
            this.address = address;
            this.lineNumber = lineNumber;
            this.date = date;
            this.type = type;
        }

        //FIXME: create a factory method, constructor is confusing!!!
        public SmsContainer(String address, long date, int type, String message) {
            this.address = address;
            this.message = message;
            this.date = date;
            this.type = type;
        }

        public SmsContainer addAddress(String address) {
            this.address = address;
            return this;
        }

        public SmsContainer addLineNumber(String lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        public SmsContainer addMessage(String message) {
            this.message = message;
            return this;
        }

        public SmsContainer addDate(long date) {
            this.date = date;
            return this;
        }

        public SmsContainer addType(int type) {
            this.type = type;
            return this;
        }

        public ContentValues toContentValues() {
            ContentValues contentValues = new ContentValues();
            addContentValue(contentValues, ADDRESS_KEY, address, ADDRESS_VALUE);
            contentValues.put(DATE_KEY, date);
            contentValues.put(READ_KEY, READ_VALUE);
            contentValues.put(TYPE_KEY, type);
            completeWithMessage(contentValues);
            return contentValues;
        }

        private void completeWithMessage(ContentValues contentValues) {
            if (type == TYPE_INCOMMING) {
                Date expirationDate = new Date(date + MINS_45);
                addContentValue(contentValues, BODY_KEY, message, String.format(BODY_VALUE, lineNumber, timeformat.format(expirationDate), dateformat.format(expirationDate), generateRandom6Digits()));
            } else {
                addContentValue(contentValues, BODY_KEY, lineNumber, null);
            }//FIXME: else illegal argument!!!
        }

        private String generateRandom6Digits() {
            Random random = new Random(System.currentTimeMillis());
            StringBuilder sb = new StringBuilder(6);
            sb.append(random.nextInt(9) + 1);
            for (int i = 0; i < 5; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
        }

        private void addContentValue(ContentValues contentValue, String key, String value, String defaultValue) {
            if (!TextUtils.isEmpty(value)) {
                contentValue.put(key, value);
            } else {
                contentValue.put(key, defaultValue);
            }
        }
    }
}
