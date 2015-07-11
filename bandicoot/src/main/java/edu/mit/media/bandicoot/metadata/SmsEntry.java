package edu.mit.media.bandicoot.metadata;

import android.database.Cursor;
import android.provider.Telephony;

/**
 * Represents metadata from a text message interaction.
 *
 * @author Brian Sweatt
 */
public class SmsEntry extends MetadataEntry {
    public SmsEntry(Cursor smsRow) {
        this.interaction = "text";
        int type = smsRow.getInt(smsRow.getColumnIndex(Telephony.Sms.TYPE));
        // NOTE: we're only handling successfully sent and received messages, not transient states
        // This should be included as a filter on the query
        this.direction = (type == Telephony.Sms.MESSAGE_TYPE_SENT)? "out":"in";
        this.dateTime = smsRow.getLong(smsRow.getColumnIndex(Telephony.Sms.DATE));
        setCorrespondentId(smsRow.getString(smsRow.getColumnIndex(Telephony.Sms.ADDRESS)));

    }
}
