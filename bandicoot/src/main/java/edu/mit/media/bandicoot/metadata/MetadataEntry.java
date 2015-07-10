package edu.mit.media.bandicoot.metadata;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Date;

/**
 * Created by BS on 7/9/2015.
 */
public class MetadataEntry implements Comparable<MetadataEntry> {
    public long dateTime;
    public String interaction;
    public String direction;
    public String correspondentId;
    public long callDuration;
    public String antennaId;

    @Override
    public String toString() {
        return String.format(
            "%s,%s,%s,%s,%s,",
            interaction,
            direction,
            correspondentId,
            getDateString(),
            (callDuration > 0)? callDuration : "");
    }

    protected void setCorrespondentId(String phoneNumber) {
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();

        Phonenumber.PhoneNumber number = null;
        try {
            number = util.parse(phoneNumber, "US");
        } catch (NumberParseException e) {
            e.printStackTrace();
        }


        if (number != null) {
            correspondentId = util.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        } else {
            correspondentId = phoneNumber;
        }
    }

    protected String getDateString() {
        Date date = new Date(dateTime);
        return date.toString();
    }

    @Override
    public int compareTo(MetadataEntry another) {
        return Long.signum(dateTime - another.dateTime);
    }
}
