package org.unbiquitous.uImpala.web.impl.time;

import org.teavm.jso.JSBody;

/**
 * Web implementation of engine.time.Time. Uses the browser monotonic clock
 * (performance.now) instead of System.currentTimeMillis().
 * @author Pimenta
 *
 */
public class WebTime extends org.unbiquitous.uImpala.engine.time.Time {
  /**
   * Engine's private use.
   */
  public static void initImpl() {
    time = new WebTime();
  }

  protected long getTime() {
    return (long)currentMillis();
  }

  @JSBody(params = {}, script = "return window.performance ? window.performance.now() : Date.now();")
  private static native double currentMillis();
}
