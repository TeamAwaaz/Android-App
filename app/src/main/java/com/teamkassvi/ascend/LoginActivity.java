package com.teamkassvi.ascend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import studios.codelight.smartloginlibrary.SmartLogin;
import studios.codelight.smartloginlibrary.SmartLoginConfig;
import studios.codelight.smartloginlibrary.users.SmartUser;

public class LoginActivity extends AppCompatActivity{

    private Button facebookLoginButton, googleLoginButton, customSigninButton, customSignupButton, logoutButton;
    private EditText emailEditText, passwordEditText;

    SmartUser currentUser;
    SmartLoginConfig config;
    SmartLogin smartLogin;

    private static final int RC_SIGN_IN = 1;
    private static final String TAG ="MAIN ACTIVITY" ;
    private SignInButton mGoogleButton;
//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    Button btnSignIn,btnSignUp;
    LoginDataBaseAdapter loginDataBaseAdapter;
    EditText editTextUserName;
    EditText editTextPassword;
    GoogleSignInAccount account = null;
    protected static final SharedPreferences settings = null;

    public static final String USERNAME = "username";
    public static final String EMAIL_ID = "email_id";
    public static final String USER_IMAGE = "user_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //bindViews();
//        setListeners();

//        config = new SmartLoginConfig(this,this);
//        config.setFacebookAppId(getString(R.string.facebook_app_id));
//        config.setFacebookPermissions(null);
//        config.setGoogleApiClient(null);

        editTextUserName = (EditText) findViewById(R.id.email_edittext);
        editTextPassword = (EditText) findViewById(R.id.password_edittext);


        //--naman---
//        FirebaseApp.initializeApp(this);
//        mAuth=FirebaseAuth.getInstance();
//
//
//        mAuthListener=new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//                if(firebaseAuth.getCurrentUser()!=null){
//                    startActivity(new Intent(LoginActivity.this,GetStartedActivity.class));
//
//                }
//
//
//            }
//        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
//
//
        mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mGoogleButton=(SignInButton) findViewById(R.id.googleButton);
        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });




















        // create a instance of SQLite Database
        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();

        // Get The Refference Of Buttons
        btnSignIn=(Button)findViewById(R.id.buttonSignIN);
        btnSignUp=(Button)findViewById(R.id.buttonSignUP);


        // Configure Google Sign In




        // Set OnClick Listener on SignUp button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                /// Create Intent for SignUpActivity  and Start The Activity
                Intent intentSignUP=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intentSignUP);
            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /// Create Intent for SignUpActivity  and Start The Activity
                // Set On ClickListener

                // get The User name and Password
                String userName = editTextUserName.getText().toString();
                String password = editTextPassword.getText().toString();

                // fetch the Password form database for respective user name
                String storedPassword = loginDataBaseAdapter.getPassword(userName);
                String accountType = loginDataBaseAdapter.getAccountType(userName);
                // check if the Stored password matches with  Password entered by user
                if (password.equals(storedPassword)&&accountType.equals("local")) {
//                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    updateUiWithLocal();
                } else {
                    Toast.makeText(LoginActivity.this, "User Name or Password does not match", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        loginDataBaseAdapter.close();
    }

    // Methos to handleClick Event of Sign In Button


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Want To Exit ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
//                        hideStatusBar();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
//                hideStatusBar();
            }
        });
        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//        automateLogin();
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            account = result.getSignInAccount();
            updateUiWithGoogle();
        } else {
            Toast.makeText(this, "Sign in failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUiWithGoogle() {
        String accountType = "google";
        String userName = account.getDisplayName();
        String emailId = account.getEmail();
        Uri userImageUrl = account.getPhotoUrl();
        Log.d("TAGuser: ", userImageUrl+"");
        loginDataBaseAdapter.insertEntry(userName, null, emailId, accountType, String.valueOf(userImageUrl));

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USERNAME, userName);
        editor.apply();

        Intent intent = new Intent(this, GetStartedActivity.class);
        intent.putExtra(USERNAME, userName);
        intent.putExtra(EMAIL_ID, emailId);
        intent.putExtra(USER_IMAGE,String.valueOf(userImageUrl));
        startActivity(intent);
    }

    private void updateUiWithLocal() {

        String userName = editTextUserName.getText().toString();
        String emailId = loginDataBaseAdapter.getEmailId(userName);
        Uri userImageUrl = null;

        //handle this at logout also.
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USERNAME, userName);
        editor.apply();

        Intent intent = new Intent(this, GetStartedActivity.class);
        intent.putExtra(USERNAME, userName);
        intent.putExtra(EMAIL_ID, emailId);
        intent.putExtra(USER_IMAGE,userImageUrl);
        startActivity(intent);
    }
