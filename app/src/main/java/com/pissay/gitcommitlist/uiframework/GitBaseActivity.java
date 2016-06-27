package com.pissay.gitcommitlist.uiframework;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pissay.gitcommitlist.R;
import com.pissay.gitcommitlist.interfaces.ServerCallback;
import com.pissay.gitcommitlist.macros.Macros;
import com.pissay.gitcommitlist.network.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by S.K. Pissay on 20/3/16.
 */
public abstract class GitBaseActivity extends AppCompatActivity implements View.OnClickListener, ServerCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int DISPLAY_ERROR_ALERT_ID = 1;
    public static final int DISPLAY_PROGRESS_BAR_ID = 2;
    public static final int DISPLAY_ANIM_DIAL_ID = 3;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
//    public static final String GCM_PROJECT_NUMBER = "159301724926";

    /*TODO enter gcm project no. down*/
    public static final String GCM_PROJECT_NUMBER = "";

    public boolean pTakePicture;
    public boolean pSavePicture;
    public String m_cImageGUID;
    public String m_cPatientID;
    public String m_cPictureID;
    public String m_cCaseID;
    public String m_cFileGUID;

    protected int m_cDialogID;
    protected AlertDialog m_cObjDialog;
    protected ProgressDialog m_cObjProgressBar;
    protected Dialog m_cObjAnimDialog;
    public UIHandler m_cObjUIHandler;
    public SwipeRefreshLayout swipeView;

    public HashMap<Integer, String> mFragmentTags = new HashMap<Integer, String>();

    public String m_cJsonSpecialityObject;
    public HashMap<String, HashMap<String, Object>> m_cJsonDoctorProfileObject;
    public HashMap<String, ArrayList<String>> m_cJsonAppSlot;

    // Drawer layout and its propeties
    public DrawerLayout m_cObjSliderMenu;
    public FrameLayout m_cContainFragment;
    public FragmentManager m_cObjFragmentManager;

    public GitFragmentBaseClass m_cObjFragmentBase;

    public boolean ISSWIPE;

    protected abstract void handleUIMessage(Message pObjMessage);

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(pSavedInstance);
        m_cObjUIHandler = new UIHandler();

        // Set the Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        // REGISTERING & GETTING PUSH NOTIFICATION
        getRegId();
    }

    public void getRegId() {
        /*new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                GoogleCloudMessaging gcm = null;
                String regid = Macros.getGCMRegId(GitBaseActivity.this);
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(GitBaseActivity.this);
                    }
                    regid = gcm.register(GCM_PROJECT_NUMBER);
                    *//*TODO send new gcm session id below*//*
                    *//*RequestManager.getInstance(GitBaseActivity.this).updateGCM(Macros.getSessionId(GitBaseActivity.this),
                            regid, GitBaseActivity.this);*//*
                    Macros.saveGCMRegId(getApplicationContext(), regid);

                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
//                etRegId.setText(msg + "\n");
            }
        }.execute(null, null, null);*/
    }


    //ui controls

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //api controls

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void complete(int code) {

    }

    @Override
    public void onAPIResponse(Object response, String apiMethod) {
        Intent lObjIntent;
        if (apiMethod == Constants.AUTHLOGOUT) {
            hideDialog();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod) {
        if (apiMethod == Constants.AUTHLOGOUT) {
            hideDialog();
        }
        if (error.networkResponse == null) {
            Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
            hideDialog();
        }
    }

    public final class UIHandler extends Handler {
        @Override
        public void handleMessage(Message pObjMessage) {
            if (null != m_cObjFragmentBase && m_cObjFragmentBase.m_cIsActivityAttached) {
                m_cObjFragmentBase.handleUIhandler(pObjMessage);
            }
            handleUIMessage(pObjMessage);
        }
    }

    @Override
    protected Dialog onCreateDialog(int pID) {
        Dialog lRetVal = null;
        if (pID == DISPLAY_PROGRESS_BAR_ID) {
            lRetVal = m_cObjProgressBar;
        } else if (pID == DISPLAY_ANIM_DIAL_ID) {
            lRetVal = m_cObjAnimDialog;
        } else {
            lRetVal = m_cObjDialog;
        }
        m_cDialogID = pID;
        return lRetVal;
    }

    @SuppressWarnings("deprecation")
    public void hideDialog() {
        try {
            if (null != m_cObjDialog) {
                m_cObjDialog.dismiss();
                removeDialog(m_cDialogID);
            }
            if (null != m_cObjProgressBar) {
                m_cObjProgressBar.dismiss();
                removeDialog(m_cDialogID);
            }
            if (null != m_cObjAnimDialog) {
                m_cObjAnimDialog.dismiss();
                removeDialog(m_cDialogID);
            }
            m_cObjProgressBar = null;
            m_cObjDialog = null;
            m_cObjAnimDialog = null;
            m_cDialogID = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayToast(String message){
        Toast toast= Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public void createProgressDialog() {
        hideDialog();
        m_cObjProgressBar = new ProgressDialog(this);
        try {
            m_cObjProgressBar.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
        m_cObjProgressBar.setCancelable(false);
        m_cObjProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        m_cObjProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_cObjProgressBar.setContentView(R.layout.progressdialog_paging);
        m_cDialogID = DISPLAY_PROGRESS_BAR_ID;
        try {
            m_cObjProgressBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayPagingProgressDialog() {
        hideDialog();
        m_cObjProgressBar = new ProgressDialog(this);
        try {
            m_cObjProgressBar.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
        m_cObjProgressBar.setCancelable(false);
        m_cObjProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        m_cObjProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_cObjProgressBar.setContentView(R.layout.progressdialog_paging);
        m_cDialogID = DISPLAY_PROGRESS_BAR_ID;
        try {
            m_cObjProgressBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayProgressBar(int pStrResID, String pErrorString) {
        createProgressDialog();
    }

    public void displaySpinnerProgressBar() {
        hideDialog();
        m_cObjProgressBar = new ProgressDialog(this/*, R.style.CustomDialogTheme*/);
        //m_cObjProgressBar.addContentView(new ProgressBar(this), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        m_cObjProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_cObjProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        m_cObjProgressBar.setCancelable(false);
        m_cDialogID = DISPLAY_PROGRESS_BAR_ID;
        try {
            m_cObjProgressBar.show();
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("deprecation")
    public void displayErrorAlert(int pStrResID, String pErrorString) {
        displayErrorAlert(pStrResID, pErrorString, false);
    }

    public void displayErrorAlert(int pStrResID, String pErrorString, final boolean pSendMsg) {
        hideDialog();
        String lMessage = (null != pErrorString) ? pErrorString : this.getResources().getString(pStrResID);
        try {
            AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
            lObjBuilder.setCancelable(false);
            lObjBuilder.setTitle(getString(R.string.app_name));
            lObjBuilder.setMessage(lMessage);
            lObjBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface pObjDialog, int id) {
                    hideDialog();
                    if (pSendMsg) {
                        m_cObjUIHandler.sendEmptyMessage(1);
                    }
                }
            });
            m_cObjDialog = lObjBuilder.create();
            m_cObjDialog.show();
            showDialog(DISPLAY_ERROR_ALERT_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayAlert(int pStrResID, String pTitle, String pMessage) {
        hideDialog();
        String lMessage = (null != pMessage) ? pMessage : getResources().getString(pStrResID);
        try {
            AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
            lObjBuilder.setCancelable(false);
            lObjBuilder.setTitle(pTitle);
            lObjBuilder.setMessage(lMessage);
            lObjBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface pObjDialog, int id) {
                    hideDialog();
                }
            });
            m_cObjDialog = lObjBuilder.create();
            m_cObjDialog.show();
            showDialog(DISPLAY_ERROR_ALERT_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayYesNoAlert(int pStrResID, String pErrorString) {
        hideDialog();
        String lMessage = (null != pErrorString) ? pErrorString : getResources().getString(pStrResID);
        AlertDialog.Builder lObjBuilder = new AlertDialog.Builder(this);
        lObjBuilder.setCancelable(false);
        lObjBuilder.setTitle(getString(R.string.app_name));
        lObjBuilder.setMessage(lMessage);
        lObjBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface pObjDialog, int id) {
                hideDialog();
                m_cObjUIHandler.sendEmptyMessage(Macros.YES_MESSAGE);
            }
        });

        lObjBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideDialog();
                m_cObjUIHandler.sendEmptyMessage(Macros.NO_MESSAGE);
            }
        });
        m_cObjDialog = lObjBuilder.create();
        m_cObjDialog.show();
        showDialog(DISPLAY_ERROR_ALERT_ID);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (pTakePicture == true && pSavePicture == false) {
//            deleteImage();
//        }
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }


    public static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public boolean isEmailValid(String pEmail) {
        boolean lRetVal = true;
        if (pEmail.trim().length() > 0) {
            CharSequence inputStr = pEmail;
            Pattern pattern = Pattern.compile(Macros.EMAIL_PATTERN_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches())
                lRetVal = true;
            else
                lRetVal = false;
        }
        return lRetVal;
    }

    public boolean isPhoneNoValid(String pPhone) {
        boolean lRetVal = true;
        if (pPhone.trim().length() > 0) {
            CharSequence inputStr = pPhone;
            Pattern pattern = Pattern.compile(Macros.PHONE_PATTERN_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches())
                lRetVal = true;
            else
                lRetVal = false;
        }
        return lRetVal;
    }

    public boolean isAlphaNumeric(String pAlphaNum) {
        boolean lRetVal = true;
        if (pAlphaNum.trim().length() > 0) {
            CharSequence inputStr = pAlphaNum;
            Pattern pattern = Pattern.compile(Macros.ALPHA_NUMERIC_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches())
                lRetVal = true;
            else
                lRetVal = false;
        }
        return lRetVal;
    }

    public boolean isAlphabetic(String pAlpha) {
        boolean lRetVal = true;
        if (pAlpha.trim().length() > 0) {
            CharSequence inputStr = pAlpha;
            Pattern pattern = Pattern.compile(Macros.ALPHABETIC_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches())
                lRetVal = true;
            else
                lRetVal = false;
        }
        return lRetVal;
    }

    public boolean copyFile(String from, String to) {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File source = new File(from);
                File destination = new File(to);
                verifyStoragePermissions(this);
                if (source.exists()) {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(destination).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onClick(View v) {
        Intent lObjIntent;
        switch (v.getId()) {
        /*TODO below code for slider id's*/
        }
    }

}
