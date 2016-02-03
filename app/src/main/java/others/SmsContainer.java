package others;

import android.content.ContentValues;
import android.text.TextUtils;

import java.util.Date;
import java.util.Random;

import activities.MainActivity;

public class SmsContainer {

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
        addContentValue(contentValues, MainActivity.ADDRESS_KEY, address, MainActivity.ADDRESS_SENDER_VALUE);
        contentValues.put(MainActivity.DATE_KEY, date);
        contentValues.put(MainActivity.READ_KEY, MainActivity.READ_VALUE);
        contentValues.put(MainActivity.TYPE_KEY, type);
        completeWithMessage(contentValues);
        return contentValues;
    }

    private void completeWithMessage(ContentValues contentValues) {
        if (type == MainActivity.TYPE_INCOMMING) {
            Date expirationDate = new Date(date + MainActivity.MINS_45);
            addContentValue(contentValues, MainActivity.BODY_KEY, message, String.format(MainActivity.BODY_VALUE, lineNumber, MainActivity.timeformat.format(expirationDate), MainActivity.dateformat.format(expirationDate), generateRandom6Digits()));
        } else {
            addContentValue(contentValues, MainActivity.BODY_KEY, lineNumber, null);
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
