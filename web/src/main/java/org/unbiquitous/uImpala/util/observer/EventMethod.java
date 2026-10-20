package org.unbiquitous.uImpala.util.observer;

/**
 * A callback observation handler: Java lambdas implement this functional
 * interface directly. The web build uses it because TeaVM wasm builds cannot
 * dispatch methods by name (java.lang.reflect is restricted in TeaVM).
 */
public interface EventMethod {
  void invoke(Event event, Subject subject);
}
