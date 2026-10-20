package org.unbiquitous.uImpala.util.observer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Immutable class to hold information about an observation (pair
 * object-method, or a static method).
 *
 * Web note: besides the original reflection-based constructor (method by
 * name, used by the desktop backend), there is a lambda-based constructor
 * (Object, EventMethod) that TeaVM builds can use because java.lang.reflect
 * is restricted in wasm. The shared demo sources keep the reflection form for
 * the desktop build, and the web build has small copies of the two classes
 * using the lambda form.
 *
 * @author Pimenta
 *
 */
public class Observation {

  public Observation(){}
  /**
   * Create an object observation.
   * @param object Object.
   * @param method Method.
   */
  public Observation(Object object, String method) {
    this.object = object;
    this.observation = object.getClass() + "/" + method;
    if (object == null)
      throw new Error("Cannot create object observation with null object");
    try {
      this.method = object.getClass().getDeclaredMethod(method, Event.class, Subject.class);
    } catch (Exception e) {
      throw new Error(e);
    }
    if (Modifier.isStatic(this.method.getModifiers()))
      throw new Error("Cannot create object observation with static method");
    this.method.setAccessible(true);
  }

  /**
   * Create a method observation.
   * @param clazz Method's class.
   * @param method Method.
   */
  public Observation(Class<?> clazz, String method) {
    if (clazz == null)
      throw new Error("Cannot create method observation with null class");
    try {
      this.method = clazz.getDeclaredMethod(method, Event.class, Subject.class);
    } catch (Exception e) {
      throw new Error(e);
    }
    if (!Modifier.isStatic(this.method.getModifiers()))
      throw new Error("Cannot create method observation with non-static method");
    this.object = null;
    this.method.setAccessible(true);
  }

  /**
   * Create an object observation with a lambda handler. TeaVM builds use this
   * constructor instead of the reflective one.
   * @param object Receiver of the event.
   * @param handler Lambda receiving (event, subject).
   */
  public Observation(Object object, EventMethod handler) {
    if (object == null)
      throw new Error("Cannot create object observation with null object");
    if (handler == null)
      throw new Error("Cannot create object observation with null handler");
    this.object = object;
    this.lambda = handler;
    this.observation = object.getClass() + "/" + Integer.toHexString(System.identityHashCode(handler));
  }
//==============================================================================
//nothings else matters from here to below
//==============================================================================
  private Object object;
  private Method method;
  private EventMethod lambda;
  private String observation;

  protected void notifyEvent(Event event, Subject subject) {
    try {
      if (lambda != null)
        lambda.invoke(event, subject);
      else
        method.invoke(object, event, subject);
    } catch (Exception e) {
      throw new Error(e);
    }
  }

  public boolean equals(Object obs) {
    return observation.equals(((Observation)obs).observation);
  }

  public int hashCode() {
    return observation.hashCode();
  }
}
