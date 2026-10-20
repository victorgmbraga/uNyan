package org.unbiquitous.uImpala.web.impl.asset;

import org.teavm.jso.dom.html.HTMLAudioElement;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.unbiquitous.uImpala.engine.asset.Audio;
import org.unbiquitous.uImpala.engine.asset.Sprite;

/**
 * Web implementation of the engine asset AssetManager. Assets must be
 * preloaded (see AssetLoader) because the browser loads media asynchronously
 * while the engine builds scenes synchronously.
 * @author Pimenta
 *
 */
public class WebAssetManager extends org.unbiquitous.uImpala.engine.asset.AssetManager {
  private static class Factory implements org.unbiquitous.uImpala.engine.asset.AssetManager.Factory {
    public WebAssetManager create() {
      return new WebAssetManager();
    }
  }

  /**
   * Engine's private use.
   */
  public static void initImpl() {
    factory = new Factory();
  }

  public Sprite newSprite(String path) {
    Sprite asset = (Sprite)assets.get(path);
    if (asset != null)
      return asset;

    asset = loadSprite(path);
    assets.put(path, asset);
    return asset;
  }

  public Audio newAudio(String path) {
    Audio asset = (Audio)assets.get(path);
    if (asset != null)
      return asset;

    asset = loadAudio(path);
    assets.put(path, asset);
    return asset;
  }

  public void destroy() {
    assets.clear();
  }

  private Sprite loadSprite(String path) {
    HTMLImageElement image = LoadedAssets.image(path);
    if (image == null)
      throw new Error("Sprite not preloaded: " + path + " (check asset-manifest.txt)");
    return new WebSprite(image, image.getNaturalWidth(), image.getNaturalHeight());
  }

  private org.unbiquitous.uImpala.engine.asset.Audio loadAudio(String path) {
    HTMLAudioElement element = LoadedAssets.sound(path);
    if (element == null)
      throw new Error("Audio not preloaded: " + path + " (check asset-manifest.txt)");
    return new WebAudio(element);
  }
}
