package com.asimolpiq.benyanindayim.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asimolpiq.benyanindayim.PersonsActivity;
import com.asimolpiq.benyanindayim.databinding.RecycleRowBinding;
import com.asimolpiq.benyanindayim.model.Person;

import java.util.List;

public class RecycleViewPerson extends RecyclerView.Adapter<RecycleViewPerson.PeopleHolder> {

     static List<Person> peopleList;
     private int countnumber = 0;

    public RecycleViewPerson(List<Person> peopleList) {
        this.peopleList = peopleList;
    }

    @NonNull
    @Override
    public PeopleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecycleRowBinding recycleRowBinding = RecycleRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PeopleHolder(recycleRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleHolder holder, @SuppressLint("RecyclerView") int position) {
        //Integer.toString(peopleList.get(position).uid)
        countnumber++;
        holder.binding.recycleViewIdText.setText(countnumber+".");
        holder.binding.recycleViewTextView.setText(peopleList.get(position).personName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), PersonsActivity.class); //ilk önce bu fragmentın ait olduğu activity olan persons activitye intent alıyoruz
                intent.putExtra("info","update"); //buradan yollayacağımız informationları persons activitye yönlendiriyoruz.
                intent.putExtra("person",peopleList.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return peopleList.size();

    }

    class PeopleHolder extends RecyclerView.ViewHolder{
        private RecycleRowBinding binding;
        public PeopleHolder(@NonNull RecycleRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
