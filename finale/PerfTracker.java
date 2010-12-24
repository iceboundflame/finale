package finale;

public class PerfTracker {
	private static PerfTracker inst = new PerfTracker();
	private int frameDrops = 0;
	private int time = 0;
	
	public static PerfTracker getInstance() {
		return inst;
	}
	
	public void reset() {
		frameDrops = 0;
		time = 0;
	}
	
	public float getFrameDropAvg() {
		return (float)frameDrops/time;
	}
	public int getFrameDrops() {
		return frameDrops;
	}
	public int getTime() {
		return time;
	}
	public void addSample(int thisSecondDrops) {
		frameDrops += thisSecondDrops;
		++time;
	}
	
	public String toString() {
		return String.format("[Perf: %.3f %d %d]",
				getFrameDropAvg(), getFrameDrops(), getTime());
	}
}
