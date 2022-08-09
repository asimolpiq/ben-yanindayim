package com.asimolpiq.benyanindayim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.asimolpiq.benyanindayim.Services.PlayerService;
import com.asimolpiq.benyanindayim.dialogs.PasswordDialog;
import com.asimolpiq.benyanindayim.model.Person;
import com.asimolpiq.benyanindayim.roomdb.Database;
import com.asimolpiq.benyanindayim.roomdb.PersonDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Calculator_Activity extends AppCompatActivity {
    TextView user_input, sign_Box;
    Double num1, num2, answer;
    String sign, val_1, val_2;
    Button eksi;
    private Boolean serviceOn = false;
    private SharedPreferences sharedPreferences;
    private String password;
    boolean has_Dot;
    private Database db;
    private PersonDAO personDAO;
    private List<Person> activesList = new ArrayList<Person>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        sharedPreferences = this.getSharedPreferences("com.asimolpiq.benyanindayim", Context.MODE_PRIVATE);
        password = sharedPreferences.getString("login_pass",null);
        if(password==null){
            openDialog(); //eğer kullanıcı bir şifre belirlemediyse
        }
        user_input = (TextView) findViewById(R.id.input_user);
        sign_Box = (TextView) findViewById(R.id.sign_user);
        eksi = findViewById(R.id.eksi);

        db = Room.databaseBuilder(getApplicationContext(),Database.class,"Person").build();
        personDAO = db.personDAO();
        compositeDisposable.add(personDAO.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(Calculator_Activity.this::handleresponsse));

        eksi.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!serviceOn){
                    Intent intent = new Intent(getApplicationContext(),PlayerService.class);
                    intent.putExtra("serviceList", (Serializable) activesList);
                    startService(intent);
                    Toast.makeText(getApplicationContext(), "Koruma Başlatıldı!", Toast.LENGTH_LONG).show();
                    serviceOn=true;
                }
                else{
                    stopService(new Intent(getApplicationContext(),PlayerService.class));
                    Toast.makeText(getApplicationContext(), "Koruma Durduruldu!!", Toast.LENGTH_LONG).show();
                    serviceOn=false;
                }
                return true;
            }
        });
        has_Dot = false;




    }



    private void handleresponsse (List<Person> personList) {
        if (!personList.isEmpty()) {
            for (Person p : personList) {
                if (p.isActive.equals("True") && p != null) {
                    activesList.add(p);
                }
            }
        }
    }

    public void openDialog(){
        PasswordDialog passwordDialog = new PasswordDialog();
        passwordDialog.setCancelable(false);
        passwordDialog.show(getSupportFragmentManager(),"Password Dialog");
    }

    @SuppressLint("SetTextI18n")
    public void btn_0(View view) {
        user_input.setText(user_input.getText() + "0");
    }
    @SuppressLint("SetTextI18n")
    public void btn_1(View view) {
        user_input.setText(user_input.getText() + "1");
    }

    @SuppressLint("SetTextI18n")
    public void btn_2(View view) {
        user_input.setText(user_input.getText() + "2");
    }

    @SuppressLint("SetTextI18n")
    public void btn_3(View view) {
        user_input.setText(user_input.getText() + "3");
    }

    @SuppressLint("SetTextI18n")
    public void btn_4(View view) {
        user_input.setText(user_input.getText() + "4");
    }

    @SuppressLint("SetTextI18n")
    public void btn_5(View view) {
        user_input.setText(user_input.getText() + "5");
    }

    @SuppressLint("SetTextI18n")
    public void btn_6(View view) {
        user_input.setText(user_input.getText() + "6");
    }

    @SuppressLint("SetTextI18n")
    public void btn_7(View view) {
        user_input.setText(user_input.getText() + "7");
    }

    @SuppressLint("SetTextI18n")
    public void btn_8(View view) {
        user_input.setText(user_input.getText() + "8");
    }

    @SuppressLint("SetTextI18n")
    public void btn_9(View view) {
        user_input.setText(user_input.getText() + "9");
    }


    public void btn_add(View view) {
        sign = "+";
        val_1 = user_input.getText().toString();
        user_input.setText(null);
        sign_Box.setText("+");
        has_Dot = false;
    }

    public void btn_subtract(View view) {
        sign = "-";
        val_1 = user_input.getText().toString();
        user_input.setText(null);
        sign_Box.setText("-");
        has_Dot = false;
    }

    public void btn_multiply(View view) {
        sign = "*";
        val_1 = user_input.getText().toString();
        user_input.setText(null);
        sign_Box.setText("×");
        has_Dot = false;
    }

    public void btn_divide(View view) {
        sign = "/";
        val_1 = user_input.getText().toString();
        user_input.setText(null);
        sign_Box.setText("÷");
        has_Dot = false;
    }




    public void btn_equal(View view) {
        if (user_input.getText().toString().equals(password)){//eğer kullanıcının girdiği şifre tuşlanıp artıya basılırsa ana uygulamaya atıyor
            Intent intent = new Intent(Calculator_Activity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else{

        if (sign == null) {
            sign_Box.setText("Hata!");
        } else if (user_input.getText().equals("")) {
            sign_Box.setText("Hata!");
        } else if ((sign.equals("+") || sign.equals("-") || sign.equals("*") || sign.equals("/")) && val_1.equals("")) {
            sign_Box.setText("Hata!");
        } else {
            switch (sign) {
                default:
                    break;

                case "+":
                    val_2 = user_input.getText().toString();
                    num1 = Double.parseDouble(val_1);
                    num2 = Double.parseDouble(val_2);
                    answer = num1 + num2;
                    user_input.setText(answer + "");
                    sign = null;
                    sign_Box.setText(null);
                    break;
                case "-":
                    val_2 = user_input.getText().toString();
                    num1 = Double.parseDouble(val_1);
                    num2 = Double.parseDouble(val_2);
                    answer = num1 - num2;
                    user_input.setText(answer + "");
                    sign = null;
                    sign_Box.setText(null);
                    break;
                case "*":
                    val_2 = user_input.getText().toString();
                    num1 = Double.parseDouble(val_1);
                    num2 = Double.parseDouble(val_2);
                    answer = num1 * num2;
                    user_input.setText(answer + "");
                    sign = null;
                    sign_Box.setText(null);
                    break;
                case "/":
                    val_2 = user_input.getText().toString();
                    num1 = Double.parseDouble(val_1);
                    num2 = Double.parseDouble(val_2);
                    answer = num1 / num2;
                    user_input.setText(answer + "");
                    sign = null;
                    sign_Box.setText(null);
                    break;
            }

        }

        }
    }


    public void btn_delete(View view) {
        if (user_input.getText().equals("")) {
            user_input.setText(null);
        } else {
            int len = user_input.getText().length();
            String s = user_input.getText().toString();
            if (s.charAt(len - 1) == '.') {
                has_Dot = false;
                user_input.setText(user_input.getText().subSequence(0, user_input.getText().length() - 1));

            } else {
                user_input.setText(user_input.getText().subSequence(0, user_input.getText().length() - 1));
            }
        }
    }

    public void btn_clear(View view) {

        user_input.setText(null);
        sign_Box.setText(null);
        val_1 = null;
        val_2 = null;
        sign = null;
        has_Dot = false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}