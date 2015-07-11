package edu.mit.media.bandicoot.metadata;

import android.database.Cursor;
import android.provider.CallLog;

/**
 * Represents metadata from a phone call interaction.
 *
 * @author Brian Sweatt
 */
public class CallLogEntry extends MetadataEntry {

    public CallLogEntry(Cursor callLogRow) {
        this.interaction = "call";
        this.dateTime = callLogRow.getLong(callLogRow.getColumnIndex(CallLog.Calls.DATE));
        this.callDuration = callLogRow.getLong(callLogRow.getColumnIndex(CallLog.Calls.DURATION));
        int callType = callLogRow.getInt(callLogRow.getColumnIndex(CallLog.Calls.TYPE));
        // NOTE: doesn't differentiate between incoming calls that were missed or picked up
        // Does bandicoot care? Is a duration of zero enough?
        this.direction = (callType == CallLog.Calls.OUTGOING_TYPE)? "out":"in";
        setCorrespondentId(callLogRow.getString(callLogRow.getColumnIndex(CallLog.Calls.NUMBER)));
    }
}
