package finale.views;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Maintains a cache of Images so that other classes don't have to maintain
 * instances of Images.
 * 
 * @author Team FINALE
 */
public class ResourceManager {
	private static ResourceManager instance = new ResourceManager();
	private static String IMG_BASE = "finale/resources/graphics/";
	private static String FONT_BASE = "finale/resources/fonts/";
	private static String SOUND_BASE = "finale/resources/sounds/";
	
	private Map<String, Font> loadedFontCache = new HashMap<String, Font>();
	private Map<String, BufferedImage> loadedCache = new HashMap<String, BufferedImage>();
	private Map<String, BufferedImage> scaledCache = new HashMap<String, BufferedImage>();
	private Map<String, AudioClip> soundCache = new HashMap<String, AudioClip>();
	private Object cacheLock = new Object();
	
	private ResourceManager() {
		//
	}
	
	/**
	 * Return the singleton instance.
	 * @return the singleton instance.
	 */
	public static ResourceManager getInstance() {
		return instance;
	}
	
	/**
	 * Loads an image with the specified file name.
	 * @param filename : the file name of the Image, no extension.
	 */
	public void preloadImage(final String filename) {
		synchronized (cacheLock) {
			if (loadedCache.containsKey(filename)) {
				//
			} else {
				loadedCache.put(filename, new BufferedImage(1,1,BufferedImage.TYPE_3BYTE_BGR));
				new Thread(new Runnable() {
					@Override
					public void run() {
						System.out.println("Loading image "+filename);
						
						ClassLoader cl = getClass().getClassLoader();
						InputStream strm = cl.getResourceAsStream(filename);

//						try {
//							Thread.sleep(1000); // simulate loading
//						} catch (InterruptedException e) {}
						try {
							synchronized (cacheLock) {
								loadedCache.put(filename, ImageIO.read(strm));
								scaledCache.remove(filename);
							}
						} catch (IOException e) {
							System.err.println("Failed to load "+filename);
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
	}
	public BufferedImage get(String filename, int width, int height) {
		filename = IMG_BASE+filename;
		preloadImage(filename);
		return rescaleImage(filename, width, height);
	}
	
	private BufferedImage rescaleImage(String filename, int width, int height) {
		if (width < 1)
			width = 1;
		if (height < 1)
			height = 1;

		BufferedImage cached = scaledCache.get(filename);
		if (cached == null || cached.getWidth() != width || cached.getHeight() != height) {
			cached = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics gc = cached.getGraphics();
			synchronized (cacheLock) {
				gc.drawImage(loadedCache.get(filename), 0, 0, width, height, null);
			}
			gc.dispose();
			scaledCache.put(filename, cached);
		}
		return scaledCache.get(filename);
	}
	
	
	
	public void preloadFont(String filename) {
		if (!loadedFontCache.containsKey(filename)) {
			System.out.println("Loading font "+filename);
			try {
				loadedFontCache.put(filename, Font.createFont(Font.TRUETYPE_FONT,
						getClass().getClassLoader().getResourceAsStream(filename) ));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public Font getFont(String filename) {
		filename = FONT_BASE+filename;
		preloadFont(filename);
		return loadedFontCache.get(filename);
	}

	public InputStream getFileStream(String filename) {
		return getClass().getClassLoader().getResourceAsStream(filename);
	}

	public void playSound(String basename) {
		final String filename = basename + ".wav.au";
		synchronized (cacheLock) {
			if (soundCache.containsKey(filename)) {
				soundCache.get(filename).stop();
				soundCache.get(filename).play();
			} else {
				soundCache.put(filename, new NullAudioClip());
				new Thread(new Runnable() {
					@Override
					public void run() {
						System.out.println("Loading sound "+filename);
						URL url = getClass().getClassLoader().getResource(SOUND_BASE+filename);
						AudioClip x = Applet.newAudioClip(url);

						synchronized (cacheLock) {
							soundCache.put(filename, x);
						}
						x.play();
					}
				}).start();
			}
		}
	}
	class NullAudioClip implements AudioClip {
		@Override
		public void loop() {
		}
		@Override
		public void play() {
		}
		@Override
		public void stop() {
		}
	}
}
