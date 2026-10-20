package org.unbiquitous.uImpala.web.impl.asset;

import org.teavm.jso.dom.html.HTMLAudioElement;

/**
 * Web implementation of the engine asset AudioPlayback.
 * @author Pimenta
 *
 */
public class AudioPlayback implements org.unbiquitous.uImpala.engine.asset.AudioPlayback {
  private final HTMLAudioElement element;

  public AudioPlayback(HTMLAudioElement element) {
    this.element = element;
  }

  public State state() {
    if (!element.isPaused())
      return State.PLAYING;
    return element.getCurrentTime() > 0 ? State.PAUSED : State.STOPPED;
  }

  public void pause() {
    if (!element.isPaused())
      element.pause();
  }

  public void resume() {
    if (element.isPaused())
      element.play();
  }

  public void stop() {
    element.pause();
    element.setCurrentTime(0);
  }

  public void volume(float volume) {
    element.setVolume(volume < 0 ? 0 : (volume > 1 ? 1 : volume));
  }
}
