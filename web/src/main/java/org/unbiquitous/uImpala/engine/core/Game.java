package org.unbiquitous.uImpala.engine.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.unbiquitous.uImpala.engine.io.InputManager;
import org.unbiquitous.uImpala.engine.io.OutputManager;
import org.unbiquitous.uImpala.engine.time.DeltaTime;

/**
 * Web-flavored rewrite of the uImpala game class. The original relied on the
 * uOS middleware (threads, networking, System.exit), which cannot run inside a
 * browser. This version keeps the same public semantics (scenes,
 * change/push/pop/quit, managers, game components) but drives the loop through
 * the browser animation loop instead of a blocking while loop.
 */
public abstract class Game {
  /**
   * Register the engine singletons, instantiate the I/O managers declared in
   * the settings and create the first game scene.
   */
  protected void init(GameSettings settings) {
    if (settings == null)
      throw new Error("GameSettings not defined!");
    if (settings.get("first_scene") == null && settings.get("first_scene_factory") == null)
      throw new Error("First game scene not defined!");
    if (settings.get("output_managers") == null)
      throw new Error("Cannot start game with no output managers!");

    GameComponents.put(GameSettings.class, settings);
    GameComponents.put(Game.class, this);
    GameComponents.put(DeltaTime.class, deltatime = new DeltaTime());

    try {
      List<Class<?>> ims = (List<Class<?>>) settings.get("input_managers");
      if (ims != null) {
        for (Class<?> imc : ims) {
          InputManager im = newInputManager(imc);
          GameComponents.put(imc, im);
          inputs.add(im);
        }
      }

      List<Class<?>> oms = (List<Class<?>>) settings.get("output_managers");
      if (oms != null) {
        for (Class<?> omc : oms) {
          OutputManager om = newOutputManager(omc);
          GameComponents.put(omc, om);
          outputs.add(om);
        }
      }

      SceneFactory factory = (SceneFactory) settings.get("first_scene_factory");
      if (factory != null)
        scenes.add(factory.create());
      else
        throw new Error("Web builds require a \"first_scene_factory\" setting "
            + "(wasm builds cannot instantiate scenes by reflection).");
    } catch (ClassCastException e) {
      throw new Error(e);
    }
  }

  /**
   * Managers listed in the settings are the engine's own ones. They are
   * instantiated by identity comparison because wasm builds cannot rely on
   * Class.newInstance().
   */
  private InputManager newInputManager(Class<?> managerClass) {
    if (managerClass == org.unbiquitous.uImpala.engine.io.MouseManager.class)
      return new org.unbiquitous.uImpala.engine.io.MouseManager();
    if (managerClass == org.unbiquitous.uImpala.engine.io.KeyboardManager.class)
      return new org.unbiquitous.uImpala.engine.io.KeyboardManager();
    throw new Error("Web backend cannot instantiate custom input manager: " + managerClass);
  }

  private OutputManager newOutputManager(Class<?> managerClass) {
    if (managerClass == org.unbiquitous.uImpala.engine.io.ScreenManager.class)
      return new org.unbiquitous.uImpala.engine.io.ScreenManager();
    if (managerClass == org.unbiquitous.uImpala.engine.io.SpeakerManager.class)
      return new org.unbiquitous.uImpala.engine.io.SpeakerManager();
    throw new Error("Web backend cannot instantiate custom output manager: " + managerClass);
  }

  /**
   * Call to change the current game scene.
   *
   * @param scene New game scene.
   */
  public void change(GameScene scene) {
    if (scene == null)
      return;
    scene_change = scene;
    change_option = ChangeOption.CHANGE;
  }

  /**
   * Call to push a game scene.
   *
   * @param scene Game scene to be pushed.
   */
  public void push(GameScene scene) {
    if (scene == null)
      return;
    scene_change = scene;
    change_option = ChangeOption.PUSH;
  }

  /**
   * Call to pop the current game scene.
   *
   * @param args Arguments to be passed to the new current game scene.
   */
  public void pop(Object... args) {
    pop_args = args;
    change_option = ChangeOption.POP;
  }

  /**
   * Call to shutdown.
   */
  public void quit() {
    change_option = ChangeOption.QUIT;
  }

  // ==============================================================================
  // nothings else matters from here to below
  // ==============================================================================
  protected GameSettings settings;
  protected LinkedList<GameScene> scenes = new LinkedList<GameScene>();
  protected List<InputManager> inputs = new ArrayList<InputManager>();
  protected List<OutputManager> outputs = new ArrayList<OutputManager>();
  protected DeltaTime deltatime;

  private enum ChangeOption {
    NA, CHANGE, PUSH, POP, QUIT
  }

  private GameScene scene_change = null;
  private Object[] pop_args = null;
  private ChangeOption change_option = ChangeOption.NA;

  /**
   * One engine step: inputs, scene updates, outputs, scene stack transitions.
   * Replaces the original while-loop of uOS-driven start(). The web backend
   * calls it from the browser animation loop.
   */
  protected void stepGame() {
    deltatime.update();
    updateInputs();
    updateScenes();
    deltatime.accumulate();
    renderScenes();
    updateOutput();
    updateStack();
  }

  /**
   * Web builds run while the scene stack is not empty (quit or pop-all exit).
   */
  protected boolean isRunning() {
    return scenes.size() > 0;
  }

  /**
   * Called once after the scene stack becomes empty. The desktop engine exits
   * the process; in the browser it releases resources and the game loop stops.
   */
  protected void finish() {
    close();
  }

  protected void updateOutput() {
    for (OutputManager om : outputs)
      om.update();
  }

  protected void renderScenes() {
    for (GameScene gs : scenes) {
      if (!gs.isFrozen() || (gs.isFrozen() && gs.isVisible()))
        gs.render();
    }
  }

  protected void updateScenes() {
    // Web builds are already display-paced by the browser animation loop, so
    // the Thread.sleep() tick limiter of the desktop engine is not used here.
    for (GameScene gs : scenes) {
      if (!gs.isFrozen())
        gs.update();
    }
  }

  protected void updateInputs() {
    for (InputManager im : inputs)
      im.update();
  }

  protected void close() {
    for (InputManager im : inputs)
      im.close();
    for (OutputManager om : outputs)
      om.close();
  }

  protected void updateStack() {
    GameScene tmp;
    switch (change_option) {
    case NA:
      break;

    case CHANGE:
      tmp = scenes.removeLast();
      tmp.assets().destroy();
      tmp.destroy();
      scenes.add(scene_change);
      break;

    case PUSH:
      scenes.add(scene_change);
      break;

    case POP:
      tmp = scenes.removeLast();
      tmp.assets().destroy();
      tmp.destroy();
      if (scenes.size() > 0)
        scenes.getLast().wakeup(pop_args);
      break;

    case QUIT:
      while (scenes.size() > 0) {
        tmp = scenes.removeLast();
        tmp.assets().destroy();
        tmp.destroy();
      }
      break;

    default:
      throw new Error("Invalid value for ChangeOption in UosGame!");
    }
    scene_change = null;
    pop_args = null;
    change_option = ChangeOption.NA;
  }
}
