package com.victorbraga.unyan;

import org.unbiquitous.uImpala.engine.asset.Animation;
import org.unbiquitous.uImpala.engine.asset.AssetManager;
import org.unbiquitous.uImpala.engine.asset.Sprite;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameObject;
import org.unbiquitous.uImpala.engine.core.GameRenderers;
import org.unbiquitous.uImpala.engine.io.KeyboardEvent;
import org.unbiquitous.uImpala.engine.io.KeyboardSource;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.util.Color;
import org.unbiquitous.uImpala.util.Corner;
import org.unbiquitous.uImpala.util.observer.Event;
import org.unbiquitous.uImpala.util.observer.Observation;
import org.unbiquitous.uImpala.util.observer.Subject;

/**
 * Web copy of the desktop demo class (same logic). The only difference: the
 * keyboard observation uses the lambda-based constructor because TeaVM wasm
 * builds cannot dispatch methods by name with java.lang.reflect.
 */
public class NyanCat extends GameObject{

	Animation nyanAnimation, rainbowAnimation;
	Sprite nyanSprite, rainbowSprite;
	Screen screen;
	int hp = 10, counter = 0;
	KeyboardSource keyboard;
	Observation keydown;
	Boolean blinker = false;
	float nyanScale = 0.7f, rainbowScale = 0.3f;

	public NyanCat(AssetManager assets) {
		screen = GameComponents.get(Screen.class);
		nyanSprite = assets.newSprite("img/nyan_sprite.png");
		nyanAnimation = assets.newAnimation(nyanSprite, 6, 10);
		rainbowSprite = assets.newSprite("img/rainbow.png");
		rainbowAnimation = assets.newAnimation(rainbowSprite, 2, 5);
		keyboard = screen.getKeyboard();
		keydown = new Observation(this, this::OnKeyDown);
		keyboard.connect(KeyboardSource.EVENT_KEY_DOWN, keydown);
	}

	@Override
	protected void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void render(GameRenderers arg0) {
		arg0.put(0, new Runnable(){
			public void run() {
				float x=screen.getMouse().getX(), y=screen.getMouse().getY() - 10;
				while(x > -((rainbowSprite.getWidth()*rainbowScale)/2)){
					rainbowAnimation.render(screen, x, y, Corner.CENTER, 1.0f, 0.0f, rainbowScale, rainbowScale);
					x -= (rainbowSprite.getWidth()*rainbowScale)/2;
				}

				if(hp > 4 || !blinker)
					nyanAnimation.render(screen, screen.getMouse().getX(), screen.getMouse().getY(), Corner.CENTER, 1.0f, 0.0f, nyanScale, nyanScale);
				else
					nyanAnimation.render(screen, screen.getMouse().getX(), screen.getMouse().getY(), Corner.CENTER, 1.0f, 0.0f, nyanScale, nyanScale, Color.red);
			}
		});
	}

	@Override
	protected void update() {
		counter++;
		if(counter > 20){
			blinker = !blinker;
			counter = 0;
		}
	}

	@Override
	protected void wakeup(Object... arg0) {
		// TODO Auto-generated method stub

	}

	protected void OnKeyDown(Event event, Subject subj) {
		KeyboardEvent e = (KeyboardEvent) event;
		if (e.getKey() == org.lwjgl.input.Keyboard.KEY_Z) {
			hp--;
		}
	}
}
