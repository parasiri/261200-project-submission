package com.mymaenak.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;

public class GameScreen implements Screen {

	final Drop game;
	private OrthographicCamera camera;
	private Texture backgroundImage;
	private Texture dropImage;
	private Texture dropSpecial;
	private Texture DaengImage;
	private Texture MakImage;
	private Sound dropSound;
	private Sound specialSound;
	private Music riverMusic;
	private Player maenak;
	private Player arm;
	private Player hand;
	private Array<Rectangle> limedrops;
	private Array<Rectangle> specialdrops;
	private long lastDropTime;
	private int player1Score = 0;
	private int highScore = 0;
	private int player1Speed = 800;
	private int spawnDiff = 500000000;
	private int dropSpeed = 200;
	private int dropSpeed2 = 1200;
	private int dropVib = 10;
	private int dropleaks = 1;
	private Array<Vector2> maenakPositions;
	private boolean showPopup = false;
	private long specialDropCaughtTime;
	private boolean showSpecialDropImage = false;





	public GameScreen(final Drop game) {
		this.game = game;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1200, 768);

		backgroundImage = new Texture(Gdx.files.internal("baan.png"));
		hand = new Player(500, 150, 64, 64, "hand.png");
		arm = new Player(500, 100, 64, 30, "arm.png");
		maenak = new Player(500, 0, 64, 30, "Maenakbody.png");
		DaengImage = new Texture(Gdx.files.internal("daeng.png"));
		MakImage = new Texture(Gdx.files.internal("Mak.png"));
		maenakPositions = new Array<Vector2>();
		maenakPositions.add(new Vector2(maenak.getRectangle().x, maenak.getRectangle().y));

		dropImage = new Texture(Gdx.files.internal("lime.png"));
		dropSpecial = new Texture(Gdx.files.internal("droplet.png"));
		limedrops = new Array<Rectangle>();
		specialdrops = new Array<Rectangle>();
		spawnLimedrop();
		spawnSpecialdrop();

		// Retrieve the high score from preferences
		Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
		highScore = prefs.getInteger("highScore", 0); // Default high score is 0 if not found

