package org.sunyaxing.transflow.agent.agentplugin;

public class TrackContext {

    private static final ThreadLocal<String> trackLocal = new ThreadLocal<String>();

    public static void clear() {
        trackLocal.remove();
    }

    public static String getLinkId() {
        return trackLocal.get();
    }

    public static void setLinkId(String linkId) {
        trackLocal.set(linkId);
    }

}