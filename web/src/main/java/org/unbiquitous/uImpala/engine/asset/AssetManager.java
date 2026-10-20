package org.unbiquitous.uImpala.engine.asset;

import java.util.HashMap;

/**
 * Web-trimmed version of the asset manager: the demo only uses sprites,
 * animations and audio. Text rendering (Java AWT fonts), tile sets/maps and
 * shape geometry are desktop-only features of the original port and are not
 * available in wasm builds.
 * @author Pimenta
 *
 */
public abstract class AssetManager {
  protected static interface Factory {
    public AssetManager create();
  }

  protected static Factory factory = null;

  /**
   * Constructor.
   * @return AssetManager created.
   */
  public static AssetManager create() {
    return factory.create();
  }

  /**
   * Create a sprite.
   * @param path Image path.
   * @return Sprite created.
   */
  public abstract Sprite newSprite(String path);

  /**
   * Create a animation.
   * @param sprite Sprite to render.
   * @param frames Number of frames in the sprite sheet.
   * @param fps Number of frames per second.
   * @return Animation created.
   */
  public Animation newAnimation(Sprite sprite, int frames, float fps) {
    return new Animation(sprite, frames, fps);
  }

  /**
   * Create a animation.
   * @param path Image path.
   * @param frames Number of frames in the sprite sheet.
   * @param fps Number of frames per second.
   * @return Animation created.
   */
  public Animation newAnimation(String path, int frames, float fps) {
    return new Animation(newSprite(path), frames, fps);
  }

  /**
   * Create an audio.
   * @param path Audio path.
   * @return Audio created.
   */
  public abstract Audio newAudio(String path);

  /**
   * Engine's private use.
   */
  public abstract void destroy();

  protected HashMap<String, Object> assets = new HashMap<String, Object>();
}
