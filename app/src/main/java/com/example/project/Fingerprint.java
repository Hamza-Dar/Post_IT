package com.example.project;

import android.hardware.fingerprint.FingerprintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.multidots.fingerprintauth.AuthErrorCodes;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;

public class Fingerprint extends AppCompatActivity implements FingerPrintAuthCallback {

    FingerPrintAuthHelper mFingerPrintAuthHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //fingerprint
        mFingerPrintAuthHelper.startAuth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFingerPrintAuthHelper.stopAuth();
    }



    @Override
    public void onNoFingerPrintHardwareFound() {
        Toast.makeText(getApplicationContext(), "No FingerPrint Sensor Found", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBelowMarshmallow() {
        Toast.makeText(getApplicationContext(), "Your device doesn't support fingerprint authentication", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNoFingerPrintRegistered() {
        //FingerPrintUtils.openSecuritySettings(MainActivity.this);
    }
    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        Toast.makeText(getApplicationContext(), "Succeeded", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {
        switch (errorCode) {
            case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                Toast.makeText(getApplicationContext(), "Cannot Recognize Fingerprint", Toast.LENGTH_SHORT).show();
                break;
            case AuthErrorCodes.NON_RECOVERABLE_ERROR:
                //Toast.makeText(getApplicationContext(), "Non-Recoverable Fingerprint Error", Toast.LENGTH_SHORT).show();
                break;
            case AuthErrorCodes.RECOVERABLE_ERROR:
                break;
        }
    }

}
