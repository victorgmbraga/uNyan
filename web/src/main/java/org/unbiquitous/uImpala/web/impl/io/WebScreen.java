package org.unbiquitous.uImpala.web.impl.io;

import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.TextRectangle;
import org.unbiquitous.uImpala.engine.core.GameComponents;

/**
 * Web implementation of the engine io Screen. Backed by a canvas in the host
 * page: open() resizes it, installs browser input listeners and feeds the
 * engine's MouseSource/KeyboardSource queues. Event types and key codes match
 * the LWJGL-based desktop backend so demo logic does not change.
 * @author Pimenta
 *
 */
public class WebScreen extends org.unbiquitous.uImpala.engine.io.Screen {
  private static final String CANVAS_ID = "uNyanScreen";

  private final HTMLCanvasElement canvas;
  private CanvasRenderingContext2D renderer;
  private org.unbiquitous.uImpala.engine.io.MouseSource mouse;
  private WebKeyboardSource keyboard;
  private boolean open;
  private String title;
  private int width, height;
  private boolean fullscreen;

  public WebScreen() {
    observations.addEvents(EVENT_CLOSE_REQUEST);
    canvas = (HTMLCanvasElement) HTMLDocument.current().getElementById(CANVAS_ID).cast();
    mouse = new org.unbiquitous.uImpala.engine.io.MouseSource(4);
    keyboard = new WebKeyboardSource(256);
  }

  private static class Factory implements org.unbiquitous.uImpala.engine.io.Screen.Factory {
    public WebScreen create() {
      return new WebScreen();
    }
  }

  /**
   * Engine's private use.
   */
  public static void initImpl() {
    factory = new Factory();
  }

  public void open() {
    throw new Error("Web backend does not support opening at fullscreen max resolution.");
  }

  public void open(String t, int w, int h, boolean f, String i, boolean gl) {
    title = t;
    width = w;
    height = h;
    fullscreen = f;
    open = true;

    canvas.setWidth(w);
    canvas.setHeight(h);
    HTMLDocument.current().setTitle(t);
    renderer = (CanvasRenderingContext2D) canvas.getContext("2d").cast();

    registerInput();
    addMouseKeyboard();
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
    HTMLDocument.current().setTitle(title);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
    canvas.setWidth(width);
    canvas.setHeight(height);
  }

  public boolean isFullscreen() {
    return fullscreen;
  }

  public void setFullscreen(boolean fullscreen) {
    this.fullscreen = fullscreen;
  }

  public String getIcon() {
    return null;
  }

  public void setIcon(String icon) {
  }

  public boolean isCloseRequested() {
    return false;
  }

  public org.unbiquitous.uImpala.engine.io.MouseSource getMouse() {
    return mouse;
  }

  public WebKeyboardSource getKeyboard() {
    return keyboard;
  }

  public void start() {

  }

  public void stop() {

  }

  protected void update() {
  }

  public void close() {
    open = false;
  }

  public boolean isUpdating() {
    return open;
  }

  /**
   * Engine's private use.
   */
  public CanvasRenderingContext2D getRenderer() {
    return renderer;
  }

  /**
   * Maps viewport coordinates to canvas pixels: the page may scale the canvas
   * to fit smaller windows, so positions follow the CSS layout box.
   */
  private void registerInput() {
    canvas.addEventListener("mousemove", event -> {
      MouseEvent e = (MouseEvent) event;
      mouse.add(mouseEvent(
          org.unbiquitous.uImpala.engine.io.MouseSource.EVENT_MOUSE_MOTION, e, -1));
    });
    canvas.addEventListener("mousedown", event -> {
      MouseEvent e = (MouseEvent) event;
      mouse.add(mouseEvent(
          org.unbiquitous.uImpala.engine.io.MouseSource.EVENT_BUTTON_DOWN, e, engineButton(e)));
    });
    canvas.addEventListener("mouseup", event -> {
      MouseEvent e = (MouseEvent) event;
      mouse.add(mouseEvent(
          org.unbiquitous.uImpala.engine.io.MouseSource.EVENT_BUTTON_UP, e, engineButton(e)));
    });
    HTMLDocument.current().addEventListener("keydown", event -> {
      org.teavm.jso.dom.events.KeyboardEvent e = (org.teavm.jso.dom.events.KeyboardEvent) event;
      int code = KeyMap.lwjglCode(e.getKey());
      if (code >= 0)
        keyboard.add(new org.unbiquitous.uImpala.engine.io.KeyboardEvent(
            org.unbiquitous.uImpala.engine.io.KeyboardSource.EVENT_KEY_DOWN, code, character(e)));
    });
    HTMLDocument.current().addEventListener("keyup", event -> {
      org.teavm.jso.dom.events.KeyboardEvent e = (org.teavm.jso.dom.events.KeyboardEvent) event;
      int code = KeyMap.lwjglCode(e.getKey());
      if (code >= 0)
        keyboard.add(new org.unbiquitous.uImpala.engine.io.KeyboardEvent(
            org.unbiquitous.uImpala.engine.io.KeyboardSource.EVENT_KEY_UP, code, character(e)));
    });
  }

  private org.unbiquitous.uImpala.engine.io.MouseEvent mouseEvent(int type, MouseEvent event, int button) {
    TextRectangle rect = canvas.getBoundingClientRect();
    double scaleX = (double) canvas.getWidth() / (double) (rect.getRight() - rect.getLeft());
    int x = (int) Math.round((event.getClientX() - rect.getLeft()) * scaleX);
    double scaleY = (double) canvas.getHeight() / (double) (rect.getBottom() - rect.getTop());
    int y = (int) Math.round((event.getClientY() - rect.getTop()) * scaleY);
    return new org.unbiquitous.uImpala.engine.io.MouseEvent(type, x, y, button);
  }

  private static char character(org.teavm.jso.dom.events.KeyboardEvent event) {
    String key = event.getKey();
    return key.length() == 1 ? key.charAt(0) : '\0';
  }

  private void addMouseKeyboard() {
    if (mouse != null)
      GameComponents.get(org.unbiquitous.uImpala.engine.io.MouseManager.class).add(mouse);
    if (keyboard != null)
      GameComponents.get(org.unbiquitous.uImpala.engine.io.KeyboardManager.class).add(keyboard);
  }

  private static int engineButton(MouseEvent event) {
    if (event.getButton() == MouseEvent.LEFT_BUTTON)
      return 0;
    if (event.getButton() == MouseEvent.MIDDLE_BUTTON)
      return 1;
    return 2;
  }
}
