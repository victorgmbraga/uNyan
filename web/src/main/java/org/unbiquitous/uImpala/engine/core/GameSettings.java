package org.unbiquitous.uImpala.engine.core;

import java.util.HashMap;

/**
 * Web version of GameSettings: just a map of engine options. The original
 * extended uOS InitialProperties (network/radar defaults), which cannot run
 * inside a browser.
 * @author Pimenta
 *
 */
@SuppressWarnings("serial")
public class GameSettings extends HashMap<String, Object> {
}
