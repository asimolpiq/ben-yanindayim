package com.asimolpiq.benyanindayim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.asimolpiq.benyanindayim.databinding.ActivityReportBinding;

public class Report_Activity extends AppCompatActivity {
    ActivityReportBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        View view= binding.getRoot();
        setContentView(view);
        binding.btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etMessage.getText().toString().isEmpty()&&binding.etSub.getText().toString().isEmpty()){
                    binding.etMessage.setHint("Boş bırakılamaz!");
                    binding.etSub.setHint("Boş bırakılamaz!");
                }

                else if(binding.etSub.getText().toString().isEmpty()){
                    binding.etSub.setHint("Boş bırakılamaz!");
                }
                else if (binding.etMessage.getText().toString().isEmpty()){
                    binding.etMessage.setHint("Boş bırakılamaz!");
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("mailto:"));
                    String[] emails = new String[1];
                    emails[0] = "alpayguroglu@gmail.com";
                    intent.putExtra(Intent.EXTRA_EMAIL,emails);
                    intent.putExtra(Intent.EXTRA_SUBJECT,binding.etSub.getText().toString());
                    intent.putExtra(Intent.EXTRA_TEXT,binding.etMessage.getText().toString());
                    try {
                        startActivity(Intent.createChooser(intent,"Mail Uygulamanızı Seçin:"));
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Mail uygulaması bulunamadı!",Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }
}