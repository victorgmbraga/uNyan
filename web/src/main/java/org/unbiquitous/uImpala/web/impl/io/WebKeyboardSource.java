package org.unbiquitous.uImpala.web.impl.io;

import org.unbiquitous.uImpala.engine.io.KeyboardSource;

/**
 * Web keyboard source: a local keyboard whose events come from browser key
 * listeners (see WebScreen). The desktop version also handled networked
 * keyboard devices discovered through uOS drivers.
 */
public class WebKeyboardSource extends KeyboardSource {
  public WebKeyboardSource(int amountKeys) {
    super(amountKeys);
  }
}
