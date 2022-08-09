package com.asimolpiq.benyanindayim;

import android.Manifest;

import android.content.Context;
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
    ActivityResultLauncher<String> requestPermissionLauncher;
    ActivityResultLauncher<String> smsRequestPermissionLauncher;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    Database db;
    PersonDAO personDAO;
    List<Person> activesList = new ArrayList<Person>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = Room.databaseBuilder(getApplicationContext(),Database.class,"Person").build();
        personDAO = db.personDAO();
        compositeDisposable.add(personDAO.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MainActivity.this::handleresponsse));
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;
        registerLauncher();
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!serviceon) {

                    if(ActivityCompat.checkSelfPermission(MainActivity.this,  Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
                        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){

                            Snackbar.make(binding.getRoot(),"Konumunuzu bulmak için izin vermeniz gerekmektedir.",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                                }
                            }).show();
                        }
                        else{
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }
                    else{
                        //permission all ready granted

                    }


                    if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.SEND_SMS)){

                            Snackbar.make(binding.getRoot(),"Konumunuzun güvendiğiniz kişilere gönderilebilmesi için izin vermelisiniz.",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestPermissionLauncher.launch(Manifest.permission.SEND_SMS);
                                }
                            }).show();
                        }
                        else{
                            requestPermissionLauncher.launch(Manifest.permission.SEND_SMS);
                        }
                    }
                    else{
                        //permission all ready granted
                        Intent intent = new Intent(getApplicationContext(),PlayerService.class);
                        intent.putExtra("serviceList", (Serializable) activesList);
                        startService(intent);
                        Toast.makeText(getApplicationContext(), "Koruma Başlatıldı!", Toast.LENGTH_LONG).show();
                        serviceon=true;
                    }

                }
                else {
                    stopService(new Intent(getApplicationContext(),PlayerService.class));
                    Toast.makeText(getApplicationContext(), "Koruma Durduruldu!!", Toast.LENGTH_LONG).show();
                    serviceon=false;
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

    public void registerLauncher(){
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)==PackageManager.PERMISSION_GRANTED) {

                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "İzin Vermelisiniz!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        smsRequestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED) {
                        startService(new Intent(getApplicationContext(), PlayerService.class));

                        Toast.makeText(getApplicationContext(), "Koruma Başlatıldı!", Toast.LENGTH_LONG).show();
                        serviceon=true;
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "İzin Vermelisiniz!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}