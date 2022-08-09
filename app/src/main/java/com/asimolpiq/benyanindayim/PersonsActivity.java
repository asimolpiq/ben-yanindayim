package com.asimolpiq.benyanindayim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.asimolpiq.benyanindayim.databinding.ActivityPersonsBinding;
import com.asimolpiq.benyanindayim.fragments.PersonDetail;
import com.asimolpiq.benyanindayim.model.Person;
//persons activity içerisinde PersonDetail fragmentını barındıran ana activity'dir.
public class PersonsActivity extends AppCompatActivity {
    ActivityPersonsBinding binding;

    String info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = this.getIntent();
        info = intent.getStringExtra("info");


        if(info.equals("add")){//MainActivityden eklemek için bir komut dönerse PersonDetail fragmentını çağırıp kaydet butonunu ekleye çeviriyor
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            PersonDetail personDetail = new PersonDetail();

            Bundle bundle = new Bundle();
            bundle.putString("info","Ekle");

            personDetail.setArguments(bundle);
            fragmentTransaction.add(R.id.fragments,personDetail).commit();
        }
        if(info.equals("update")){//MainActivityden güncellemek için bir komut dönerse PersonDetail fragmentını çağırıp kaydet butonunu güncelleye çeviriyor
            Person person1 = (Person) intent.getSerializableExtra("person"); //burada adaptörden gelen bilgileri  Person Detail fragmentına bundle aracılığıyla aktarıyoruz

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            PersonDetail personDetail = new PersonDetail();

            Bundle bundle = new Bundle();
            bundle.putSerializable("selected_person",person1);
            bundle.putInt("p_id", person1.uid);
            bundle.putString("p_name",person1.personName);
            bundle.putLong("p_number",person1.phoneNumber);
            bundle.putString("p_active", person1.isActive);
            bundle.putString("info","Güncelle");

            personDetail.setArguments(bundle);
            fragmentTransaction.add(R.id.fragments,personDetail).commit();
        }
    }
}