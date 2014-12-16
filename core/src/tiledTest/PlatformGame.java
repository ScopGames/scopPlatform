package tiledTest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class PlatformGame implements Screen {

	TiledMap tiledMap;
	OrthogonalTiledMapRenderer tiledMapRenderer;
	OrthographicCamera camera;
	TiledMapTileLayer collisionLayer;
	
	Player player;
	SpriteBatch batch;
	
	
	@Override
	public void show() {		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.zoom = 1.2f; // farther 
		
		tiledMap = new TmxMapLoader().load("platformTileSet.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		tiledMapRenderer.setView(camera);
		
		collisionLayer = (TiledMapTileLayer)tiledMap.getLayers().get("Collision Layer");

		player = new Player(collisionLayer);
		player.setPosition(10*70, 10*70);
		player.setDebug(true, false);
				
		batch = (SpriteBatch)tiledMapRenderer.getSpriteBatch();			
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0.5f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Gdx.app.log("FPS", Gdx.graphics.getFramesPerSecond()+"");
		
		handleInput(delta);
		
		// camera follows the player
		camera.position.set(player.getX(), player.getY(), 0);
		camera.update();
		
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		batch = (SpriteBatch)tiledMapRenderer.getSpriteBatch();		
		
		batch.begin();
		player.draw(delta, batch);
		batch.end();
	}

	private void handleInput(float delta) {
		MapLayers layers = tiledMap.getLayers();
	
		if(Gdx.input.isKeyJustPressed(Keys.NUM_1))
		{
			MapLayer layer0 = layers.get(0);
			layer0.setVisible(!layer0.isVisible());
		}
		if(Gdx.input.isKeyJustPressed(Keys.NUM_2))
		{
			MapLayer layer1 = layers.get(1);
			layer1.setVisible(!layer1.isVisible());
		}
		if(Gdx.input.isKeyJustPressed(Keys.NUM_3))
		{
			MapLayer layer2 = layers.get(2);
			layer2.setVisible(!layer2.isVisible());
		}
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
		{
			Gdx.app.exit();
		}

		//		if(Gdx.input.isKeyPressed(Keys.A))
		//			camera.translate(-speed*delta, 0);
		//		if(Gdx.input.isKeyPressed(Keys.D))
		//			camera.translate(speed*delta, 0);
		//		if(Gdx.input.isKeyPressed(Keys.W))
		//			camera.translate(0, speed*delta);
		//		if(Gdx.input.isKeyPressed(Keys.S))
		//			camera.translate(0, -speed*delta);
	}
	
	@Override
	public void resize(int width, int height) {
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
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
		tiledMap.dispose();
		batch.dispose();
	}	
}
