package teamawaazdtu.com.awaaz;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import studios.codelight.smartloginlibrary.LoginType;
import studios.codelight.smartloginlibrary.SmartLogin;
import studios.codelight.smartloginlibrary.SmartLoginCallbacks;
import studios.codelight.smartloginlibrary.SmartLoginConfig;
import studios.codelight.smartloginlibrary.SmartLoginFactory;
import studios.codelight.smartloginlibrary.UserSessionManager;
import studios.codelight.smartloginlibrary.users.SmartFacebookUser;
import studios.codelight.smartloginlibrary.users.SmartGoogleUser;
import studios.codelight.smartloginlibrary.users.SmartUser;
import studios.codelight.smartloginlibrary.util.SmartLoginException;

public class LoginActivity extends AppCompatActivity{

    private Button facebookLoginButton, googleLoginButton, customSigninButton, customSignupButton, logoutButton;
    private EditText emailEditText, passwordEditText;

    SmartUser currentUser;
    SmartLoginConfig config;
    SmartLogin smartLogin;

    private static final int RC_SIGN_IN = 1;
    private static final String TAG ="MAIN ACTIVITY" ;
    private SignInButton mGoogleButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    Button btnSignIn,btnSignUp;
    LoginDataBaseAdapter loginDataBaseAdapter;

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


        //--naman---
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();


        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser()!=null){
                    startActivity(new Intent(LoginActivity.this,StartActivity.class));

                }


            }
        };
        mGoogleButton=(SignInButton) findViewById(R.id.googleButton);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


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
                final  EditText editTextUserName = (EditText) findViewById(R.id.email_edittext);
                final  EditText editTextPassword = (EditText) findViewById(R.id.password_edittext);
                // get The User name and Password
                String userName = editTextUserName.getText().toString();
                String password = editTextPassword.getText().toString();

                // fetch the Password form database for respective user name
                String storedPassword = loginDataBaseAdapter.getSinlgeEntry(userName);

                // check if the Stored password matches with  Password entered by user
                if (password.equals(storedPassword)) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this,StartActivity.class);
                    startActivity(i);
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
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG,"signInWithCredential:onComplete:" + task.isSuccessful());





                        if(!task.isSuccessful())  {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


    }



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
