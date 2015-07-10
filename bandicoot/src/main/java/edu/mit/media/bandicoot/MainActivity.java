package edu.mit.media.bandicoot;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import edu.mit.media.bandicoot.metadata.MetadataReader;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button readLogsButton = (Button) findViewById(R.id.read_logs_button);
        final TextView mainTextView = (TextView) findViewById(R.id.main_text_view);

        final Intent shareFileIntent = new Intent(Intent.ACTION_SEND);
        shareFileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final MetadataReader reader = new MetadataReader(this);

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
                mainTextView.setText(String.format("Calls: %d\nTexts: %d", callCount, smsCount));
            }
        };
        showCountsTask.execute();

        final FileProvider provider = new FileProvider();

        readLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogReaderTask logReaderTask = new LogReaderTask(MainActivity.this, reader) {
                    @Override
                    protected void onPostExecute(File csvFile) {
                        super.onPostExecute(csvFile);
                        shareFileIntent.putExtra(Intent.EXTRA_SUBJECT, "Bandicoot interactions file");
                        shareFileIntent.putExtra(Intent.EXTRA_TEXT, "Attached.");
                        shareFileIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(MainActivity.this, "edu.mit.media.bandicoot.fileprovider", csvFile));
                        shareFileIntent.setType("text/csv");
                        Intent shareChooserIntent = Intent.createChooser(shareFileIntent, "Send interactions csv");
                        MainActivity.this.startActivityForResult(shareChooserIntent, 1234);
                    }
                };
                logReaderTask.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
