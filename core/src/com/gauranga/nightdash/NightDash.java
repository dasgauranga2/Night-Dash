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

	Texture[] dino;
	int dino_state = 0;
	int pause = 0;
	float gravity = 0.5f;
	float velocity = 0;
	float dino_y;
	Rectangle dino_rect;

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

		// all textures for dino
		dino = new Texture[8];
		dino[0] = new Texture("run1.png");
		dino[1] = new Texture("run2.png");
		dino[2] = new Texture("run3.png");
		dino[3] = new Texture("run4.png");
		dino[4] = new Texture("run5.png");
		dino[5] = new Texture("run6.png");
		dino[6] = new Texture("run7.png");
		dino[7] = new Texture("run8.png");
		// initialize y coordinate of dino
		dino_y = Gdx.graphics.getHeight()/3;

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
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

		// draw the background
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// check if screen is touched
		if (Gdx.input.justTouched()) {
			velocity = -10;
		}

		// decide which dino texture to use
		if (pause < 5) {
			pause++;
		}
		else {
			pause = 0;
			if (dino_state < dino.length-1) {
				dino_state++;
			}
			else {
				dino_state = 0;
			}
		}
		// calculate the velocity and y coordinate of the dino
		velocity = velocity + gravity;
		dino_y = dino_y-velocity;
		if (dino_y <= 0) {
			dino_y = 0;
		}
		// draw the dino
		batch.draw(dino[dino_state],200, dino_y);
		// set rectangle for dino
		dino_rect = new Rectangle(200, (int) dino_y, dino[dino_state].getWidth(), dino[dino_state].getHeight());

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

		// check if dino collides with any coin
		for (int i=0; i<coin_rect.size(); i++) {
			if (Intersector.overlaps(coin_rect.get(i), dino_rect)) {
				coin_rect.remove(i);
				coin_ys.remove(i);
				coin_xs.remove(i);
				break;
			}
		}

		shapeRenderer.end();
		batch.end();
	}
}
