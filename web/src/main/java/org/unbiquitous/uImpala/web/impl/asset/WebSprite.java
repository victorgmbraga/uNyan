package org.unbiquitous.uImpala.web.impl.asset;

import org.teavm.jso.canvas.CanvasImageSource;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.web.impl.io.WebScreen;
import org.unbiquitous.uImpala.util.Color;
import org.unbiquitous.uImpala.util.Corner;

/**
 * Web implementation of the engine asset Sprite. Renders the preloaded image
 * via 2D canvas drawImage() with rotation/scale/pivot semantics equivalent to
 * the engine's single textured OpenGL quad.
 * @author Pimenta
 *
 */
public class WebSprite extends org.unbiquitous.uImpala.engine.asset.Sprite {
  private final CanvasImageSource image;
  private final int width, height;
  private float clipX, clipY, clipWidth, clipHeight;

  public WebSprite(CanvasImageSource image, int width, int height) {
    this.image = image;
    this.width = width;
    this.height = height;
    resetClip();
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void render(Screen screen, float x, float y, Corner corner, float opacity, float angle, float scaleX, float scaleY, Color color) {
    if (!(screen instanceof WebScreen))
      throw new Error("Sprites must be rendered on the web Screen implementation.");
    CanvasRenderingContext2D ctx = ((WebScreen)screen).getRenderer();

    if (corner == null)
      corner = Corner.CENTER;
    double px = pivotX(corner), py = pivotY(corner);
    double radius = angle * Math.PI / 180.0;
    double cos = Math.cos(radius), sin = Math.sin(radius);
    float opacityClamped = clamp(opacity);

    ctx.save();
    ctx.setGlobalAlpha(opacityClamped);
    ctx.setTransform(cos * scaleX, sin * scaleX, -sin * scaleY, cos * scaleY, x, y);
    if (color == null || baseColor(color))
      ctx.drawImage(image, clipX, clipY, clipWidth, clipHeight, px, py, clipWidth, clipHeight);
    else {
      // Multiply blending emulates the OpenGL texture color multiplication.
      ctx.drawImage(image, clipX, clipY, clipWidth, clipHeight, px, py, clipWidth, clipHeight);
      ctx.setGlobalCompositeOperation("multiply");
      ctx.setGlobalAlpha(opacityClamped * clamp(color.a));
      int r = roundPct(color.r), g = roundPct(color.g), b = roundPct(color.b);
      ctx.setFillStyle("rgb(" + r + "," + g + "," + b + ")");
      ctx.fillRect(px, py, clipWidth, clipHeight);
    }
    ctx.restore();
  }

  public void resetClip() {
    clip(0, 0, width, height);
  }

  public void clip(float x, float y, float w, float h) {
    this.clipX = x;
    this.clipY = y;
    this.clipWidth = w;
    this.clipHeight = h;
  }

  private static float clamp(float opacity) {
    return opacity < 0 ? 0 : (opacity > 1 ? 1 : opacity);
  }

  private static boolean baseColor(Color color) {
    return color.r >= 0.999f && color.g >= 0.999f && color.b >= 0.999f && color.a >= 0.999f;
  }

  private static int roundPct(float component) {
    return Math.round(clamp(component) * 255);
  }

  private double pivotX(Corner corner) {
    switch (corner) {
      case TOP_LEFT:
      case BOTTOM_LEFT:
        return 0;
      case TOP_RIGHT:
      case BOTTOM_RIGHT:
        return -clipWidth;
      default:
        return -clipWidth / 2;
    }
  }

  private double pivotY(Corner corner) {
    switch (corner) {
      case TOP_LEFT:
      case TOP_RIGHT:
        return 0;
      case BOTTOM_LEFT:
      case BOTTOM_RIGHT:
        return -clipHeight;
      default:
        return -clipHeight / 2;
    }
  }
}
