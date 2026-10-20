package org.unbiquitous.uImpala.web.impl.io;

/**
 * Web speaker: a virtual output device. Playback happens at the HTMLAudio
 * elements (see web asset WebAudio).
 */
public class WebSpeaker extends org.unbiquitous.uImpala.engine.io.Speaker {
  private static class Factory implements org.unbiquitous.uImpala.engine.io.Speaker.Factory {
    public WebSpeaker create() {
      return new WebSpeaker();
    }
  }

  /**
   * Engine's private use.
   */
  public static void initImpl() {
    factory = new Factory();
  }

  public void start() {

  }

  public void stop() {

  }

  protected void update() {

  }

  public void close() {

  }

  public boolean isUpdating() {
    return true;
  }
}
