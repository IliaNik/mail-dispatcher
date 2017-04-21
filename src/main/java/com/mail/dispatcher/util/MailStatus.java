package com.mail.dispatcher.util;

/**
 * @author IliaNik on 20.04.2017.
 */

public enum MailStatus {
    EXPECTS(0, ""),
    PROCESSED(1, ""),
    OK(2,""),
    ERROR(3,"");

    private final int value;
    private final String reasonPhrase;

    MailStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

}
