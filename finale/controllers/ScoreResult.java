package finale.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ScoreResult {
	public int score, level;
	public boolean isNewHigh;
	public int numFriendsBeat;
	public List<String> friendsBeat = new ArrayList<String>();
	public Map<String, Integer> friendsScores = new TreeMap<String, Integer>();
	public Map<String, String> friendsNames = new TreeMap<String, String>();

	public String captionString() {
		String newHigh = isNewHigh ? ", a new high score" : "";
		return "I scored "+score+" points in FINALE"+newHigh+"!";
	}
	public String descriptionString() {
		String iBeat = "I beat ";
		String friendBeat0="", friendBeat1="";
		if (numFriendsBeat >= 1)
			friendBeat0 = friendsNames.get(friendsBeat.get(0));
		if (numFriendsBeat >= 2)
			friendBeat1 = friendsNames.get(friendsBeat.get(1));
		
		if (numFriendsBeat == 0) {
			iBeat = "";
		} else if (numFriendsBeat == 1) {
			iBeat += friendBeat0+"! ";
		} else if (numFriendsBeat == 2) {
			iBeat += friendBeat0+" and "+friendBeat1+"! ";
		} else if (numFriendsBeat > 2) {
			int nMore = numFriendsBeat-1;
			iBeat += friendBeat0+", "+friendBeat1+" and "
					+nMore+" more friend"+(nMore > 1 ? "s" : "")+"! ";
		}
		
		String whatIsFinale =
			"FINALE is an addictive block-matching game like Lumines(R).";
		return iBeat+whatIsFinale;
	}
}
