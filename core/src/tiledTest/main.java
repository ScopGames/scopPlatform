package tiledTest;

import com.badlogic.gdx.Game;

public class main extends Game
{
	PlatformGame game;
	
	@Override
	public void create() {
		game = new PlatformGame();
		setScreen(game);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}
