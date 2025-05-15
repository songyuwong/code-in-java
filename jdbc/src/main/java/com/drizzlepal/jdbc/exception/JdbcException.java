package com.drizzlepal.jdbc.exception;

public abstract class JdbcException extends Exception {

    public JdbcException(String msg) {
        super(msg);
    }

}
