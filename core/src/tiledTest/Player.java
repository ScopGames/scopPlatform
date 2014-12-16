package tiledTest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends Sprite
{
	private Texture texture;
	private TextureRegion frameTex;
	
	/**
	 * Holds the (x,y) coordinates of the bottom left vertex of the sprite's
	 * rectangle.
	 */
	private Vector2 position = new Vector2(0, 0);
	private Vector2 velocity = new Vector2(0, 0); // null velocity
	private Animation walkUpAnimation;
	
	private float speed = 500;
	private float gravity = 900;
	private float jump = 550;
	private boolean canJump = false;
	
	private TiledMapTileLayer collisionLayer;
	private int tileWidth;
	private int tileHeight;
	
	// debug properties
	private boolean debugMovement = false;
	private boolean showPlayerRectangleBounds = false;
	private Sprite playerRectangleBounds;
		
	public Player(TiledMapTileLayer collisionLayer) 
	{		
		super(); // Sprite constructor

		this.collisionLayer = collisionLayer;
		tileHeight = (int) this.collisionLayer.getTileHeight();
		tileWidth = (int) this.collisionLayer.getTileWidth();
		
		texture = new Texture(Gdx.files.internal("player/war_walk.png"));
		setTexture(texture);
		
		setSize(70, 70);
		// ISSUE #1 : https://github.com/ScopGames/scopPlatform/issues/1
		// collision works properly only if getWidth < tileWidth 
		// change the size of the player and try to jump on some blocks. 
		
		//setBounds(0, 0, texture.getWidth(), texture.getHeight());
	
		frameTex = new TextureRegion();
		
		walkUpAnimation = getAnimationFromAtlas("war_walk_up", "player/war_walk.pack");
			
		position.x = tileWidth*1;
		position.y = tileHeight*9;
		
		// rectangle for player bounds
		Pixmap pixmap = new Pixmap((int)getWidth(), (int)getHeight(), Format.RGBA8888);
		pixmap.setColor(1, 0, 0, 0.8f);
		pixmap.drawRectangle(0, 0, (int)getWidth(), (int)getHeight());
		playerRectangleBounds = new Sprite(new Texture(pixmap));
	}
	
	private Animation getAnimationFromAtlas(String animationName, String pathToAtlas) 
	{
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(pathToAtlas));
		AtlasRegion region;
		Array<TextureRegion> animationFrames = new Array<TextureRegion>();
		
		boolean regionNull = false;
		
		for(int i=1; !regionNull; i++)
		{		
			region = atlas.findRegion(animationName, i);
			
			if(region != null)
				animationFrames.add(region);
			else
				regionNull = true;
		}
		
		Animation animation = new Animation(0.1f, animationFrames);
		
		return animation;
	}
	
	private void update(float time) 
	{ 			
		// input processing
		handleInput();
		

		if (debugMovement == false)
		{
			// apply gravity
			velocity.y -= gravity*time;
		}
						
		// clamp velocity
		/*if(velocity.y > speed)
		{
			velocity.y = speed;
		}
		else if(velocity.y < -speed)
		{
			velocity.y = -speed;
		}*/
		
		float oldX = getX(), oldY = getY();
		float xGap = 0;
		
		//Gdx.app.log("velocity", velocity+"");
		
		// collision algorithm		
		boolean collisionY = false, collisionX = false;
		
		// updates the y coordinate
		position.y += velocity.y*time;
			
		// if moving to the top
		if(velocity.y > 0)
		{
			canJump = false;
			
			if(collide(position.x, position.y + getHeight()) || 
			   collide(position.x + getWidth(), position.y + getHeight()))
			{
				collisionY = true;
				//Gdx.app.log("collision y", "while jumping");
			}
			
		}
		else if(velocity.y < 0) 
		{
			if(collide(position.x+1, position.y) || 
			   collide(position.x + getWidth(), position.y))
			{
				collisionY = true;
				//Gdx.app.log("collision y", "because of gravity");
				canJump = true;
			}
		}

		// updates the x coordinate
		position.x += velocity.x*time;
		
		// if moving to the right
		if(velocity.x > 0)
		{
			float actualRightEdge = oldX + getWidth(); // right edge in the actual position
			float futureRightEdge = position.x + getWidth();
			
			if(collide(position.x + getWidth(), position.y+getHeight()) ||
			   collide(position.x + getWidth(), position.y+1))
			{
				collisionX = true;
				
				int tx =(int) ((futureRightEdge)/getWidth()); // x coordinate of the tile
				int tile_x_border = tx*tileWidth; // calcolo il bordo sinistro della tile tx
				xGap = tile_x_border - actualRightEdge ; 
				
				Gdx.app.log("GAP", xGap+"");
				//Gdx.app.log("collision x", "going to right");
			}
		}
		else if(velocity.x < 0) // if moving to the left
		{
			if(collide(position.x, position.y+getHeight()) ||
			   collide(position.x, position.y+1))
			{
				collisionX = true;
				//Gdx.app.log("collision x", "going to left");
			}				
		}
		
		if(debugMovement == false)
		{			
			if (collisionX)
			{
				//Gdx.app.log("Collision", "right: " + (getX()+getWidth())+" left:" + getX()); 
				
				velocity.x = 0;
				//position.x = oldX + xGap;
				position.x = oldX;
			}
			if (collisionY)
			{
				velocity.y = 0;
				position.y = oldY;
			}
		}
		
		// animation (actually it isn't suppose to work)
		float brokenAnimationTime = 0;
		frameTex = walkUpAnimation.getKeyFrame(brokenAnimationTime, true);
		setRegion(frameTex);
		
		setPosition(position.x, position.y);
		Gdx.app.log("position", "x: " + getX() + " y:" + getY());
	}

	public void draw(float delta, Batch batch) {
		update(delta);
		
		if(showPlayerRectangleBounds == true)
		{
			updateDebugRectangle();
			playerRectangleBounds.draw(batch);
		}
		
		super.draw(batch);
	}
		
	private void updateDebugRectangle() {
		playerRectangleBounds.setPosition(this.getX(), this.getY());
	}

	private boolean collide(float x, float y) {
		boolean collision = false;
		MapProperties tileProperties = getTileProperties(x, y);
				
		if(tileProperties.get("collision") !=null &&
		   tileProperties.get("collision").equals("true"))
		{
			collision = true;
			//Gdx.app.log("debug", "collision");
		}
		else
		{
			collision = false;
		}
		
		return collision;
	}
	
	private MapProperties getTileProperties(float x, float y) {
		int tileX = (int) (x / tileWidth);
		int tileY = (int) (y / tileHeight);
		
		//Gdx.app.log("Tile:(" + tileX + ", " + tileY + ")","");
				
		Cell cell = collisionLayer.getCell(tileX, tileY);
		
		if (cell == null) 
		{
			// no tiles found
			Gdx.app.log("warning", "no tiles found at tile:(" + tileX + ", " + tileY + ")");
		}
		else 
		{
			// tile found
			//Gdx.app.log("debug", "SUCCESS");
		}
			
		return cell.getTile().getProperties();
	}
	
	public void setDebug(boolean showPlayerBounds, boolean freeMovement)
	{
		debugMovement = freeMovement;
		showPlayerRectangleBounds = showPlayerBounds;
	}
	
	public void handleInput() {
		float debugStep = 500*Gdx.graphics.getDeltaTime();
		
		velocity.x = 0;
		//velocity.y = 0;
		
		if(Gdx.input.isKeyPressed(Keys.UP))
		{
			if(canJump)
			{
				velocity.y = jump;
			}
			
			if(debugMovement)
			{
				velocity.y = 0;
				position.y += debugStep;
			}				
		}
		if(Gdx.input.isKeyPressed(Keys.DOWN))
		{
			if(debugMovement)
			{
				velocity.y = 0;
				position.y -= debugStep;
			}			
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT))
		{
			velocity.x = -speed;
			
			if(debugMovement)
			{
				velocity.x = 0;
				position.x -= debugStep;
			}
			//position.x = position.x - 70;
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			velocity.x = speed;
			
			if(debugMovement)
			{
				velocity.x = 0;
				position.x += debugStep;
			}
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.F1))
		{
			// toggle debug rectangle
			this.showPlayerRectangleBounds = !this.showPlayerRectangleBounds;			
		}
		if(Gdx.input.isKeyJustPressed(Keys.F2))
		{
			// toggle debug rectangle
			this.debugMovement = !this.debugMovement;			
		}
	}
}
