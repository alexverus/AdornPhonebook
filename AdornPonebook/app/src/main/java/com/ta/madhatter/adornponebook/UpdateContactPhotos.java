package com.ta.madhatter.adornponebook;

import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UpdateContactPhotos extends AppCompatActivity {

    int counter = 1;
    private ProgressBar mProgress;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        textView = (TextView) findViewById(R.id.fini);
        textView.setText("Updating, please wait...");
        mProgress = (ProgressBar) findViewById(R.id.progress_bar);

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                byte[][] bImageList = new byte[10][];
                int[] myImageList = new int[]{R.drawable.__pic1,R.drawable.__pic2, R.drawable.__pic3,R.drawable.__pic4,
                                                R.drawable.__pic5,R.drawable.__pic6, R.drawable.__pic7,
                                                R.drawable.__pic8, R.drawable.__pic9,R.drawable.__pic10};

                for (int i = 0; i < 10; i++) {
                    Bitmap bit = BitmapFactory.decodeResource(getResources(), myImageList[i]);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bit.compress(Bitmap.CompressFormat.JPEG, 0, stream);
                    bit.recycle();
                    bImageList[i] = stream.toByteArray();
                }
                Cursor contacts = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                int count = contacts.getCount();
                mProgress.setMax(count);

                while (contacts.moveToNext()) {
                    long rawContactId = contacts.getLong(contacts.getColumnIndex(ContactsContract.Contacts._ID));
                    writeDisplayPhoto(rawContactId, bImageList[counter%10]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setProgress(counter);
                        }
                    });
                    counter++;
                }
                contacts.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        textView.setText("Fini!");
                    }
                });
            }
        }).start();
    }

    public void writeDisplayPhoto(long rawContactId, byte[] photo) {
        Uri rawContactPhotoUri = Uri.withAppendedPath(
                ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactId),
                ContactsContract.RawContacts.DisplayPhoto.CONTENT_DIRECTORY);
        try {
            AssetFileDescriptor fd = getContentResolver().openAssetFileDescriptor(rawContactPhotoUri, "rw");
            OutputStream os = fd.createOutputStream();
            os.write(photo);
            os.close();
            fd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
