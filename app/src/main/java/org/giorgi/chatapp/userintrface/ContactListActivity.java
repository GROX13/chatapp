package org.giorgi.chatapp.userintrface;

import org.giorgi.chatapp.model.Message;
import org.giorgi.chatapp.transport.ChatEventListener;

public class ContactListActivity extends CustomActivity implements ChatEventListener {

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
        // TODO Auto-generated method stub

    }


}
