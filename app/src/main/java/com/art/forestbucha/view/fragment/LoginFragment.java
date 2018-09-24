package com.art.forestbucha.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.art.forestbucha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private final static String LOG_TAG = LoginFragment.class.getName();

    private EditText etEmail;
    private EditText etPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView tvStatus;
    private TextView tvDetail;
    private TextView tvStatusEmail;

    private Button btnPassword;
    private Button btnSignIn;

    private OnLoginFragmentListener mListener;

    public static LoginFragment newInstance(){
        //TODO можно использовать bundle здесь

        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmint_login, container, false);

        //EditTexts
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);

        //buttons
        btnSignIn = view.findViewById(R.id.emailSignInButton);
        btnSignIn.setOnClickListener(this);

        view.findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        view.findViewById(R.id.signOutButton).setOnClickListener(this);
        view.findViewById(R.id.verifyEmailButton).setOnClickListener(this);

        //TextViews
        tvStatus = view.findViewById(R.id.status);
        tvDetail = view.findViewById(R.id.detail);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                }
                else {

                }
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentListener) {
            mListener = (OnLoginFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implements OnLoginFragmentListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        //TODO
        mListener.hideProgressDialog();
        if (user != null) {
            tvStatus.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(),user.isEmailVerified()));
            tvDetail.setText(getString(R.string.firebase_status_fmt, user.getUid()));

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAuth){
            signIn(etEmail.getText().toString(), etPassword.getText().toString());
        } else if(v.getId() == R.id.btnRegistr){
            registration(etEmail.getText().toString(), etPassword.getText().toString());
        }
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Авторизация успещна", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getActivity(), "Авторизация не удалась", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void registration(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Регистрация успещна", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getActivity(), "Регистрация не удалась", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
