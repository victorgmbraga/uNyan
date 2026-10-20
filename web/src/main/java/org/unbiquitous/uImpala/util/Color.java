package org.unbiquitous.uImpala.util;

/**
 * A color. Matches the deployed uImpala 1.0 jar API (lowercase static
 * constants and short component field names), since the shared demo sources
 * reference Color.red and the backends read component fields directly.
 *
 * @author Pimenta
 *
 */
public class Color {
  public static final Color black = new Color(0.0f, 0.0f, 0.0f);
  public static final Color gray = new Color(0.5f, 0.5f, 0.5f);
  public static final Color white = new Color(1.0f, 1.0f, 1.0f);
  public static final Color red = new Color(1.0f, 0.0f, 0.0f);
  public static final Color green = new Color(0.0f, 1.0f, 0.0f);
  public static final Color blue = new Color(0.0f, 0.0f, 1.0f);
  public static final Color cyan = new Color(0.0f, 1.0f, 1.0f);
  public static final Color magenta = new Color(1.0f, 0.0f, 1.0f);
  public static final Color yellow = new Color(1.0f, 1.0f, 0.0f);

  public float r, g, b, a;

  public Color(float r, float g, float b) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = 1f;
  }

  public Color(float r, float g, float b, float a) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }

  private static final double FACTOR = 0.7;

  public Color darker() {
    return new Color(Math.max((float) (r * FACTOR), 0), Math.max(
        (float) (g * FACTOR), 0), Math.max(
        (float) (b * FACTOR), 0));
  }
}
