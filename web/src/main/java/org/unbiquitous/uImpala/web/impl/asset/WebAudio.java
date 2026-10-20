package org.unbiquitous.uImpala.web.impl.asset;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLAudioElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.unbiquitous.uImpala.engine.io.Speaker;

/**
 * Web implementation of the engine asset Audio. Backed by a preloaded
 * HTMLAudioElement. Volume/loop configuration mirrors the engine API.
 *
 * Browsers block playback started without user interaction (autoplay
 * policy), so a looping music that fails to start is retried on the first
 * user gesture (any mouse move/click or key press).
 * @author Pimenta
 *
 */
public class WebAudio implements org.unbiquitous.uImpala.engine.asset.Audio {
  private static final String[] GESTURE_EVENTS = { "mousemove", "mousedown", "keydown" };

  private final HTMLAudioElement element;
  private float volume = 1.0f;
  private boolean loop;

  public WebAudio(HTMLAudioElement element) {
    this.element = element;
  }

  public AudioPlayback play(Speaker speaker, float volume, boolean loop) {
    this.loop = loop;
    setVolume(volume);
    element.setLoop(loop);
    element.play();
    retryOnUserGesture();
    return new AudioPlayback(element);
  }

  private void setVolume(float volume) {
    this.volume = volume < 0 ? 0 : (volume > 1 ? 1 : volume);
    element.setVolume(this.volume);
  }

  /**
   * Registers a one-shot page gesture listener that restarts the playback if
   * the browser blocked it (element paused while a playback was requested).
   */
  private void retryOnUserGesture() {
    EventListener<Event> listener = new EventListener<Event>() {
      public void handleEvent(Event event) {
        if (element.isPaused() && element.getReadyState() >= HTMLAudioElement.HAVE_CURRENT_DATA) {
          element.setLoop(loop);
          element.setVolume(volume);
          element.play();
        }
        for (String gesture : GESTURE_EVENTS)
          HTMLDocument.current().removeEventListener(gesture, this, false);
      }
    };
    for (String gesture : GESTURE_EVENTS)
      HTMLDocument.current().addEventListener(gesture, listener, false);
  }
}
