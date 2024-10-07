package com.alexp.whatstele;

public class Message {
    private String idSender;
    private String idReceiver;
    private String textMessage;

    public Message(String idSender, String idReceiver, String textMessage) {
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.textMessage = textMessage;
    }

    public Message() {
    }

    public String getIdSender() {
        return idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public String getTextMessage() {
        return textMessage;
    }
}
