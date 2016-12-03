package com.ta.madhatter.adornponebook;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UpdateContactPhotos extends AppCompatActivity {

    private ProgressDialog progress;
    int counter = 1;
    private ProgressBar mProgress;
    TextView textView;
    private int mProgressStatus = 0;
    //private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();

        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        textView = (TextView) findViewById(R.id.fini);
        textView.setText("Updating, please wait...");

//        progress = new ProgressDialog(this);
//        progress.setMessage("Updating contact photos");
//        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progress.setIndeterminate(false);
//        progress.setProgress(0);

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
                    ByteArrayOutputStream streamy = new ByteArrayOutputStream();
                    bit.compress(Bitmap.CompressFormat.JPEG, 0, streamy);
                    bit.recycle();
                    bImageList[i] = streamy.toByteArray();
                }
//                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
                Cursor contacts = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
//                Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,null,null, null);

                int count = contacts.getCount();
//                progress.setMax(count);
                mProgress.setMax(count);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run()
//                    {
//                        progress.show();
//                    }
//                });

                while (contacts.moveToNext()) {
                    //String name = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    String rawContactId = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                    long rawContactId = contacts.getLong(contacts.getColumnIndex(ContactsContract.Contacts._ID));
//                    long rawContactID = Long.parseLong(rawContactId);
                    writeDisplayPhoto(rawContactId, bImageList[counter%10]);
//                    if(counter%100==0 || counter == count) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgress.setProgress(counter);
                            }
                        });
//                    }
                    counter++;
                }
//                phones.close();
                contacts.close();

//                while (emails.moveToNext()) {
//                    //String name = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                    //String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    String rawContactId = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID));
////                    long rawContactId = contacts.getLong(contacts.getColumnIndex(ContactsContract.Contacts._ID));
//                    long rawContactID = Long.parseLong(rawContactId);
//                    writeDisplayPhoto(rawContactID, bImageList[counter%10]);
////                    if(counter%100==0 || counter == count) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mProgress.setProgress(counter);
//                            }
//                        });
////                    }
//                    counter++;
//                    lc++;
//                }
//                emails.close();

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
