package org.unbiquitous.uImpala.engine.core;

/**
 * Web alias of the engine's component registry (the original versions of
 * uImpala exposed this class in the engine core jar beside GameSingletons).
 * @author Pimenta
 *
 */
public final class GameComponents {
  /**
   * Method to access a singleton of the current game.
   * @param key Singleton class.
   * @return Singleton instance.
   */
  public static <T> T get(Class<T> key) {
    return components.get(key);
  }

  /**
   * Method to put a singleton in the current game.
   * @param key Singleton class.
   * @param value Singleton instance.
   * @return The value mapped previously, or null if there was no mapped value.
   */
  public static Object put(Class<?> key, Object value) {
    return components.put(key, value);
  }
//==============================================================================
//nothings else matters from here to below
//==============================================================================
  private static final org.unbiquitous.uImpala.util.SingletonContainer components =
      new org.unbiquitous.uImpala.util.SingletonContainer();
}
