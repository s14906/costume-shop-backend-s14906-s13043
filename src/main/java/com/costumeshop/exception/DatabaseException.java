package com.costumeshop.exception;

import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.utils.CodeMessageUtils;

public class DatabaseException extends RuntimeException  {

    public DatabaseException(ErrorCode errorCode, String option) {
        super(errorCode.name() + ": " + errorCode.getMessage() + option);
    }

    public DatabaseException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public DatabaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.name() + ": " + errorCode.getMessage() + cause.getMessage(), cause);
    }

    public DatabaseException(ErrorCode errorCode, Integer option) {
        super(errorCode.name() + ": " + CodeMessageUtils.getMessage(errorCode.getMessage(), option));
    }

    public DatabaseException(ErrorCode errorCode) {
        super(errorCode.name() + ": " + errorCode.getMessage());
    }

}
