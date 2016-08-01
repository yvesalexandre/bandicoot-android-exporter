package edu.mit.media.bandicoot.metadata;

import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Base class for interaction metadata. Only intended to be used when writing out to a CSV file.
 *
 * @author Brian Sweatt
 */
public class Interaction implements Comparable<Interaction> {
    protected long dateTime;
    protected String interaction;
    protected String direction;
    private String correspondentId;
    protected long callDuration;
    protected String antennaId;

    @Override
    public String toString() {
        return String.format(
            "%s,%s,%s,%s,%s,",
            interaction,
            direction,
            getCorrespondentId(),
            getDateString(),
            (callDuration > 0)? callDuration : "");
    }

    protected String getCorrespondentId() {
        return (correspondentId == null)? "" : correspondentId;
    }

    protected void setCorrespondentId(String phoneNumber, boolean hashNumber) {
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();

        Phonenumber.PhoneNumber number = null;
        try {
            number = util.parse(phoneNumber, "US");
        } catch (NumberParseException e) {
            e.printStackTrace();
        }

        if (number != null) {
            correspondentId = util.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        }

        // If the correspondentId hasn't been set yet, or the formatting above was unsuccessful,
        // set it to the phoneNumber passed in
        if (correspondentId == null) {
            correspondentId = phoneNumber;
        }

        // It's hard to believe, but the correspondentId can still be null at this point.
        if (hashNumber && correspondentId != null) {
            try {
                // Hex encoded SHA-1 of the phone number, rather than the actual number
                MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                sha1.update(correspondentId.getBytes());
                BigInteger digestInt = new BigInteger(1, sha1.digest());
                correspondentId = digestInt.toString(16);
            } catch (NoSuchAlgorithmException e) {
                Log.e(getClass().getSimpleName(), "Error getting algorithm for phone number hash");
            }
        }
    }

    protected String getDateString() {
        Date date = new Date(dateTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
        return dateFormat.format(date);
    }

    @Override
    public int compareTo(Interaction another) {
        return Long.signum(dateTime - another.dateTime);
    }
}
