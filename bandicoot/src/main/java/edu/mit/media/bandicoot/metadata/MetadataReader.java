package edu.mit.media.bandicoot.metadata;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used to read interaction metadata from call logs and SMS
 *
 * @author Brian Sweatt
 */
public class MetadataReader {
    private Cursor callLogCursor;
    private Cursor smsCursor;
    private Context context;

    public MetadataReader(Context context) {
        // Creating the cursors here, so that we can get the counts from them prior to reading
        this.context = context;
        createCursorsIfNecessary();
    }

    public List<MetadataEntry> getAllInteractions(ProgressBar progressBar) {
        List<MetadataEntry> entries = new ArrayList<MetadataEntry>();

        createCursorsIfNecessary();

        if (progressBar != null) {
            progressBar.setMax((getCallLogCount() + getSmsCount()) * 2);
        }

        int i = 0;
        while (callLogCursor.moveToNext()) {
            entries.add(new CallLogEntry(callLogCursor));
            i++;
            if (progressBar != null && i % 10 == 0) {
                progressBar.setProgress(i);
            }
        }

        callLogCursor.close();

        while (smsCursor.moveToNext()) {
            entries.add(new SmsEntry(smsCursor));
            i++;
            if (progressBar != null && i % 10 == 0) {
                progressBar.setProgress(i);
            }
        }

        smsCursor.close();

        Collections.sort(entries);
        return entries;
    }

    private void updateProgress(ProgressBar progressBar, int progress) {

    }

    private void createCursorsIfNecessary() {
        if (callLogCursor == null || callLogCursor.isClosed()) {
            callLogCursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.DATE + " ASC"
            );
        }

        if (smsCursor == null || smsCursor.isClosed()) {
            // NOTE: using a hard-coded content URI, since the SMS URI isn't public below KitKat
            smsCursor = context.getContentResolver().query(
                Uri.parse("content://sms"),
                null,
                null,
                null,
                null
            );
        }
    }

    public int getCallLogCount() {
        return callLogCursor.getCount();
    }

    public int getSmsCount() {
        return smsCursor.getCount();
    }
}
