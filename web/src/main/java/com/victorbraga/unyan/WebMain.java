package com.victorbraga.unyan;

import java.util.Arrays;

import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.KeyboardManager;
import org.unbiquitous.uImpala.engine.io.MouseManager;
import org.unbiquitous.uImpala.engine.io.ScreenManager;
import org.unbiquitous.uImpala.engine.io.SpeakerManager;
import org.unbiquitous.uImpala.web.impl.core.Game;

/**
 * Browser entry point of the wasm port. The scene and manager settings mirror
 * the desktop Main.java; the only difference is how the first scene is
 * provided: wasm builds cannot instantiate scenes by reflection, so a factory
 * is passed in "first_scene_factory" (keeping "first_scene" only for
 * documentation/settings compatibility).
 */
@SuppressWarnings("serial")
public class WebMain {
  public static void main(String[] args) {
    Game.run(new GameSettings() {{
      put("first_scene", Menu.class);
      put("first_scene_factory", (org.unbiquitous.uImpala.engine.core.SceneFactory) Menu::new);
      put("input_managers", Arrays.asList(
              MouseManager.class,
              KeyboardManager.class
      ));
      put("output_managers", Arrays.asList(
              ScreenManager.class,
              SpeakerManager.class
      ));
    }});
  }
}
