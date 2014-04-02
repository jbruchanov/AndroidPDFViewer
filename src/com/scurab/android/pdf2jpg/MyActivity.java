package com.scurab.android.pdf2jpg;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import java.io.*;

public class MyActivity extends FragmentActivity {

    private String mPDFFile;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPDFFile = getFilesDir() + "/sample.pdf";
        try {
            copyPDFIntoInternalStorageIfNecessary(mPDFFile);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to copy pdf file into internal storage", Toast.LENGTH_LONG).show();
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.fragment_container);
        if (f == null) {
            f = PDFViewFragment.newInstance(mPDFFile);
            fm.beginTransaction().add(R.id.fragment_container, f).commit();
        }
    }

    /**
     * Just copy raw resource file to storage to be able access it like a simple file
     * @param targetFile
     * @throws IOException
     */
    public void copyPDFIntoInternalStorageIfNecessary(String targetFile) throws IOException {
        File target = new File(targetFile);
        target.delete();
        if (!target.exists()) {
            InputStream in = getResources().openRawResource(R.raw.sample);
            FileOutputStream out = new FileOutputStream(targetFile);
            byte[] buf = new byte[32 * 1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        }
    }
}
