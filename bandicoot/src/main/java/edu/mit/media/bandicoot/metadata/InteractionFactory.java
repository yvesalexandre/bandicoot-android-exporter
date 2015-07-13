package edu.mit.media.bandicoot.metadata;

import android.database.Cursor;

/**
 * A factory for creating Interaction objects from underlying cursors. Any configuration
 * that should apply to all objects being created should occur in the constructor for this
 * class. For example, number hashing or field formatting behavior.
 *
 * We eventually might want to move the actual object construction logic into this class and
 * do away with the individual subclasses.
 *
 * @author Brian Sweatt
 */
public class InteractionFactory {

    private boolean hashNumbers;

    public InteractionFactory(boolean hashNumbers) {
        this.hashNumbers = hashNumbers;
    }

    public Interaction getCallInteraction(Cursor cursor) {
        return new CallInteraction(cursor, hashNumbers);
    }

    public Interaction getTextInteraction(Cursor cursor) {
        return new TextInteraction(cursor, hashNumbers);
    }

    public void setHashNumbers(boolean hashNumbers) {
        this.hashNumbers = hashNumbers;
    }
}
