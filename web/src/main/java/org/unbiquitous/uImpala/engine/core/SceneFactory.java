package org.unbiquitous.uImpala.engine.core;

/**
 * Creates the first game scene. Web builds use this factory instead of
 * reflective Class.newInstance() of the "first_scene" setting.
 */
public interface SceneFactory {
  /**
   * @return A new instance of the game scene.
   */
  GameScene create();
}
