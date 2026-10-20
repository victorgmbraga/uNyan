package org.unbiquitous.uImpala.web.impl.asset;

import org.teavm.jso.JSBody;

/**
 * Logs messages to the appropriate browser console channel (console.error is
 * the most visible in headless test runs).
 */
public final class BrowserConsole {
  private BrowserConsole() {
  }

  @JSBody(params = "message", script = "(console && console.log ? console.log(message) : 0);")
  public static native void log(String message);

  @JSBody(params = "message", script = "(console && console.error ? console.error(message) : console.log(message));")
  public static native void error(String message);
}
