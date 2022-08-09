package com.asimolpiq.benyanindayim.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.asimolpiq.benyanindayim.Calculator_Activity;
import com.asimolpiq.benyanindayim.R;

public class PasswordDialog extends AppCompatDialogFragment {
    private EditText passEditText;
    private SharedPreferences sharedPreferences;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("com.asimolpiq.benyanindayim", Context.MODE_PRIVATE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);
        passEditText = view.findViewById(R.id.passEditText);
        builder.setView(view)
                .setTitle("Hoş Geldiniz!")
                .setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (passEditText.getText().toString().isEmpty()){
                            Toast.makeText(getActivity().getApplicationContext(),"Boş bir şifre girdiğiniz için şifreniz değiştirilemedi!",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity().getApplicationContext(), Calculator_Activity.class);//Şifre belirledikten sonra uygulamayı yeniden çalıştırıyor.
                            startActivity(intent);
                            getActivity().finish();
                        }
                        else{
                            sharedPreferences.edit().putString("login_pass",passEditText.getText().toString()).apply();
                            Intent intent = new Intent(getActivity().getApplicationContext(), Calculator_Activity.class);//Şifre belirledikten sonra uygulamayı yeniden çalıştırıyor.
                            startActivity(intent);
                            getActivity().finish();
                        }

                    }
                });
        return  builder.create();
    }
}
