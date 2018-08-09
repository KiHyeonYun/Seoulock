package com.example.yun.seoulock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.yun.seoulock.air.AirData;
import com.example.yun.seoulock.eve.EveData;
import com.example.yun.seoulock.weat.WeatData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    final static String _AIR = "AIR_DATA";
    final static String _WEAT = "WEAT_DATA";
    final static String _EVE = "EVE_DATA";

    private SharedPreferences mPref;
    private SharedPreferences.Editor mPrefEdit;

    private Switch autoLogin;
    private   EditText email_et, password_et;
    private  Button login_button, signin_button,re_verify;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;

    ArrayList<AirData> m_airData = new ArrayList<AirData>();
    ArrayList<WeatData> m_weatData = new ArrayList<WeatData>();
    ArrayList<EveData> m_eveData = new ArrayList<EveData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autoLogin = findViewById(R.id.auto_login);
        email_et = findViewById(R.id.LoginActivity_email);
        password_et = findViewById(R.id.LoginActivity_password);
        login_button = findViewById(R.id.LoginActivity_button_login);
        signin_button = findViewById(R.id.LoginActivity_button_signup);
        re_verify = findViewById(R.id.loginactivity_button_REverity);

        re_verify.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mPref = getSharedPreferences("setting", 0);
        mPrefEdit = mPref.edit();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            Log.e("CCC", "LOGIN_START");
            m_airData = bundle.getParcelableArrayList(_AIR);
            Log.e("CCC", "LOGIN_" + m_airData.get(0).getA_GRADE());
            m_weatData = bundle.getParcelableArrayList(_WEAT);
            Log.e("CCC", "LOGIN_" + m_weatData.get(0).getW_TEMP());
            m_eveData = bundle.getParcelableArrayList(_EVE);
            Log.e("CCC", "LOGIN_" + m_eveData.get(0).getE_TITLE());
        }


        //회원가입 리스너
        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
        //로그인 리스너
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loginEvent();
            }
        });

        //메일인증이 완료 x이면 재전송 버튼을 활성화시켜줌.
       re_verify.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               sendEmailVerification();
           }
       });

       //자동로그인 버튼의 상태가 바뀌면 동작하는 리스너
        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mPrefEdit.putString("switch", "1");
                    mPrefEdit.commit();
                    Log.d("switch", "on");
                }
                else {
                    mPrefEdit.putString("switch", "0");
                    mPrefEdit.commit();
                    Log.d("switch", "off");
                }
            }
        });
        //자동로그인
        autologin();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //로그인 부분
                if(user !=null){
                    if (user.isEmailVerified() == true) {
                        if(autoLogin.isChecked()){
                            Toast.makeText(getApplicationContext(),user.getDisplayName()+"님 환영합니다.",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            Bundle bundle = new Bundle();

                            if( !m_airData.isEmpty() && !m_weatData.isEmpty() && !m_eveData.isEmpty()) {
                                bundle.putParcelableArrayList(_AIR, m_airData);
                                bundle.putParcelableArrayList(_WEAT, m_weatData);
                                bundle.putParcelableArrayList(_EVE, m_eveData);
                                intent.putExtras(bundle);
                            }
                            startActivity(intent);

                            finish();
                        }
                       }
                    else {
                    }
                }
            }
        };
    }

    //로그인 이벤트 메소드
    void loginEvent(){
        String email = email_et.getText().toString();
        String pass = password_et.getText().toString();

        if(!TextUtils.isEmpty(email)) {

            if(!TextUtils.isEmpty(pass)) {
                    //로그인 메소드, 아이디와 비밀번호를 firebase에 전달하고 가입이 되어있다면 로그인 시켜주는 메소드
                mAuth.signInWithEmailAndPassword(email_et.getText().toString(), password_et.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                                else if (task.isSuccessful()) {
                                     user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        if (user.isEmailVerified() == true) {
                                            Toast.makeText(getApplicationContext(),user.getDisplayName()+"님 환영합니다.",Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            Bundle bundle = new Bundle();

                                            if( !m_airData.isEmpty() && !m_weatData.isEmpty() && !m_eveData.isEmpty()) {
                                                bundle.putParcelableArrayList(_AIR, m_airData);
                                                bundle.putParcelableArrayList(_WEAT, m_weatData);
                                                bundle.putParcelableArrayList(_EVE, m_eveData);
                                                intent.putExtras(bundle);
                                            }
                                            startActivity(intent);
                                            finish();

                                        } else if (user.isEmailVerified() == false) {
                                            Toast.makeText(LoginActivity.this, "E-mail 인증해주세요!", Toast.LENGTH_SHORT).show();
                                            re_verify.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }
                        });
            }
            else {
                Toast.makeText(LoginActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        else {
            Toast.makeText(LoginActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        //로그인 인터페이스 리스너 생성
    }

    //인증메일 재전송 메소드
    private void sendEmailVerification() {

        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "인증메일 재전송 완료",
                            Toast.LENGTH_SHORT).show();
                    re_verify.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(LoginActivity.this,
                            "인증메일 전송 실패.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //스타트하면
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }
    //정지하면
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
    //자동로그인값 확인
    private void autologin(){
        if(mPref.getString("switch", "").equals("1")) {
            autoLogin.setChecked(true);
            Log.d("setting", "true");
        } else {
            autoLogin.setChecked(false);
            Log.d("setting", "false");
        }
    }
}
