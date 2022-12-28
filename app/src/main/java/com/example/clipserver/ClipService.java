package com.example.clipserver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.example.clipserver.MediaServiceInterface;

import java.util.ArrayList;
import java.util.HashMap;

public class ClipService extends Service {

    private MediaPlayer mPlayer;
    private int mStartID;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "Music player style" ;
    private int[] audioMappings = new int[]{
            R.raw.mixaund_dreamers,
            R.raw.mixaund_joyful,
            R.raw.tech_audio_event,
            R.raw.mixaund_dreamers,
            R.raw.mixaund_joyful
    };


    @Override
    public void onCreate()
    {
        super.onCreate();

        createNotificationChannel();
        final Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);



        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .setContentTitle("Music Playing")
                .setContentText("Click to access music player")
                .setTicker("Music is Playing!")
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground,"Show service",pendingIntent)
                .build();
        //mPlayer.start();

        try {
            mBinder.setAudioClipNumber(1);
            mBinder.startMedia();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        startForeground(NOTIFICATION_ID, notification);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel()
    {
        CharSequence name = "Music Player Notification";
        String description = "The channel for music player notification";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public int onStartCommand(Intent intent, int flags, int startID)
    {
//        if(mPlayer != null)
//        {
//            mStartID = startID;
//            if(mPlayer.isPlaying())
//            {
//                mPlayer.seekTo(0);
//            }
//            else
//            {
//                mPlayer.start();
//            }
//        }
        Log.i("Logs", "Service has been started : onStartCommand()");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        if(mPlayer != null)
        {
            mPlayer.stop();
            mPlayer.release();
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final MediaServiceInterface.Stub mBinder = new MediaServiceInterface.Stub() {

        @Override
        public void setAudioClipNumber(int clipNumber)
        {
            mPlayer = MediaPlayer.create(getApplicationContext(), audioMappings[clipNumber + 1]);

        }

        @Override
        public void startMedia()  {
            if(mPlayer != null)
            {
                mPlayer.setLooping(false);
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopSelf(mStartID);
                        mPlayer.release(); //might be able to use this to stop and unbind service
                    }
                });
                //mPlayer.start();
            }
            if(mPlayer !=null)
         {
             mPlayer.start();
         }
        }

        @Override
        public void pauseMedia() {
            if(mPlayer != null && mPlayer.isPlaying())
            {
                mPlayer.pause();
            }
        }

        @Override
        public void resumeMedia(){
            if(mPlayer != null && !mPlayer.isPlaying())
            {
                mPlayer.start();
            }
        }

        @Override
        public void stopMedia() {
            if(mPlayer != null)
            {
                mPlayer.stop();
            }
        }
    };

}
