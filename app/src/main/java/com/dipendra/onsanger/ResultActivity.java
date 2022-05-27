package com.dipendra.onsanger;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dipendra.onsanger.databinding.ActivityResultBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;

import org.jcodec.api.SequenceEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;
    MyDataBase DB;
    MediaPlayer mediaPlayer;
    SequenceEncoder sequenceEncoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DB = new MyDataBase(ResultActivity.this);
        getSupportActionBar().hide();
        ProgressDialog progress;

        initads();
        progress = new ProgressDialog(this);
        binding.image.setImageURI(getIntent().getData());
        Picasso.get().load(getIntent().getData()).into(binding.image);
        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.image.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageandText(bitmap);
            }
        });
        binding.facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio();
            }
        });
        binding.instagrambtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking the media player 
                // if the audio is playing or not.
                if (mediaPlayer.isPlaying()) {
                    // pausing the media player if media player 
                    // is playing we are calling below line to
                    // stop our media player.
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();

                    // below line is to display a message 
                    // when media player is paused.
                    Toast.makeText(ResultActivity.this, "Audio has been paused", Toast.LENGTH_SHORT).show();
                } else {
                    // this method is called when media 
                    // player is not playing.
                    Toast.makeText(ResultActivity.this, "Audio has not played", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = ((BitmapDrawable) binding.image.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();
                // AlertDialog Builder class
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(ResultActivity.this);

                // Set the message show for the Alert time


                // Set Alert Title
                builder.setTitle("Save Image with name");
// Set up the input
                final EditText input = new EditText(getApplicationContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                // Set Cancelable false
                // for when the user clicks on the outside
                // the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name
                // OnClickListener method is use of
                // DialogInterface interface.

                builder
                        .setPositiveButton(
                                "Save",
                                new DialogInterface
                                        .OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        boolean insert = DB.insertdata(input.getText().toString(), data);
//        progress.setTitle("Saving Image!!");
//        progress.setMessage("Wait!!");
//        progress.setCancelable(true);
//        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progress.show();

                                        if (insert == true) {
                                            Toast.makeText(ResultActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ResultActivity.this, "Image Not Saved Permission Denied",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                // Set the Negative button with No name
                // OnClickListener method is use
                // of DialogInterface interface.
                builder
                        .setNegativeButton(
                                "Cancel",
                                new DialogInterface
                                        .OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        // If user click no
                                        // then dialog box is canceled.
                                        dialog.cancel();
                                    }
                                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();

                // Show the Alert Dialog box
                alertDialog.show();

            }
        });
    }

        private void initads () {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            AdView mAdView;
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        }

    private void shareImageandText(Bitmap bitmap) {
            Uri uri = getmageToShare(bitmap);
            Intent intent = new Intent(Intent.ACTION_SEND);

            // putting uri of image to be shared
            intent.putExtra(Intent.EXTRA_STREAM, uri);

            // adding text to share
            intent.putExtra(Intent.EXTRA_TEXT, "Sharing Onsanger Image");

            // Add subject Here
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");

            // setting type to image
            intent.setType("image/png");

            // calling startactivity() to share
            startActivity(Intent.createChooser(intent, "Share Via"));
        }

        // Retrieving the url to share
        private Uri getmageToShare(Bitmap bitmap) {
            File imagefolder = new File(getCacheDir(), "images");
            Uri uri = null;
            try {
                imagefolder.mkdirs();
                File file = new File(imagefolder, "shared_image.png");
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
                outputStream.flush();
                outputStream.close();
                uri = FileProvider.getUriForFile(this, "com.anni.shareimage.fileprovider", file);
            } catch (Exception e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return uri;
        }

    private void playAudio() {

        String audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";

        // initializing media player
        mediaPlayer = new MediaPlayer();

        // below line is use to set the audio
        // stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // below line is use to set our
        // url to our media player.
        try {
            mediaPlayer.setDataSource(audioUrl);
            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // below line is use to display a toast message.
        Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show();
    }
}


