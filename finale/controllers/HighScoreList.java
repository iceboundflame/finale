package finale.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
Manages highscores received through the game.

@author  David Liu, Brandon Liu, Yuzhi Zheng
@version Jun 4, 2008
@author team FINALE
*/
public class HighScoreList {
	
	private static String highScoreFileName = "highscores.txt";
	private ArrayList<Integer> highScores = new ArrayList<Integer>();
	private ArrayList<String> highScoreNames = new ArrayList<String>();
	
	/**
	   Creates a high score object by reading in the textfile to make a 
	   arraylist and updating high score.
	 */
	public HighScoreList()
	{
		readScores();
		updateHighScoreFile();
	}
	
	/**
	   Writes in the new high score into the ArrayList of high scores
	   @param score : the score received at this game to be check if it is high score
	   @param name : name entered by the player for this game
	 */
	public void updateHighScore(int score, String name)
    {
    	if (highScores.isEmpty())
    		readScores();
    	
    	Integer newScore = Integer.valueOf(score);
    	Iterator<Integer> iter = highScores.iterator();
    	int index = 0;
    	while (iter.hasNext() && index++ < 10)
    	{
    		Integer s = iter.next();
    		if (newScore >= s)
    		{
    			highScores.add(index - 1, newScore);
    			highScoreNames.add(index - 1, name);
    			break;
    		}
    	}
    	updateHighScoreFile();
    }
    
    private void readScores()
    {
    	try {
	    	File hsfile = new File(highScoreFileName);
	    	if (!hsfile.exists())
	    		hsfile.createNewFile();
    	
	    	BufferedReader in = new BufferedReader(new FileReader(hsfile));
        	
	    	Pattern linePattern = Pattern.compile("^(\\d+) (.*) (\\w+)$");
        	int count = 0;
        	String line;
        	while ((line = in.readLine()) != null && count < 10)
        	{
        		Matcher match = linePattern.matcher(line);
        		if (!match.matches())
        			continue;
        		int score = Integer.parseInt(match.group(1));
        		String name = match.group(2);
        		String hash = match.group(3);
        		String record = "" + score + " " + name;
        		if (!hash.equals( generateScoreVerification(record) )) {
        			System.out.println("*********** WARNING ***********");
        			System.out.println("Do not attempt to hack FINALE.");
        			System.out.println("Your safety is not guaranteed.");
        			System.out.println("*********** WARNING ***********");
        			continue;
//        			name = "lamer";
//					score = 0;
        		}
        		highScores.add(score);
        		highScoreNames.add(name);
        	}
        	while (highScores.size() < 10 || highScoreNames.size() < 10) {
        		highScores.add(0);
        		highScoreNames.add("FINALE");
        	}
    	}
    	catch (IOException e) {
    		System.err.println("Error: " + e);
    	}
    }
    
    private void updateHighScoreFile()
    {
    	PrintWriter out = null;
    	try {
    		out = new PrintWriter(new File(highScoreFileName));
    		for (int i = 0; i < 10; i ++) {
    			String record = highScores.get(i) + " " + highScoreNames.get(i);
    			out.println(record + " " + generateScoreVerification(record));
    		}
    		out.close();
    	}
    	catch (IOException e){
    		System.err.println("Error: " + e);
    	}
    }
    
    /**
       @return the list of high scores in an ArrayList
     */
    public ArrayList<String> getHighScores()
    {
    	ArrayList<String> scores = new ArrayList<String>();
    	for (int i = 0; i < 10; i++) {
    		scores.add(highScores.get(i) + " " + highScoreNames.get(i));
    	}
    	return scores;
    }
    
    /**
       @param newScore :the score received at this game
       @return true if it is new high score, false if it is not greater than the last high score
     */
    public boolean isNewHighScore(int newScore)
    {
    	readScores();
    	return newScore > highScores.get(9);
    }
    
    /**
     * Returns a verification string to ensure that the high score file is not
     * hacked and changed.  If verification strings do not match, the specific high 
     * score is deleted and reset.
     * @param record : the high score and the high scorer's name (e.g. 348 John)
     * @return the verification string
     */
    public String generateScoreVerification(String record) {
        try {
            String key =
            	"FINALE is a high security application. " +
            	"It is prohibited to tamper with FINALE internal records.";
            
	        String message = record + "-" + key;
	        MessageDigest digester = MessageDigest.getInstance("SHA-512");
	        digester.reset();
	        digester.update(message.getBytes());
	        byte messageDigest[] = digester.digest();
	        
	    	StringBuffer hexString = new StringBuffer();
	    	for (int i = 0; i < messageDigest.length; i++) {
	    		hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	    	}
	
	    	return hexString.toString();
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }
}
