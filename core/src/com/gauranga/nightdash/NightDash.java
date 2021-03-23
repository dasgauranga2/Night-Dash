package com.gauranga.nightdash;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class NightDash extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Random random;
	ShapeRenderer shapeRenderer;

	Texture[] jack;
	int jack_state = 0;
	int pause = 0;
	float gravity = 0.5f;
	float velocity = 0;
	float jack_y;
	Rectangle jack_rect;

	Texture coin;
	int coin_count = 0;
	ArrayList<Integer> coin_xs = new ArrayList<>();
	ArrayList<Integer> coin_ys = new ArrayList<>();
	ArrayList<Rectangle> coin_rect = new ArrayList<>();
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		// background texture
		background = new Texture("night_background.png");
		random = new Random();
		shapeRenderer = new ShapeRenderer();

		// all textures for jack
		jack = new Texture[8];
		jack[0] = new Texture("run1.png");
		jack[1] = new Texture("run2.png");
		jack[2] = new Texture("run3.png");
		jack[3] = new Texture("run4.png");
		jack[4] = new Texture("run5.png");
		jack[5] = new Texture("run6.png");
		jack[6] = new Texture("run7.png");
		jack[7] = new Texture("run8.png");
		// initialize y coordinate of jack
		jack_y = Gdx.graphics.getHeight()/3;

		// texture for coin
		coin = new Texture("coin.png");
	}

	// create a new coin
	public void make_coin() {
		// y coordinate for the coin
		float height = random.nextFloat() * Gdx.graphics.getHeight()/2;

		coin_xs.add(Gdx.graphics.getWidth());
		coin_ys.add((int) height);
	}

	@Override
	public void render () {
		batch.begin();
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

		// draw the background
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// check if screen is touched
		if (Gdx.input.justTouched()) {
			velocity = -10;
		}

		// decide which jack texture to use
		if (pause < 5) {
			pause++;
		}
		else {
			pause = 0;
			if (jack_state < jack.length-1) {
				jack_state++;
			}
			else {
				jack_state = 0;
			}
		}
		// calculate the velocity and y coordinate of the jack
		velocity = velocity + gravity;
		jack_y = jack_y-velocity;
		if (jack_y <= 0) {
			jack_y = 0;
		}
		// draw the jack
		batch.draw(jack[jack_state],200, (int) jack_y, 250, 330);
		// set rectangle for jack
		jack_rect = new Rectangle(200, (int) jack_y, 220, 300);

		// add a new coin
		if (coin_count < 100) {
			coin_count++;
		}
		else {
			coin_count = 0;
			make_coin();
		}
		coin_rect.clear();
		// draw all the coins
		for (int i=0; i<coin_xs.size(); i++) {
			batch.draw(coin, coin_xs.get(i), coin_ys.get(i),100,100);
			// create rectangle for each coin
			coin_rect.add(new Rectangle(coin_xs.get(i), coin_ys.get(i), 100, 100));
			// move each coin to the left
			coin_xs.set(i, coin_xs.get(i)-5);
		}

		// check if jack collides with any coin
		for (int i=0; i<coin_rect.size(); i++) {
			if (Intersector.overlaps(coin_rect.get(i), jack_rect)) {
				coin_rect.remove(i);
				coin_ys.remove(i);
				coin_xs.remove(i);
				break;
			}
		}

		//shapeRenderer.end();
		batch.end();
	}
}
