package org.giorgi.chatapp.app;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import org.giorgi.chatapp.asynchtasks.URLContactListDownloaderTask;
import org.giorgi.chatapp.database.MyDBHelper;
import org.giorgi.chatapp.model.Contact;
import org.giorgi.chatapp.model.Message;
import org.giorgi.chatapp.network.NetworkReceiver;
import org.giorgi.chatapp.parser.ContactListJsonParser;
import org.giorgi.chatapp.transport.ChatEventListsner;
import org.giorgi.chatapp.transport.ChatTransport;
import org.giorgi.chatapp.transport.NetworkEventListener;
import org.giorgi.chatapp.transport.TestChatTransport;

import java.util.ArrayList;
import java.util.List;

public class App extends Application implements NetworkEventListener, ChatEventListsner {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String URL =
            "https://dl.dropboxusercontent.com/u/28030891/FreeUni/Android/assinments/contacts.json";
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    // The user's current network preference setting.
    public static String sPref = null;
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    private static ChatTransport chatTransport;
    private static ArrayList<Contact> contacts;
    private static MyDBHelper dbHelper;
    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();

    public static ChatTransport getChatTransport() {
        return chatTransport;
    }

    public static ArrayList<Contact> getContactList() {
        return contacts;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initApp();
    }

    private void initApp() {
        chatTransport = new TestChatTransport();
        chatTransport.addChatEventListsner(this);
        // Set up contact list download for my application
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        updateConnectedFlags();

        // Only loads the page if refreshDisplay is true. Otherwise, keeps previous
        // display. For example, if the user has set "Wi-Fi only" in prefs and the
        // device loses its Wi-Fi connection midway through the user using the app,
        // you don't want to refresh the display--this would force the display of
        // an error page instead of stackoverflow.com content.
        if (refreshDisplay) {
            load();
        }
    }

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    // Uses AsyncTask subclass to download the XML feed from stackoverflow.com.
    // This avoids UI lock up. To prevent network operations from
    // causing a delay that results in a poor user experience, always perform
    // network operations on a separate thread from the UI.
    private void load() {
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass
            new URLContactListDownloaderTask
                    (URL, this, new ContactListJsonParser()).execute();
        } else {
            showError();
        }
    }

    // Displays an error if the app is unable to load content.
    private void showError() {
        Log.e("App", "Error occurred while downloading contact list!");
    }

    @Override
    public void onIncomingMsg(Message m) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOutgoingMsg(Message m) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String contactId, boolean isOnline) {
        // TODO: Auto-generated method stub

    }


    @Override
    @SuppressWarnings("unchecked")
    public void onContactListDownloaded(List<Contact> contacts) {
        App.contacts = (ArrayList<Contact>) contacts;
    }

    @Override
    public void onAvatarDownloaded(byte[] imgData, String contactId) {
        // TODO: Auto-generated method stub

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        // TODO: Auto-generated method stub

    }

}