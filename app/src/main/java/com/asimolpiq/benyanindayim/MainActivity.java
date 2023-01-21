package com.asimolpiq.benyanindayim;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import com.asimolpiq.benyanindayim.Services.PlayerService;
import com.asimolpiq.benyanindayim.dialogs.PasswordChangeDialog;
import com.asimolpiq.benyanindayim.model.Person;
import com.asimolpiq.benyanindayim.roomdb.Database;
import com.asimolpiq.benyanindayim.roomdb.PersonDAO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.asimolpiq.benyanindayim.ui.main.SectionsPagerAdapter;
import com.asimolpiq.benyanindayim.databinding.ActivityMainBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean serviceon = false;
    SharedPreferences sharedPreferences;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    Database db;
    PersonDAO personDAO;
    List<Person> activesList = new ArrayList<Person>();

    private  String[] PERMISSIONS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        PERMISSIONS = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
        };
        db = Room.databaseBuilder(getApplicationContext(),Database.class,"Person").build();
        personDAO = db.personDAO();
        compositeDisposable.add(personDAO.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MainActivity.this::handleresponsse));
        if(ActivityCompat.checkSelfPermission(MainActivity.this,  Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            {
                new AlertDialog.Builder(this)
                        .setTitle("İzinler")
                        .setMessage("Değerli kullanıcımız güvenliğinizi sağlayabilmek amacıyla Arkaplanda konum ve Sms gönderme izinlerini aktif etmeniz gerekmektedir.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                      if (!hasPermissions(MainActivity.this,PERMISSIONS)){
                                        ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS,1);
                                      }

                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert).show();

            }
        }
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!serviceon) {

                    if (!hasPermissions(MainActivity.this,PERMISSIONS)){
                        ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS,1);
                    }
                        Intent intent = new Intent(getApplicationContext(),PlayerService.class);
                        intent.putExtra("serviceList", (Serializable) activesList);
                        startService(intent);
                        Toast.makeText(getApplicationContext(), "Koruma Başlatıldı!", Toast.LENGTH_LONG).show();
                        serviceon=true;
                    }
                else {
                    stopService(new Intent(getApplicationContext(), PlayerService.class));
                    Toast.makeText(getApplicationContext(), "Koruma Durduruldu!!", Toast.LENGTH_LONG).show();
                    serviceon = false;
                }
            }
        });
    }




  @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.right_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void handleresponsse (List<Person> personList){
        if(!personList.isEmpty()) {
            for (Person p : personList) {
                if (p.isActive.equals("True")&&p!=null) {
                    activesList.add(p);
                }
            }
        }




    }

    private  boolean hasPermissions(Context context, String... PERMISSIONS){
        if(context != null && PERMISSIONS !=null) {
            for (String permission : PERMISSIONS){
                if (ActivityCompat.checkSelfPermission(context,permission)!=PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startService(new Intent(getApplicationContext(), PlayerService.class));
        if (requestCode ==1){
            if (grantResults[1]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "İzin Vermelisiniz ", Toast.LENGTH_LONG).show();

            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.addPeople:
                Intent intent = new Intent(MainActivity.this, PersonsActivity.class);
                intent.putExtra("info","add");
                startActivity(intent);
                break;
            case R.id.passChange:
                openDialog();
                break;
            case R.id.info:
                Intent intent2 = new Intent(MainActivity.this,Info_Activity.class);
                startActivity(intent2);
                break;
            case R.id.report:
                Intent intent1 = new Intent(MainActivity.this,Report_Activity.class);
                startActivity(intent1);
                break;
            default:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openDialog(){
        PasswordChangeDialog passwordChangeDialog = new PasswordChangeDialog();
        passwordChangeDialog.show(getSupportFragmentManager(),"Password Change Dialog");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MainActivity.this,Calculator_Activity.class);
        startActivity(intent);
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}