package finale.gameModel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import finale.views.ResourceManager;

public class Level {
	public static final int NUM_LEVELS = 6;

	private int levelNum = 1;
	private int timebarAdvancePeriod = 10;
	private int holdTime = 60;
	private int dropPeriod = 30;
	private int scoreToAdvance = 100;
	private String block1MatchColor = "#0000ff";
	private String block2MatchColor = "#ff8800";

	public Level() {
		// make a default level
	}
	
	/**
	 * Makes a custom Level
	 * @param levelNum 
	 * @param timebarAdvancePeriod
	 * @param holdTime
	 * @param dropPeriod
	 * @param scoreToAdvance
	 * @param block1MatchColor
	 * @param block2MatchColor
	 */
	public Level(int levelNum, int timebarAdvancePeriod, int holdTime, int dropPeriod,
			int scoreToAdvance, String block1MatchColor, String block2MatchColor) {
		this.levelNum = levelNum;
		this.timebarAdvancePeriod = timebarAdvancePeriod;
		this.holdTime = holdTime;
		this.dropPeriod = dropPeriod;
		this.scoreToAdvance = scoreToAdvance;
		this.block1MatchColor = block1MatchColor;
		this.block2MatchColor = block2MatchColor;
	}

	/**
	 * Loads a pre-defined Level with the specified file name
	 * @param filename : The name of the file containing level information
	 * @param levelNum : The level number for this Level
	 */
	public Level(String filename, int levelNum) {
		load(filename, levelNum);
	}
	
	private void load(String filename, int levelNum) {
		try {
			this.levelNum = levelNum;
			Pattern p = Pattern.compile("^(\\w+)\\s*=\\s*(.+)$");
			Map<String, String> config = new TreeMap<String, String>();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					getClass().getClassLoader().getResourceAsStream(filename)
					));
			String line;
			while((line = br.readLine()) != null) {
				Matcher m = p.matcher(line);
				if (m.matches())
					config.put(m.group(1), m.group(2));
			}
			timebarAdvancePeriod = Integer.parseInt(config.get("timebarAdvancePeriod"));
			holdTime = Integer.parseInt(config.get("holdTime"));
			dropPeriod = Integer.parseInt(config.get("dropPeriod"));
			scoreToAdvance = Integer.parseInt(config.get("scoreToAdvance"));
			block1MatchColor = config.get("block1MatchColor");
			block2MatchColor = config.get("block2MatchColor");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getLevelNum() {
		return levelNum;
	}

	public int getTimebarAdvancePeriod() {
		return timebarAdvancePeriod;
	}

	public int getHoldTime() {
		return holdTime;
	}

	public int getDropPeriod() {
		return dropPeriod;
	}
	
	public int getScoreToAdvance() {
		return scoreToAdvance;
	}

	public String getBlock1MatchColor() {
		return block1MatchColor;
	}
	
	public String getBlock2MatchColor() {
		return block2MatchColor;
	}
	
	public String getThemeBase() {
		return "level"+levelNum+"/";
	}
	
	/**
	 * Factory method to load the level from the game resources
	 * @param num the level number to load
	 * @return the loaded Level object
	 */
	public static Level loadLevel(int num) {
		if (!levelExists(num))
			return null;
		return new Level(levelFilename(num), num);
	}

	public static boolean levelExists(int num) {
		return num > 0 && num < NUM_LEVELS &&
			ResourceManager.getInstance().getFileStream(levelFilename(num)) != null;
	}
	
	private static String levelFilename(int num) {
		return "finale/resources/levels/"+num;
	}

	public String getBlockMatchColor(boolean color) {
		return color ? block1MatchColor : block2MatchColor;
	}

	// hash of level files. extt is just a random function name.
	public static String extt() {
        try {
        	ResourceManager res = ResourceManager.getInstance();
        	MessageDigest md = MessageDigest.getInstance("SHA-1");
	        for (int i = 1; i < NUM_LEVELS; ++i) {
	        	InputStream fis = res.getFileStream(levelFilename(i));
	        	byte[] dataBytes = new byte[1024];
	        	int nread = fis.read(dataBytes);
	        	while (nread > 0) {
	        		md.update(dataBytes, 0, nread);
	        		nread = fis.read(dataBytes);
	        	}
	        }
	        byte[] hash = md.digest();
	        BigInteger bi = new BigInteger(1, hash);
	        return String.format("%0" + (hash.length << 1) + "x", bi);
        } catch (Exception e) {
        	e.printStackTrace();
        	return "";
        }
	}
}
