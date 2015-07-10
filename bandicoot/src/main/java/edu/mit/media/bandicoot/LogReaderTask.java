package edu.mit.media.bandicoot;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import edu.mit.media.bandicoot.metadata.MetadataEntry;
import edu.mit.media.bandicoot.metadata.MetadataReader;

/**
 * Created by BS on 7/9/2015.
 */
public class LogReaderTask extends AsyncTask<Void, Void, File> {

    private MetadataReader reader;
    private Context context;

    public LogReaderTask(Context context, MetadataReader reader) {
        // Creating the cursors here, so that we can get the counts from them prior to reading
        this.context = context;
        this.reader = reader;
    }

    @Override
    protected File doInBackground(Void... nothing) {

        List<MetadataEntry> entries = reader.getAllInteractions();

        //File outputDir = context.getFilesDir();
        //File outputFile = null;
        try {
            //outputFile = File.createTempFile("interactions", "csv", outputDir);
            FileOutputStream os = context.openFileOutput("interactions.csv", Context.MODE_PRIVATE);
            for (MetadataEntry entry : entries) {
                os.write((entry.toString() + "\n").getBytes());
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
