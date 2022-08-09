package com.asimolpiq.benyanindayim.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asimolpiq.benyanindayim.MainActivity;
import com.asimolpiq.benyanindayim.databinding.FragmentPersonDetailBinding;
import com.asimolpiq.benyanindayim.model.Person;
import com.asimolpiq.benyanindayim.roomdb.Database;
import com.asimolpiq.benyanindayim.roomdb.PersonDAO;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class PersonDetail extends Fragment {
    FragmentPersonDetailBinding binding;
    String info;
    String aktifPasif;
    Person selected_person;
    String p_name;
    Long p_number;
    String p_active;
    Boolean p_activebool;
    Database db;
    PersonDAO personDAO;
    Intent personintent;



    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    // TODO: Rename and change types of parameters

    public PersonDetail() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PersonDetail newInstance() {
        PersonDetail fragment = new PersonDetail();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = Room.databaseBuilder(getActivity().getApplicationContext(),Database.class,"Person").build();
        personDAO = db.personDAO();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) { //görünüm oluşturulduktan sonra
        super.onViewCreated(view, savedInstanceState);
        binding.saveOrChangeButton.setText(info); //butonun yazısını güncelleme veya yeni ekleme infosuna göre değiştiriyor.


        if(info.equals("Güncelle")){
            binding.nameInput.setText(selected_person.personName);
            binding.editTextPhone.setText(String.valueOf(selected_person.phoneNumber));
            if(selected_person.isActive.equals("True")){
                p_activebool= true;
            }
            if(selected_person.isActive.equals("False")){
                p_activebool= false;
            }
            binding.switch1.setChecked(p_activebool);


            //SİLME BUTONU || DELETE BUTTON FUNC
            binding.delete.setOnClickListener(new View.OnClickListener() { //silme butonu
                @Override
                public void onClick(View v) {
                    compositeDisposable.add(personDAO.delete(selected_person)
                            .subscribeOn(Schedulers.io())
                            .subscribe(PersonDetail.this::handleResponse));
                }
            });

            //GÜNCELLEME BUTONU || UPDATE BUTTON
            binding.saveOrChangeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (binding.editTextPhone.getText().toString().isEmpty() && binding.nameInput.getText().toString().isEmpty()){
                        binding.nameInput.setHint("Bu alan boş bırakılamaz!");
                        binding.editTextPhone.setHint("Bu alan boş bırakılamaz!");
                    }
                    else if(binding.nameInput.getText().toString().isEmpty() && !binding.editTextPhone.getText().toString().isEmpty()){
                        binding.nameInput.setHint("Bu alan boş bırakılamaz!");
                    }
                    else if (binding.editTextPhone.getText().toString().isEmpty() && !binding.nameInput.getText().toString().isEmpty()){
                        binding.editTextPhone.setHint("Bu alan boş bırakılamaz!");
                    }
                    else {
                        selected_person.personName = binding.nameInput.getText().toString();
                        String phoneNumber = binding.editTextPhone.getText().toString();
                        selected_person.phoneNumber = Long.parseLong(phoneNumber);
                        Boolean switchState = binding.switch1.isChecked();
                        if (switchState){
                            selected_person.isActive = "True";
                        }
                        else {
                            selected_person.isActive = "False";
                        }

                        compositeDisposable.add(personDAO.updatePerson(selected_person)
                                .subscribeOn(Schedulers.io())
                                .subscribe(PersonDetail.this::handleResponse));
                    }


                }
            });

        }

        if(info.equals("Ekle")){
            binding.delete.setVisibility(View.INVISIBLE);
            binding.delete.setVisibility(View.GONE); //eğer Persons Activityden gelen cevap Ekle ise silme butonunun kaldırılmasını söylüyoruz.

            binding.saveOrChangeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (binding.editTextPhone.getText().toString().isEmpty() && binding.nameInput.getText().toString().isEmpty()){
                        binding.nameInput.setHint("Bu alan boş bırakılamaz!");
                        binding.editTextPhone.setHint("Bu alan boş bırakılamaz!");
                    }
                    else if(binding.nameInput.getText().toString().isEmpty() && !binding.editTextPhone.getText().toString().isEmpty()){
                        binding.nameInput.setHint("Bu alan boş bırakılamaz!");
                    }
                    else if (binding.editTextPhone.getText().toString().isEmpty() && !binding.nameInput.getText().toString().isEmpty()){
                        binding.editTextPhone.setHint("Bu alan boş bırakılamaz!");
                    }


                    else {
                        String name = binding.nameInput.getText().toString();
                        String phoneNumber = binding.editTextPhone.getText().toString();
                        long phn = Long.parseLong(phoneNumber);
                        Boolean switchState = binding.switch1.isChecked();
                        if (switchState){
                            aktifPasif = "True";
                        }
                        else {
                            aktifPasif = "False";
                        }


                        Person person = new Person(name,phn,aktifPasif);
                        compositeDisposable.add(personDAO.insert(person)
                                .subscribeOn(Schedulers.io())
                                .subscribe(PersonDetail.this::handleResponse));
                    }

                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, // fragment açıldıktan sonra görünüm oluşturulduğunda
                             Bundle savedInstanceState) {
        //bundle bağlantısı ile Persons activityden gelen verilerin atamasını yapıyoruz.
        info = getArguments().getString("info");
        selected_person = (Person)getArguments().getSerializable("selected_person");
        //viewbinding bğlantısı kurduk.
        binding = FragmentPersonDetailBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void handleResponse(){
        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}