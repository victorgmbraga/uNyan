package org.unbiquitous.uImpala.engine.io;

import java.util.HashSet;

import org.unbiquitous.uImpala.util.observer.Observation;

/**
 * Web version of the keyboards manager: local keyboard sources only. The
 * original also managed networked keyboards discovered through uOS drivers,
 * which cannot run inside a browser.
 *
 * @author Pimenta
 *
 */
public class KeyboardManager implements InputManager {
  public IOResource alloc() {
    return null;
  }

  public boolean free(IOResource rsc) {
    return false;
  }

  public void update() {
    for (KeyboardSource ks : screenKeyboards)
      ks.update();
  }

  public void close() {
    for (KeyboardSource ks : screenKeyboards)
      ks.close();
  }

  /**
   * Allows to connect to an event in all available sources.
   */
  public void connect(Integer eventType, Observation obs) {
    for (KeyboardSource ks : screenKeyboards)
      ks.connect(eventType, obs);
  }

  /**
   * Engine's private use.
   */
  public void add(KeyboardSource ks) {
    screenKeyboards.add(ks);
  }

  /**
   * Engine's private use.
   */
  public void remove(KeyboardSource ks) {
    screenKeyboards.remove(ks);
  }

  public java.util.List<IOResource> list() {
    return new java.util.ArrayList<IOResource>(screenKeyboards);
  }

  private HashSet<KeyboardSource> screenKeyboards = new HashSet<KeyboardSource>();
}
