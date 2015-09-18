package edu.mit.media.bandicoot;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import edu.mit.media.bandicoot.metadata.InteractionReader;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button readLogsButton = (Button) findViewById(R.id.read_logs_button);
        final TextView totalTextView = (TextView) findViewById(R.id.total_text_view);
        //final TextView callTextView = (TextView) findViewById(R.id.call_text_view);
        //final TextView smsTextView = (TextView) findViewById(R.id.sms_text_view);
        final TextView fileSizeTextView = (TextView) findViewById(R.id.filesize_text_view);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final CheckBox hashNumbersCheckbox = (CheckBox) findViewById(R.id.hash_numbers_checkbox);

        final Intent shareFileIntent = new Intent(Intent.ACTION_SEND);
        shareFileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final InteractionReader reader = new InteractionReader(this, hashNumbersCheckbox.isChecked());

        // Wrapping this in a task as it could take a while for the count query to return
        AsyncTask<Void, Void, Void> showCountsTask = new AsyncTask<Void, Void, Void>() {
            private int smsCount;
            private int callCount;
            @Override
            protected Void doInBackground(Void... params) {
                smsCount = reader.getSmsCount();
                callCount = reader.getCallLogCount();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                totalTextView.setText(String.format(getString(R.string.total_interactions), callCount, smsCount));
                //callTextView.setText(String.format("%s %d", getString(R.string.calls), callCount));
                //smsTextView.setText(String.format("%s %d", getString(R.string.texts), smsCount));
                LogReaderTask logReaderTask = new LogReaderTask(MainActivity.this, reader) {
                    @Override
                    protected void onPostExecute(File file) {
                        super.onPostExecute(file);
                        fileSizeTextView.setText(humanReadableByteCount(file.length(), true));
                    }
                };

                logReaderTask.execute();
            }
        };
        showCountsTask.execute();

        readLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                reader.setHashNumbers(hashNumbersCheckbox.isChecked());
                LogReaderTask logReaderTask = new LogReaderTask(MainActivity.this, reader, progressBar) {
                    @Override
                    protected void onPostExecute(File csvFile) {
                        super.onPostExecute(csvFile);
                        progressBar.setVisibility(View.GONE);
                        shareFileIntent.putExtra(Intent.EXTRA_SUBJECT, "Bandicoot metadata file");
                        shareFileIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body));
                        shareFileIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(MainActivity.this, "edu.mit.media.bandicoot.fileprovider", csvFile));
                        shareFileIntent.setType("text/csv");
                        Intent shareChooserIntent = Intent.createChooser(shareFileIntent, "Send metadata csv");
                        MainActivity.this.startActivityForResult(shareChooserIntent, 1234);
                    }
                };
                logReaderTask.execute();
            }
        });
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
