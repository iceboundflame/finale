package finale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.data.statistics.BoxAndWhiskerItem;

public class PerfTracker {
	private static PerfTracker inst = new PerfTracker();
	private boolean isCollecting = true;
	private List<Integer> frameDrops = new ArrayList<Integer>(1000);
	
	public static PerfTracker getInstance() {
		return inst;
	}
	
	public void reset() {
		frameDrops.clear();
	}
	public void stop() {
		isCollecting = false;
	}
	public void start() {
		isCollecting = true;
	}
	
	public void addSample(int thisSecondDrops) {
		if (!isCollecting) return;
		
		frameDrops.add(thisSecondDrops);
	}
	
	public String toString() {
		BoxAndWhiskerItem stats = BoxAndWhiskerCalculator
			.calculateBoxAndWhiskerStatistics(frameDrops);
		
		return String.format(
				"Perf A=%.3f %d (%.1f (%.1f %.1f %.1f %.1f %.1f) %.1f) %d n=%d",
				stats.getMean(),
				Collections.min(frameDrops),
				stats.getMedian(),
				stats.getMinRegularValue(),
				stats.getQ1(),
				stats.getMedian(),
				stats.getQ3(),
				stats.getMaxRegularValue(),
				stats.getMaxOutlier(),
				Collections.max(frameDrops),
				frameDrops.size());
	}
}
