package org.unbiquitous.uImpala.web.impl.core;

import org.teavm.jso.browser.AnimationFrameCallback;
import org.teavm.jso.browser.Window;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.web.impl.asset.AssetLoader;
import org.unbiquitous.uImpala.web.impl.asset.BrowserConsole;

/**
 * Web implementation of the engine core Game. Entry of the whole run(): the
 * wasm module bootstraps the backend, waits for the browser asset preload and
 * then drives the engine steps with requestAnimationFrame.
 * @author Pimenta
 *
 */
public class Game extends org.unbiquitous.uImpala.engine.core.Game {
  private boolean running;

  /**
   * Use this method in main() to start the game.
   */
  public static void run(GameSettings settings) {
    initBackends();
    Game game = new Game();
    game.launch(settings);
  }

  private void launch(GameSettings settings) {
    // Scene constructors create Sprite/Audio assets synchronously at init()
    // time, so the browser preload must complete before the engine registers
    // its first scene.
    AssetLoader.load(new Runnable() {
      public void run() {
        onAssetsReady(settings);
      }
    });
  }

  private void onAssetsReady(GameSettings settings) {
    try {
      init(settings);
      running = true;
      scheduleStep();
    } catch (Throwable t) {
      BrowserConsole.error("uNyan: engine bootstrap failed: " + t);
      t.printStackTrace();
    }
  }

  /**
   * Engine's private use: schedules the next game step with rAF.
   */
  private void scheduleStep() {
    Window.requestAnimationFrame(new AnimationFrameCallback() {
      public void onAnimationFrame(double timestamp) {
        step(timestamp);
      }
    });
  }

  private void step(double timestamp) {
    if (!running)
      return;
    try {
      if (isRunning()) {
        stepGame();
        scheduleStep();
      } else {
        running = false;
        finish();
      }
    } catch (Throwable t) {
      BrowserConsole.error("uNyan: engine step failed: " + t);
      t.printStackTrace();
      running = false;
    }
  }

  /**
   * Engine's private use.
   */
  private static void initBackends() {
    org.unbiquitous.uImpala.web.impl.asset.WebAssetManager.initImpl();
    org.unbiquitous.uImpala.web.impl.io.WebScreen.initImpl();
    org.unbiquitous.uImpala.web.impl.io.WebSpeaker.initImpl();
    org.unbiquitous.uImpala.web.impl.time.WebTime.initImpl();
  }
}
