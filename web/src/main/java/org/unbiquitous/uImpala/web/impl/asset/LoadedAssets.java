package org.unbiquitous.uImpala.web.impl.asset;

import org.teavm.jso.dom.html.HTMLAudioElement;
import org.teavm.jso.dom.html.HTMLImageElement;

import java.util.HashMap;

/**
 * Stores the assets preloaded from the browser before the engine's first
 * scene is created. The web AssetManager serves sprites/audio from here.
 */
public final class LoadedAssets {
  public static final HashMap<String, HTMLImageElement> images = new HashMap<String, HTMLImageElement>();
  public static final HashMap<String, HTMLAudioElement> sounds = new HashMap<String, HTMLAudioElement>();

  private LoadedAssets() {
  }

  public static HTMLImageElement image(String path) {
    return images.get(path);
  }

  public static HTMLAudioElement sound(String path) {
    return sounds.get(path);
  }

  public static void putImage(String path, HTMLImageElement image) {
    images.put(path, image);
  }

  public static void putSound(String path, HTMLAudioElement audio) {
    sounds.put(path, audio);
  }
}
