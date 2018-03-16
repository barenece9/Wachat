package com.wachat.xmpp;//package com.wachat.xmpp;
//
//import org.jivesoftware.smack.AbstractXMPPConnection;
//import org.jivesoftware.smack.tcp.XMPPTCPConnection;
//import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
//
///**
// * Created by Ashish on 22-04-2016.
// */
//public class XmppManager {
//
//    private static volatile XmppManager Instance = null;
//
//    private AbstractXMPPConnection mConnection;
//    public static XmppManager getInstance() {
//        XmppManager localInstance = Instance;
//        if (localInstance == null) {
//            synchronized (XmppManager.class) {
//                localInstance = Instance;
//                if (localInstance == null) {
//                    Instance = localInstance = new XmppManager();
//                }
//            }
//        }
//        return localInstance;
//    }
//
//    public AbstractXMPPConnection getActiveConnection(){
//        return mConnection;
//    }
//    public AbstractXMPPConnection initConnection(XMPPTCPConnectionConfiguration config){
//        if(mConnection!=null){
//
//        }
//
//        mConnection = new XMPPTCPConnection(config);
//        return mConnection;
//    }
//
//    public void nullify() {
//        mConnection = null;
//    }
//}
