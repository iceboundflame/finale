package finale.remote;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import finale.FinaleApplet;
import finale.utils.ClientHttpRequest;
import finale.utils.StringEscaper;

public class ScoreReporter {
	private String submitURL, tok;
	private FinaleApplet applet;
	
	public ScoreReporter() {
		applet = FinaleApplet.getInstance();
		if (applet != null) {
			submitURL = applet.getParameter("submiturl");
			tok = applet.getParameter("tok");
		}
	}
	
	public ScoreResult submitScore(int score, int level, int playTime) {
		if (submitURL == null)
			return null;
		try {
			Map<String, String> params = new TreeMap<String,String>();
			String record = String.valueOf(score) + " "
				+ String.valueOf(level) + " "
				+ String.valueOf(playTime);
			params.put("score", record);
			params.put("vkey", generateScoreVerification(record));
			params.put("tok", tok);
			
			Scanner rd = new Scanner(ClientHttpRequest.post(
				new URL(submitURL), params
			));
			
			ScoreResult x = new ScoreResult();
			x.score = score;
			x.level = level;
			x.isNewHigh = rd.nextBoolean();
			x.numFriendsBeat = rd.nextInt();
			for (int i = 0; i < x.numFriendsBeat; ++i) {
				int beat_score = rd.nextInt();
				String beat_uid = rd.next();
				String beat_name = rd.nextLine();
				
				x.friendsBeat.add(beat_uid);
				x.friendsScores.put(beat_uid, beat_score);
				x.friendsNames.put(beat_uid, beat_name);
			}
			rd.close();
			
			return x;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
            String key = "Welcome to FINALE!";
	        String message = record + key;
	        
	        byte[] hash = MessageDigest.getInstance("SHA-512")
	        							.digest(message.getBytes());
	        BigInteger bi = new BigInteger(1, hash);
	        return String.format("%0" + (hash.length << 1) + "x", bi);
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }
    
    public void refreshPageScores() {
    	if (applet == null)
    		return;
    	try {
			applet.getAppletContext().showDocument(
				new URL("javascript:refreshHS()")
			);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }

    public void postToFacebook(ScoreResult res) {
    	if (applet == null)
    		return;
    	try {
    		String url = "javascript:postScore('"+
    			StringEscaper.escapeJavaScript(res.captionString())+
    			"','"+
    			StringEscaper.escapeJavaScript(res.descriptionString())+
    			"')";
			applet.getAppletContext().showDocument(
				new URL(url)
			);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
}
