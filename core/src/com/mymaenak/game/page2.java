package com.mymaenak.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

public class page2 implements Screen {

    final Drop game;
    OrthographicCamera camera;
    private Texture background;
    private float textBlinkTime = 0;
    private boolean isTextVisible = true;
    private Music MomMusic;


    public page2(final Drop game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 768);
        background = new Texture(Gdx.files.internal("page2.png"));

        MomMusic = Gdx.audio.newMusic(Gdx.files.internal("pmak.mp3"));
        MomMusic.setLooping(false);
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
            game.font.draw(game.batch, "Press [Spacebar] to begin!", 500, 300);
        }
        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void show() {
        MomMusic.play();

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
        MomMusic.dispose();

    }
}