package com.fanboy.network.message;

import com.fanboy.Constants;

import static com.fanboy.network.message.ServerStatusMessage.Status.DISCONNECT;

public class ServerStatusMessage {
    public enum Status {INFO, DISCONNECT}

    public String text;
    public Status status;

    public ServerStatusMessage(String text, Status status) {
        this.text = text;
        this.status = status;
    }

    public static ServerStatusMessage mismatchVersionMessage(int version) {
        if (version > Constants.PROTOCOL_VERSION) {
            return new ServerStatusMessage("Please downgrade client", DISCONNECT);
        }
        return new ServerStatusMessage("Please update client", DISCONNECT);
    }
}
