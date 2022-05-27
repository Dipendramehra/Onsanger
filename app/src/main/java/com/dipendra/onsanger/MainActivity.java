package com.dipendra.onsanger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dipendra.alertdialogshow.AlertDialogShow;
import com.dipendra.alertdialogshow.FFmpegActivity;
import com.dipendra.onsanger.databinding.ActivityMainBinding;

import com.dipendra.toasty.Toaster;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {
//    InterstitialAd mInterstitialAd;
private static final String AD_UNIT_ID = "ca-app-pub-5082239756548143/8508636715";
    private static final String TAG = "Interstitial";
   AlertDialog.Builder builder;
String Videouri,AudioUri;
    private InterstitialAd interstitialAd;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    ActivityMainBinding binding;
    int IMAGE_REQUEST_CODE = 45;
    int CAMERA_REQUEST_CODE = 14;
    int FILE_FROM_STORAGE = 184;
    int VIDEO_FROM_STORAGE = 1274;
    int AUDIO_FROM_STORAGE = 26;
    int RESULT_CODE = 200;
 private AdView adviewmain;

    private NavigationView nvDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        nvDrawer = (NavigationView) findViewById(R.id.navigationView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//                        @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
        loadAd();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
        }

        builder = new AlertDialog.Builder(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        adviewmain = findViewById(R.id.adView5);
        adviewmain.loadAd(adRequest);

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitial();
            }
        });
        binding.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });

        binding.merge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FFmpegActivity.mergevideoandaudio(Videouri, AudioUri,Environment.getExternalStorageDirectory().getPath()
                        + "/Download/Output Mixed Video.mp4");
            }
        });

        binding.eturl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        binding.trimVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FFmpegActivity.trimvideo(Videouri, Environment.getExternalStorageDirectory().getPath()
                        + "/Download/Output Trimmed Video.mp4");

//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("*/*");
//                startActivityForResult(intent,FILE_FROM_STORAGE);
            }
        });
        binding.Videotoaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FFmpegActivity.videotoaudio(Videouri, Environment.getExternalStorageDirectory().getPath()
                        + "/Download/output shsh audio.mp3");
            }
        });
        binding.cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

            }
        });

    }



    @SuppressLint("ResourceAsColor")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST_CODE) {
            if(data.getData() != null) {
                Uri filePath = data.getData();
                Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
                dsPhotoEditorIntent.setData(filePath);
                dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Onsanger");
                int[] toolsToHide = {DsPhotoEditorActivity.TOOL_ORIENTATION, DsPhotoEditorActivity.TOOL_CROP};
                dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
                startActivityForResult(dsPhotoEditorIntent, RESULT_CODE);
            }
        }
        if(requestCode == FILE_FROM_STORAGE) {
           String path=data.getData().getPath();
            TextView textView=binding.textView5at;
            textView.setText(path);
            textView.setTextColor(R.color.purple_200);

            }
        if(requestCode ==VIDEO_FROM_STORAGE) {
            Videouri=data.getData().getPath();


        }
        if(requestCode == AUDIO_FROM_STORAGE) {
            AudioUri=data.getData().getPath();


        }

        if(requestCode == RESULT_CODE) {
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.setData(data.getData());
            startActivity(intent);
        }

        if(requestCode == CAMERA_REQUEST_CODE) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri uri = getImageUri(photo);
            Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
            dsPhotoEditorIntent.setData(uri);
            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Pico");
            int[] toolsToHide = {DsPhotoEditorActivity.TOOL_ORIENTATION, DsPhotoEditorActivity.TOOL_CROP};
            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
            startActivityForResult(dsPhotoEditorIntent, RESULT_CODE);
        }
    }

    public Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked

        switch(menuItem.getItemId()) {
            case R.id.nav_library:
                startActivity(new Intent(MainActivity.this,Library.class));
                break;
            case R.id.nav_profile:
                Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                break;


        }



        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());

    }
public void loadAd() {
    AdRequest adRequest = new AdRequest.Builder().build();
    InterstitialAd.load(
            this,
            AD_UNIT_ID,
            adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    MainActivity.this.interstitialAd = interstitialAd;
                    Log.i(TAG, "onAdLoaded");
                    Toast.makeText(MainActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                    interstitialAd.setFullScreenContentCallback(
                            new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when fullscreen content is dismissed.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    MainActivity.this.interstitialAd = null;
                                    Log.d("TAG", "The ad was dismissed.");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    // Called when fullscreen content failed to show.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    MainActivity.this.interstitialAd = null;
                                    Log.d("TAG", "The ad failed to show.");
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    // Called when fullscreen content is shown.
                                    Log.d("TAG", "The ad was shown.");
                                }
                            });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i(TAG, loadAdError.getMessage());
                    interstitialAd = null;

                    String error =
                            String.format(
                                    "domain: %s, code: %d, message: %s",
                                    loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                    Toast.makeText(
                            MainActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                            .show();
                }
            });
}
    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);

            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
            });
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        }
    }
    private void startGame() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (interstitialAd == null) {
            loadAd();
        }


    }
    private void makemergealert() {
        //Uncomment the below code to Set the message and title from the strings.xml file


        //Setting message manually and performing action on button click
        builder.setMessage("Select Video and audio To be mixed")
                .setCancelable(false)
                .setPositiveButton("Video", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent,VIDEO_FROM_STORAGE);
                    }
                })
                .setNegativeButton("Audio", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent,AUDIO_FROM_STORAGE);
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("AlertDialogExample");
        alert.show();
    }

}