//
//    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG,"signInWithCredential:onComplete:" + task.isSuccessful());
//
//                        if(!task.isSuccessful())  {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential", task.getException());
//                            Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        // ...
//                    }
//                });
//    }



//    @Override
//    protected void onResume() {
//        super.onResume();
//        currentUser = UserSessionManager.getCurrentUser(this);
//        refreshLayout();
//    }
//
//    @Override
//    public void onLoginSuccess(SmartUser user) {
//        Toast.makeText(this, user.toString(), Toast.LENGTH_SHORT).show();
//        refreshLayout();
//    }
//
//    @Override
//    public void onLoginFailure(SmartLoginException e) {
//        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public SmartUser doCustomLogin() {
//        SmartUser user = new SmartUser();
//        user.setEmail(emailEditText.getText().toString());
//        return user;
//    }
//
//    @Override
//    public SmartUser doCustomSignup() {
//        SmartUser user = new SmartUser();
//        user.setEmail(emailEditText.getText().toString());
//        return user;
//    }
//
//    private void bindViews() {
//        facebookLoginButton = (Button) findViewById(R.id.facebook_login_button);
//        googleLoginButton = (Button) findViewById(R.id.google_login_button);
//        customSigninButton = (Button) findViewById(R.id.custom_signin_button);
//        customSignupButton = (Button) findViewById(R.id.custom_signup_button);
//        emailEditText = (EditText) findViewById(R.id.email_edittext);
//        passwordEditText = (EditText) findViewById(R.id.password_edittext);
//        logoutButton = (Button) findViewById(R.id.logout_button);
//    }
//
//    private void setListeners() {
//        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Perform Facebook login
//                smartLogin = SmartLoginFactory.build(LoginType.Facebook);
//                smartLogin.login(config);
//            }
//        });
//
//        googleLoginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Perform Google login
//                smartLogin = SmartLoginFactory.build(LoginType.Google);
//                smartLogin.login(config);
//
//            }
//        });
//
//        customSigninButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Perform custom sign in
//                smartLogin = SmartLoginFactory.build(LoginType.CustomLogin);
//                smartLogin.login(config);
//                Intent intent = new Intent(LoginActivity.this,StartActivity.class);
//                LoginActivity.this.startActivity(intent);
//            }
//        });
//
//        customSignupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Perform custom sign up
//                smartLogin = SmartLoginFactory.build(LoginType.CustomLogin);
//                smartLogin.signup(config);
//            }
//        });
//
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (currentUser != null) {
//                    if (currentUser instanceof SmartFacebookUser) {
//                        smartLogin = SmartLoginFactory.build(LoginType.Facebook);
//                    } else if(currentUser instanceof SmartGoogleUser) {
//                        smartLogin = SmartLoginFactory.build(LoginType.Google);
//                    } else {
//                        smartLogin = SmartLoginFactory.build(LoginType.CustomLogin);
//                    }
//                    boolean result = smartLogin.logout(LoginActivity.this);
//                    if (result) {
//                        refreshLayout();
//                        Toast.makeText(LoginActivity.this, "User logged out successfully", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//    }
//
//    private void refreshLayout() {
//        currentUser = UserSessionManager.getCurrentUser(this);
//        if (currentUser != null) {
//            Log.d("Smart Login", "Logged in user: " + currentUser.toString());
//            facebookLoginButton.setVisibility(View.GONE);
//            googleLoginButton.setVisibility(View.GONE);
//            customSigninButton.setVisibility(View.GONE);
//            customSignupButton.setVisibility(View.GONE);
//            emailEditText.setVisibility(View.GONE);
//            passwordEditText.setVisibility(View.GONE);
//            logoutButton.setVisibility(View.VISIBLE);
//        } else {
//            facebookLoginButton.setVisibility(View.VISIBLE);
//            googleLoginButton.setVisibility(View.VISIBLE);
//            customSigninButton.setVisibility(View.VISIBLE);
//            customSignupButton.setVisibility(View.VISIBLE);
//            emailEditText.setVisibility(View.VISIBLE);
//            passwordEditText.setVisibility(View.VISIBLE);
//            logoutButton.setVisibility(View.GONE);
//        }
//    }


}
