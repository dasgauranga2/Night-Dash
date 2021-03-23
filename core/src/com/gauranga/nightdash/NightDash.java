package com.gauranga.nightdash;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class NightDash extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture ground;
	Random random;
	Texture hourglass;
	ShapeRenderer shapeRenderer;
	int GROUND_HEIGHT = 400;
	int game_state = 0;
	int time_left = 0;
	int TOTAL_TIME = 50;
	int FONT_SIZE = 200;
	Sound coin_sound;

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
	int COIN_DIFF = 700;

	int score = 0;
	BitmapFont font;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		// background texture
		background = new Texture("night_background.png");
		// ground texture
		ground = new Texture("ground2.png");
		random = new Random();
		shapeRenderer = new ShapeRenderer();
		// load sound
		coin_sound = Gdx.audio.newSound(Gdx.files.internal("coin_sound.wav"));
		hourglass = new Texture("hourglass.png");

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
		jack_y = GROUND_HEIGHT;

		// texture for coin
		coin = new Texture("coin.png");

		// set font settings
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("roboto_mono.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 120;
		font = generator.generateFont(parameter);
		generator.dispose();
	}

	// create a new coin
	public void make_coin() {
		// y coordinate for the coin
		float height = (random.nextFloat() * COIN_DIFF) + GROUND_HEIGHT+100;

		coin_xs.add(Gdx.graphics.getWidth());
		coin_ys.add((int) height);
	}

	@Override
	public void render () {
		batch.begin();

		// draw the background
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// draw the ground grass
		batch.draw(ground, 0, GROUND_HEIGHT, Gdx.graphics.getWidth(), 150);

		if (game_state == 1) {
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
				time_left--;
			}
			// calculate the velocity and y coordinate of the jack
			velocity = velocity + gravity;
			jack_y = jack_y-velocity;
			if (jack_y <= GROUND_HEIGHT) {
				jack_y = GROUND_HEIGHT;
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
				int start = random.nextInt(50);
				coin_count = start;
				make_coin();
			}
			coin_rect.clear();
			// draw all the coins
			for (int i=0; i<coin_xs.size(); i++) {
				batch.draw(coin, coin_xs.get(i), coin_ys.get(i),150,150);
				// create rectangle for each coin
				coin_rect.add(new Rectangle(coin_xs.get(i), coin_ys.get(i), 150, 150));
				// move each coin to the left
				coin_xs.set(i, coin_xs.get(i)-10);
			}

			// check if jack collides with any coin
			for (int i=0; i<coin_rect.size(); i++) {
				if (Intersector.overlaps(coin_rect.get(i), jack_rect)) {
					coin_rect.remove(i);
					coin_ys.remove(i);
					coin_xs.remove(i);
					score++;
					coin_sound.play();
					break;
				}
			}

			// check if time is over
			if (time_left == 0) {
				game_state = 2;
			}
		}
		else if (game_state == 0) {
			// game waiting to start

			display_font("NIGHT DASH", Gdx.graphics.getWidth()/2 - 320, Gdx.graphics.getHeight()/2+300, "amatic.ttf", 280);
			display_font("Collect coins", Gdx.graphics.getWidth()/2 - 200, Gdx.graphics.getHeight()/2, "roboto_mono.ttf", 60);
			display_font("before the timer runs out", Gdx.graphics.getWidth()/2 - 450, Gdx.graphics.getHeight()/2-100, "roboto_mono.ttf", 60);

			if (Gdx.input.justTouched()) {
				time_left = TOTAL_TIME;
				game_state = 1;
			}
		}
		else if (game_state == 2) {
			// game over

			// display game over text
			BitmapFont go_font = new BitmapFont();
			FreeTypeFontGenerator go_generator = new FreeTypeFontGenerator(Gdx.files.internal("amatic.ttf"));
			FreeTypeFontGenerator.FreeTypeFontParameter go_parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
			go_parameter.size = FONT_SIZE;
			go_font = go_generator.generateFont(go_parameter);
			go_generator.dispose();
			go_font.draw(batch, "GAME OVER", Gdx.graphics.getWidth()/2-220, Gdx.graphics.getHeight()-700);

			if (Gdx.input.justTouched()) {
				time_left = TOTAL_TIME;
				game_state = 1;
				// reset the game
				jack_state = 0;
				pause = 0;
				velocity = 0;
				coin_count = 0;
				coin_xs.clear();
				coin_ys.clear();
				coin_rect.clear();
				score = 0;
				jack_y = GROUND_HEIGHT;
			}
		}

		// display the score
		batch.draw(coin, Gdx.graphics.getWidth()/2 - 60, Gdx.graphics.getHeight()-500, 100, 100);
		font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth()/2 + 60, FONT_SIZE/2 + Gdx.graphics.getHeight()-500);
		// display the time left
		batch.draw(hourglass, Gdx.graphics.getWidth()-400, Gdx.graphics.getHeight()-hourglass.getWidth()-50, 100, 100);
		font.draw(batch, String.valueOf(time_left), Gdx.graphics.getWidth()-220, Gdx.graphics.getHeight()-80);

		batch.end();

		// draw the ground
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(0,0, Gdx.graphics.getWidth(), GROUND_HEIGHT);
		shapeRenderer.end();
	}

	public void display_font(String text, int x, int y, String font_family, int size) {
		BitmapFont custom_font = new BitmapFont();
		FreeTypeFontGenerator custom_generator = new FreeTypeFontGenerator(Gdx.files.internal(font_family));
		FreeTypeFontGenerator.FreeTypeFontParameter custom_parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		custom_parameter.size = size;
		custom_font = custom_generator.generateFont(custom_parameter);
		custom_generator.dispose();
		custom_font.draw(batch, text, x, y);
	}
}
