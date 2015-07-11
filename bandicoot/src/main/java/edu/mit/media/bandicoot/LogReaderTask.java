package edu.mit.media.bandicoot;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import edu.mit.media.bandicoot.metadata.MetadataEntry;
import edu.mit.media.bandicoot.metadata.MetadataReader;

/**
 * Asynchronous task for reading logs and writing the output to a CSV.
 * Optionally updates a progress bar, if one is provided.
 *
 * Returns a file handle to the aforementioned CSV containing all interactions.
 *
 * @author Brian Sweatt
 */
public class LogReaderTask extends AsyncTask<Void, Void, File> {

    private MetadataReader reader;
    private Context context;
    private ProgressBar progressBar;

    public LogReaderTask(Context context, MetadataReader reader) {
        this(context, reader, null);
    }

    public LogReaderTask(Context context, MetadataReader reader, ProgressBar progressBar) {
        // Creating the cursors here, so that we can get the counts from them prior to reading
        this.context = context;
        this.reader = reader;
        this.progressBar = progressBar;
    }

    @Override
    protected File doInBackground(Void... nothing) {

        List<MetadataEntry> entries = reader.getAllInteractions(progressBar);

        try {
            FileOutputStream os = context.openFileOutput("interactions.csv", Context.MODE_PRIVATE);
            for (MetadataEntry entry : entries) {
                os.write((entry.toString() + "\n").getBytes());
            }
            if (progressBar != null) {
                progressBar.setProgress(progressBar.getMax());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return context.getFileStreamPath("interactions.csv");
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
    }
}
