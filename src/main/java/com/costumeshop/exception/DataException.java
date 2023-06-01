package com.costumeshop.exception;

import com.costumeshop.info.codes.ErrorCode;
import com.costumeshop.info.utils.CodeMessageUtils;

public class DataException extends RuntimeException  {

    public DataException(ErrorCode errorCode, String option) {
        super(errorCode.name() + ": " + errorCode.getMessage() + option);
    }

    public DataException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public DataException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.name() + ": " + errorCode.getMessage() + cause.getMessage(), cause);
    }

    public DataException(ErrorCode errorCode, Integer option) {
        super(errorCode.name() + ": " + CodeMessageUtils.getMessage(errorCode.getMessage(), option));
    }

    public DataException(ErrorCode errorCode) {
        super(errorCode.name() + ": " + errorCode.getMessage());
    }

}