		dropSound = Gdx.audio.newSound(Gdx.files.internal("catch.mp3"));
		specialSound = Gdx.audio.newSound(Gdx.files.internal("pmak.mp3"));
		riverMusic = Gdx.audio.newMusic(Gdx.files.internal("RiverSound.mp3"));
		riverMusic.setLooping(true);
	}

	private void spawnLimedrop() {
		Rectangle limedrop = new Rectangle();
		limedrop.x = MathUtils.random(400, 1200-400);
		limedrop.y = 768;
		limedrop.width = 64;
		limedrop.height = 64;
		limedrops.add(limedrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	private void spawnSpecialdrop() {
		Rectangle special = new Rectangle();
		special.x = MathUtils.random(500, 1200-500);
		special.y = 768;
		special.width = 64;
		special.height = 64;
		specialdrops.add(special);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);


        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            maenak.getRectangle().x -= player1Speed * Gdx.graphics.getDeltaTime();
            updateMaenakPositions(); // Call the method to update bucket positions
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            maenak.getRectangle().x += player1Speed * Gdx.graphics.getDeltaTime();
            updateMaenakPositions(); // Call the method to update bucket positions
        }


        if (maenak.getRectangle().x < 0)
            maenak.getRectangle().x = 0;
        if (maenak.getRectangle().x > 1200 - 64)
            maenak.getRectangle().x = 1200 - 64;


        if (TimeUtils.nanoTime() - lastDropTime > spawnDiff)
            spawnLimedrop();
        if (TimeUtils.nanoTime() - lastDropTime > spawnDiff)
            spawnSpecialdrop();

        //lime
        for (Iterator<Rectangle> iter = limedrops.iterator(); iter.hasNext(); ) {
            Rectangle limedrop = iter.next();
            limedrop.y -= dropSpeed * Gdx.graphics.getDeltaTime();
            limedrop.x += MathUtils.random(-dropVib, dropVib) * Gdx.graphics.getDeltaTime();

            if (limedrop.y + 64 < 0) {
                dropleaks--;

                // reset to init state --> every 11 drops gathered
                if ((player1Score) % 11 == 0) {
                    dropSpeed = 200;
                    dropVib = 10;
                    spawnDiff = 500000000;
                }

                iter.remove();
            }

            // player --> speed up when gathering 5 drops
            // other player --> slower

            if (limedrop.overlaps(hand.getRectangle())) {
                dropSound.play();
                player1Score++;
                if (player1Score % 5 == 0) {
                    player1Speed += 50;
                    dropSpeed += 50;

                }

				if (player1Score > highScore) {
					highScore = player1Score;

					// Save the new high score to preferences
					Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
					prefs.putInteger("highScore", highScore);
					prefs.flush(); // Make sure the highscore is persisted
				}

                iter.remove();

                // Stack the arm on top of each other when limedrop is caught
                float x = maenak.getRectangle().x;
                float y = maenak.getRectangle().y + maenak.getRectangle().height;
                maenakPositions.add(new Vector2(x, y));


            }

            //set game over
            if (dropleaks == 0) {
                game.setScreen(new GameOverScreen(game, highScore));
                dispose();
            }

        }


        //special
        for (Iterator<Rectangle> iter = specialdrops.iterator(); iter.hasNext(); ) {
            Rectangle special = iter.next();
            special.y -= dropSpeed2 * Gdx.graphics.getDeltaTime();


            // player --> speed up when gathering 5 drops
            // other player --> slower

            if (special.overlaps(hand.getRectangle())) {
                specialSound.play();
                player1Score++;
                iter.remove();
				showPopup = true;
				showSpecialDropImage = true;

				Timer.schedule(new Timer.Task() {
					@Override
					public void run() {
						showPopup = false; // Set the flag back to false after the delay
					}
				}, 2); // 1 second delay
            }

        }






        camera.update();


        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        //set background image
        game.batch.draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        for (Rectangle raindrop : limedrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        for (Rectangle special : specialdrops) {
            game.batch.draw(dropSpecial, special.x, special.y);
        }
        game.batch.draw(maenak.getTexture(), maenak.getRectangle().x, maenak.getRectangle().y);
		if (showSpecialDropImage) {
			// Draw the special drop image on the screen
			game.batch.draw(DaengImage, 10, 0);
			game.batch.draw(MakImage, 1000, 0);
		}
        // stack arm
		for (Vector2 position : maenakPositions) {
				game.batch.draw(arm.getTexture(), position.x, position.y);
		}
		game.batch.draw(hand.getTexture(), hand.getRectangle().x, hand.getRectangle().y);
        game.batch.draw(arm.getTexture(), arm.getRectangle().x, arm.getRectangle().y);
		game.batch.draw(maenak.getTexture(), maenak.getRectangle().x, maenak.getRectangle().y);
		game.font.draw(game.batch, "score: " + player1Score + ", speed: " + player1Speed, 1000, 700);
		game.font.draw(game.batch, "High Score: " + highScore, 1000, 680);
        game.batch.end();



    }
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		riverMusic.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		dropImage.dispose();
		maenak.getTexture().dispose();
		dropSound.dispose();
		riverMusic.dispose();
		backgroundImage.dispose();
		dropSpecial.dispose();
		DaengImage.dispose();
		MakImage.dispose();
		specialSound.dispose();
	}

	private void updateMaenakPositions() {
		float x = maenak.getRectangle().x;
		float y = maenak.getRectangle().y + maenak.getRectangle().height;

		arm.getRectangle().x = x;
		arm.getRectangle().y = y;
		hand.getRectangle().x = x;
		hand.getRectangle().y = y + arm.getRectangle().height;

		// Ensure the arms stay within the screen boundaries
		y = Math.max(100, y); // Limit y to a minimum of 0 (top of the screen)
		y = Math.min(350 - maenak.getRectangle().height * maenakPositions.size, y); // Limit y to a maximum

		// Update the positions of all arms in the stack
		for (int i = maenakPositions.size - 1; i >= 0; i--) {
			Vector2 position = maenakPositions.get(i);
			position.set(x, y);
			y += maenak.getRectangle().height;

			arm.getRectangle().y = y;
			hand.getRectangle().y = y + arm.getRectangle().height;

			// Check if neck and head have gone off the screen
			if (arm.getRectangle().y < 0 || hand.getRectangle().y < 0) {
				// Remove player1 from the screen
				maenak.getRectangle().y = -maenak.getRectangle().height ; // Move it out of the screen

				// Remove them from the list
				maenakPositions.removeIndex(i);

				// Adjust the positions of the remaining buckets above
				for (int j = i; j < maenakPositions.size; j++) {
					Vector2 pos = maenakPositions.get(j);
					pos.y -= maenak.getRectangle().height;
				}

			}
		}
	}


}