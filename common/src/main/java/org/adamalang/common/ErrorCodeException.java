/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

/** an error happened related to an error code that we can present to the public */
public class ErrorCodeException extends Exception {
  public final int code;

  public ErrorCodeException(final int code) {
    super("code:" + code);
    this.code = code;
  }

  public ErrorCodeException(final int code, String message) {
    super(message);
    this.code = code;
  }

  public ErrorCodeException(final int code, final Throwable cause) {
    super("code:" + code + ":" + cause.getMessage(), cause);
    this.code = code;
  }

  public static ErrorCodeException detectOrWrap(int code, Throwable cause, ExceptionLogger logger) {
    if (cause instanceof RuntimeException) {
      if (cause.getCause() instanceof ErrorCodeException) {
        return (ErrorCodeException) (cause.getCause());
      }
    }
    if (cause instanceof ErrorCodeException) {
      return (ErrorCodeException) cause;
    }
    if (logger != null) {
      logger.convertedToErrorCode(cause, code);
    }
    return new ErrorCodeException(code, cause);
  }
}
