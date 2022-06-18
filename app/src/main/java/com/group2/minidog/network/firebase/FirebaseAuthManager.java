package com.group2.minidog.network.firebase;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.group2.minidog.R;

import java.util.Objects;

public class FirebaseAuthManager implements FirebaseAuthManagerI {

    private final Activity activity;
    private final FirebaseAuthManagerListener listener;
    private final FirebaseAuth firebaseAuth;
    private final SignInClient signInClient;
    private final BeginSignInRequest beginSignInRequest;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;

    public FirebaseAuthManager(Activity activity, FirebaseAuthManagerListener listener){
        this.activity = activity;
        this.listener = listener;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.signInClient = Identity.getSignInClient(activity);
        this.beginSignInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(activity.getResources().getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();
    }

    @Override
    public FirebaseUser checkCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    @Override
    public void showSignInUI() {
        if(showOneTapUI) {
            signInClient.beginSignIn(beginSignInRequest)
                    .addOnSuccessListener(activity, result -> {
                        try {
                            activity.startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            listener.onFail("Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    })
                    .addOnFailureListener(activity, e -> {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        listener.onFail(e.getLocalizedMessage());
                    });
        }else{
            listener.onFail("Previous action was canceled.\n Please try again later.");
        }
    }

    @Override
    public void signInWithGoogle(int requestCode, @Nullable Intent data){
        if (requestCode == REQ_ONE_TAP) {
            //Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                SignInCredential credential = signInClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate with your backend.
                    firebaseAuthWithGoogle(idToken);
                } else {
                    listener.onFail("Couldn't get credential from result");
                }
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case CommonStatusCodes.CANCELED:
                        //One-tap dialog was closed.
                        // Don't re-prompt the user.
                        showOneTapUI = false;
                        break;
                    case CommonStatusCodes.NETWORK_ERROR:
                        //One-tap encountered a network error.
                        listener.onFail("Network error encountered.");
                        // Try again or just ignore.
                        break;
                    default:
                        listener.onFail("Couldn't get credential from result. " + e.getLocalizedMessage());
                        break;
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                ////FirebaseUser user = firebaseAuth.getCurrentUser();
                // Check if email is already exist in Firebase Realtime Database
                // If not, show fullscreen dialog to get their name, gender, etc.
                // Else, go to main activity
                listener.onSuccess();
            } else {
                // If sign in fails, display a message to the user.
                listener.onFail("SignIn failed: " + task.getException());
            }
        });
    }

    @Override
    public void signInEmailAndPassword(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                listener.onSuccess();
            }else{
                listener.onFail(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    @Override
    public void signUpWithEmailAndPassword(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                listener.onSuccess();
            }else{
                listener.onFail(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    @Override
    public void signOut() {
        firebaseAuth.signOut();
        signInClient.signOut();
    }
}
