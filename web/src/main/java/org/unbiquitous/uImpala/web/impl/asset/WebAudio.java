package org.unbiquitous.uImpala.web.impl.asset;

import org.teavm.jso.JSBody;
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
 * policy), so a looping music that fails to start is retried on user
 * gestures (any mouse move/click or key press) until it actually plays.
 *
 * Note: which formats decode varies per browser; Chrome, Edge and Firefox
 * play OGG files, Safari does not.
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
    tryPlay();
    armGestureRetry();
    return new AudioPlayback(element);
  }

  private void setVolume(float volume) {
    this.volume = volume < 0 ? 0 : (volume > 1 ? 1 : volume);
    element.setVolume(this.volume);
  }

  private void tryPlay() {
    if (element.isPaused())
      quietPlay(element);
  }

  /**
   * Browsers block .play() without a user gesture and return a rejected
   * promise; this swallows the rejection so it does not pollute the console
   * (the gesture retry below handles the real start).
   */
  @JSBody(params = "element", script = ""
      + "try { var p = element.play(); if (p && p.catch) p.catch(function (e) {}); }"
      + " catch (e) {}")
  private static native void quietPlay(HTMLAudioElement element);

  /**
   * Registers page gesture listeners that (re)start the playback whenever the
   * browser blocked the initial attempt. They are removed once the playback
   * actually starts, but stay armed while the condition is not met (for
   * example, when the first gesture happens before the audio finishes
   * buffering).
   */
  private void armGestureRetry() {
    EventListener<Event> listener = new EventListener<Event>() {
      public void handleEvent(Event event) {
        if (element.isPaused() && element.getReadyState() >= HTMLAudioElement.HAVE_CURRENT_DATA) {
          element.setLoop(loop);
          element.setVolume(volume);
          quietPlay(element);
          for (String gesture : GESTURE_EVENTS)
            HTMLDocument.current().removeEventListener(gesture, this, false);
        }
      }
    };
    for (String gesture : GESTURE_EVENTS)
      HTMLDocument.current().addEventListener(gesture, listener, false);
  }
}
