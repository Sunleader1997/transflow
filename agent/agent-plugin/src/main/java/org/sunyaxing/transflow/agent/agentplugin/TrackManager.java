package org.sunyaxing.transflow.agent.agentplugin;

import java.util.Stack;

public class TrackManager {

    private static final ThreadLocal<Stack<String>> track = new ThreadLocal<Stack<String>>();

    private static String createSpan() {
        Stack<String> stack = track.get();
        if (stack == null) {
            stack = new Stack<>();
            track.set(stack);
        }
        String linkId;
        if (stack.isEmpty()) {
            linkId = TrackContext.getLinkId();
            if (linkId == null) {
                linkId = "nvl";
                TrackContext.setLinkId(linkId);
            }
        } else {
            linkId = stack.peek();
            TrackContext.setLinkId(linkId);
        }
        return linkId;
    }

    public static String createEntrySpan() {
        String span = createSpan();
        Stack<String> stack = track.get();
        stack.push(span);
        return span;
    }


    public static String getExitSpan() {
        Stack<String> stack = track.get();
        if (stack == null || stack.isEmpty()) {
            TrackContext.clear();
            return null;
        }
        return stack.pop();
    }

    public static String getCurrentSpan() {
        Stack<String> stack = track.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }


}
 