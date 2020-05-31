package com.talagasoft.gojek;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.talagasoft.gojek.model.HttpXml;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG="LoginActivity";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mNamaView;
    private EditText mPasswordView;
    private EditText mHandphoneView;
    private View mProgressView;
    private View mLoginFormView;
    SharedPreferences sharedPreferences=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



         sharedPreferences = getSharedPreferences(
                getResources().getString(R.string.setting), Context.MODE_WORLD_READABLE);

        // Set up the login form.
       //getSupportActionBar().hide();
        mHandphoneView = (EditText) findViewById(R.id.handphone);

        mNamaView = (AutoCompleteTextView) findViewById(R.id.nama);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress2);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNamaView.setError(null);
        mPasswordView.setError(null);
        mHandphoneView.setError(null);

        // Store values at the time of the login attempt.
        String nama = mNamaView.getText().toString();
        String password = mPasswordView.getText().toString();
        String handphone = mHandphoneView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(handphone)  && !isHandphoneValid(handphone) ) {
            mHandphoneView.setError("Nomor handphone terlalu pendek");
            focusView = mHandphoneView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(nama, password,handphone);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    private boolean isHandphoneValid(String hp) {
        //TODO: Replace this with your own logic
        return hp.length() > 8;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mNama;
        private final String mPassword;
        private final String mHandphone;

        UserLoginTask(String nama, String password, String hp) {
            mNama = nama;
            mPassword = password;
            mHandphone=hp;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
                String mUrl=getResources().getString(R.string.url_source)+"login2.php?username=" +
                        mNama+"&password="+mPassword+"&handphone="+mHandphone;

                HttpXml web=new HttpXml();
                StringBuilder doc=web.GetUrlData(mUrl);
                if(doc != null) {
                    if(doc.toString().contains("success")) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        //Adding values to editor
                        editor.putBoolean("logged_in", true);
                        editor.putString("no_hp", mHandphone);
                        editor.putString("nama", mNama);
                        editor.putString("alamat",web.getKey("alamat"));
                        editor.putString("deposit",web.getKey("deposit"));

                        //Saving values to editor
                        editor.commit();

                        Log.d(TAG, "LoginActivity is " + doc.toString());

                        return true;
                    }
                }

            } catch (InterruptedException e) {
                return false;
            }

            // TODO: register the new account here.

            return registerNewAccount();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            showProgress(false);

            if (success) {
                LinearLayout area_login=(LinearLayout) findViewById(R.id.email_login_form);
                area_login.setVisibility(View.INVISIBLE);

                startActivity(new Intent("MainActivity"));

                finish();

            } else {
                mPasswordView.setError("Password salah atau user sudah ada !");
                mPasswordView.requestFocus();
            }

        }


        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private boolean registerNewAccount() {
        String vUser= String.valueOf(mNamaView.getText());
        String vPass= String.valueOf(mPasswordView.getText());
        String vHp= String.valueOf(mHandphoneView.getText());

        String mUrl=getResources().getString(R.string.url_source)+"user_add.php?username=" +
                vUser+"&password="+vPass+"&handphone="+vHp;

        HttpXml web=new HttpXml();
        StringBuilder doc=web.GetUrlData(mUrl);
        if(doc  != null) {
            if(doc.toString().contains("success")) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                //Adding values to editor
                editor.putBoolean("logged_in", true);
                editor.putString("no_hp", vHp);
                editor.putString("nama", vUser);

                //Saving values to editor
                editor.commit();

                Log.d(TAG, "registerNewAccount is " + doc.toString());


                return true;

            }
        } else {
            Log.d(TAG,"No internet connection !");
        }
        return false;
    }
}

