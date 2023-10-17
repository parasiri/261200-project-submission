package com.mymaenak.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;


public class GameOverScreen implements Screen {

    final Drop game;
    OrthographicCamera camera;
    private Texture background;
    private float textBlinkTime = 0;
    private boolean isTextVisible = true;
    private Music ScreamMusic;
    private int highScore;



    public GameOverScreen(final Drop game, int highScore) {
        this.game = game;
        this.highScore = highScore;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 768);
        background = new Texture(Gdx.files.internal("Over.png"));

        ScreamMusic = Gdx.audio.newMusic(Gdx.files.internal("scream3.mp3"));
        ScreamMusic.setLooping(false);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        textBlinkTime += delta;
        if (textBlinkTime >= 0.5f) {
            isTextVisible = !isTextVisible;
            textBlinkTime = 0;
        }


        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (isTextVisible) {
            game.font.draw(game.batch, "Press [Spacebar] or Tap anywhere to begin!", 500, 350);
        }
        game.font.draw(game.batch, "High Score: " + highScore, 560, 300);
        game.batch.end();


        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }



    @Override
    public void show() {
        ScreamMusic.play();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}