package com.art.forestbucha.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public final static String USER_ID = "userId";

    private EditText etEmail;
    private EditText etPassword;
    private FirebaseAuth mAuth;
    private TextView tvStatus;
    private TextView tvDetail;

    private OnLoginFragmentListener mListener;
    private SharedPreferences preferences;

    public static LoginFragment newInstance() {
        // можно использовать bundle здесь
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmint_login, container, false);

        //EditTexts
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);

        //buttons
        view.findViewById(R.id.emailSignInButton).setOnClickListener(this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    private void updateUI(FirebaseUser user) {

        mListener.hideProgressDialog();

        if (user != null) {
            tvStatus.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            tvDetail.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            getView().findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
            getView().findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
            getView().findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
        }
        else {
          tvStatus.setText(R.string.signed_out);
          tvDetail.setText(null);

          getView().findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
          getView().findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
          getView().findViewById(R.id.signedInButtons).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
      int i = v.getId();
        //TODO
      switch (i) {
          case R.id.emailCreateAccountButton:
              createAccount(etEmail.getText().toString(), etPassword.getText().toString());
              break;

          case R.id.emailSignInButton:
              signIn(etEmail.getText().toString(), etPassword.getText().toString());
              break;

          case R.id.signOutButton:
              signOut();
              break;

          case R.id.verifyEmailButton:
              sendEmailVerification();
              break;
      }
    }

    public void createAccount(String email, String password){
        Log.i(LOG_TAG, "create account: " + email);
        if (!validateForm()) {
            return;
        }
        mListener.showProgressDialog();
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(),
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            mListener.startMainFragment();
                        }
                        else {
                            Log.w(LOG_TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                            // [START_EXCLUDE]
                            mListener.hideProgressDialog();
                        }
                    }
                });
        // [END create_user_with_email]
    }

    public void signIn(String email, String password) {
        Log.d(LOG_TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        mListener.showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getActivity(), "Добро пожаловать, " + user.getEmail(),
                                    Toast.LENGTH_LONG).show();
                            String userId = user.getUid();
                            SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(USER_ID, userId);
                            editor.apply();
                            mListener.startMainFragment();
                        }
                        else {
                            Log.d(LOG_TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        if (!task.isSuccessful()) {
                            tvStatus.setText(R.string.auth_failed);
                        }
                        mListener.hideProgressDialog();
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_ID, "");
        editor.apply();
        updateUI(null);
    }

    public void sendEmailVerification() {
        // Disable button
        getView().findViewById(R.id.verifyEmailButton).setEnabled(false);
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        getView().findViewById(R.id.verifyEmailButton).setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(),"Verification email sent to " +
                                            user.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                           Log.d(LOG_TAG, "sendEmailVerification ", task.getException());
                            Toast.makeText(getActivity(),"Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //-------------------------------------------------------------------------------------------
    //private methods
    private boolean validateForm() {
        boolean valid = true;

        String email = etEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }
}
