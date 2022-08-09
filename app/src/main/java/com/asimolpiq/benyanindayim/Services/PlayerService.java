package com.asimolpiq.benyanindayim.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.VolumeProviderCompat;

import com.asimolpiq.benyanindayim.MainActivity;
import com.asimolpiq.benyanindayim.R;
import com.asimolpiq.benyanindayim.model.Person;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class PlayerService extends Service {
    private MediaSessionCompat mediaSession;
    int sayac = 0;
    private Runnable runnable;
    private Handler handler;
    private boolean yolla = false;
    private SharedPreferences sharedPreferences;
    private LocationListener listener;
    private LocationManager locationManager;
    private List<Person> activesList;
    private Boolean user_location_provider;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("com.asimolpiq.benyanindayim",Context.MODE_PRIVATE);
        user_location_provider= sharedPreferences.getBoolean("location_provider",false);
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {

            @Override
            public void onLocationChanged(@NonNull Location location) {
                   /* Intent i = new Intent("location_update");
                    i.putExtra("lat",location.getLatitude());
                    i.putExtra("longi",location.getLongitude());
                    sendBroadcast(i); */


                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());


                if (!activesList.isEmpty()){
                    for (Person p : activesList) {
                        handler = new Handler();
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String sMessage = p.personName+", tehlikede olabilirim. Bana yardım etmen için anlık konum linkim : https://maps.google.com/?q="+latitude+","+longitude;
                                    SmsManager smsManager=SmsManager.getDefault();
                                    smsManager.sendTextMessage("+90"+p.phoneNumber,null,sMessage,null,null);
                                    Toast.makeText(getApplicationContext(),"Yardım mesajı iletildi!",Toast.LENGTH_LONG).show();
                                }catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(),"Mesaj gönderilemedi!",Toast.LENGTH_LONG).show();
                                }

                            }
                        };
                        handler.post(runnable);
                    }
                }
                else{
                    System.out.println("hata var!");
                }


                System.out.println(latitude+" "+longitude);


            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }

        };



        //kontrol
        mediaSession = new MediaSessionCompat(this, "PlayerService");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0) //you simulate a player which plays something.
                .build());

        //this will only work on Lollipop and up, see https://code.google.com/p/android/issues/detail?id=224134
        VolumeProviderCompat myVolumeProvider =
                new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, /*max volume*/100, /*initial volume level*/50) {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onAdjustVolume(int direction) {
                /*
                -1 -- volume down
                1 -- volume up
                0 -- volume button released
                 */
                        if (direction == 1) { //seç açma tuşuysa
                            sayac++;
                            if (sayac == 4) { //4.kez basıldığında uyar
                                yolla = true;

                                Vibrator v = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
                                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                        .setUsage(AudioAttributes.USAGE_ALARM)
                                        .build();
                                VibrationEffect ve = VibrationEffect.createOneShot(1000,
                                        VibrationEffect.DEFAULT_AMPLITUDE);
                                v.vibrate(ve, audioAttributes);
                            }

                            if ( sayac == 5&& yolla) { //5.kez basıldığında işlemlere başla
                                sayac = 0;
                                yolla = false;
                                if (!user_location_provider){
                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, listener);
                                }
                                else {
                                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 0, listener);
                                }
                                Toast.makeText(getApplicationContext(), "Yardım çağrısı başladı!", Toast.LENGTH_LONG).show();
                            }
                        }


                    }
                };

        mediaSession.setPlaybackToRemote(myVolumeProvider);
        mediaSession.setActive(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.release();
        if (locationManager!=null){
            locationManager.removeUpdates(listener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        if (intent != null && intent.getExtras() != null){
            activesList = (List<Person>) intent.getSerializableExtra("serviceList");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.asimolpiq.benyanindayim";
        String channelName = "Benim Arkaplan Servisim";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setContentTitle("Merak etme 'BEN YANINDAYIM'")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
}
