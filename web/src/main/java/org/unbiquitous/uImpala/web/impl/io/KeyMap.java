package org.unbiquitous.uImpala.web.impl.io;

/**
 * Maps DOM keyboard key names to the key codes used by the engine (the
 * LWJGL2 Keyboard constants of the desktop backend), so scenes comparing
 * e.getKey() against those codes work unchanged in the browser.
 */
public final class KeyMap {
  private KeyMap() {
  }

  /**
   * @return engine key code, or -1 for keys without an engine representation.
   */
  public static int lwjglCode(String key) {
    if (key == null)
      return -1;
    switch (key) {
      case "Escape": return 1;
      case "Enter": return 28;
      case "Backspace": return 14;
      case "Tab": return 15;
      case " ": return 57;
      case "ArrowUp": return 200;
      case "ArrowDown": return 208;
      case "ArrowLeft": return 203;
      case "ArrowRight": return 205;
      default: break;
    }
    if (key.length() != 1)
      return -1;
    char c = Character.toUpperCase(key.charAt(0));
    switch (c) {
      case 'Q': return 16;
      case 'W': return 17;
      case 'E': return 18;
      case 'R': return 19;
      case 'T': return 20;
      case 'Y': return 21;
      case 'U': return 22;
      case 'I': return 23;
      case 'O': return 24;
      case 'P': return 25;
      case 'A': return 30;
      case 'S': return 31;
      case 'D': return 32;
      case 'F': return 33;
      case 'G': return 34;
      case 'H': return 35;
      case 'J': return 36;
      case 'K': return 37;
      case 'L': return 38;
      case 'Z': return 44;
      case 'X': return 45;
      case 'C': return 46;
      case 'V': return 47;
      case 'B': return 48;
      case 'N': return 49;
      case 'M': return 50;
      case '1': return 2;
      case '2': return 3;
      case '3': return 4;
      case '4': return 5;
      case '5': return 6;
      case '6': return 7;
      case '7': return 8;
      case '8': return 9;
      case '9': return 10;
      case '0': return 11;
      default: return -1;
    }
  }
}
