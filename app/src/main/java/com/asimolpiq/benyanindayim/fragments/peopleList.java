package com.asimolpiq.benyanindayim.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asimolpiq.benyanindayim.databinding.FragmentPeopleListBinding;
import com.asimolpiq.benyanindayim.model.Person;
import com.asimolpiq.benyanindayim.adapter.RecycleViewPerson;
import com.asimolpiq.benyanindayim.roomdb.Database;
import com.asimolpiq.benyanindayim.roomdb.PersonDAO;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
//Anasayfada 1.tab'de kişileri listeleyen sayfa

public class peopleList extends Fragment {
    FragmentPeopleListBinding binding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Database db;
    private PersonDAO personDAO;
    private RecycleViewPerson recycleViewPerson;
    private View view;

    public peopleList() {
        // Required empty public constructor
    }


    public static peopleList newInstance() {
        peopleList fragment = new peopleList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = Room.databaseBuilder(getActivity().getApplicationContext(),Database.class,"Person").build();
        personDAO = db.personDAO();
        compositeDisposable.add(personDAO.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(peopleList.this::handleresponsse));


    }

    private void handleresponsse (List<Person> personList){
        recycleViewPerson = new RecycleViewPerson(personList);
        binding.peopleRecycle.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recycleViewPerson.notifyDataSetChanged();
        binding.peopleRecycle.setAdapter(recycleViewPerson);




    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentPeopleListBinding.inflate(inflater,container,false);
        view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
}