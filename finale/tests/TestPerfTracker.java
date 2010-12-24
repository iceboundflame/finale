package finale.tests;

import finale.PerfTracker;

public class TestPerfTracker {
	public static void main(String args[]) {
		PerfTracker pt = PerfTracker.getInstance();
		pt.start();
		pt.addSample(0);
		pt.addSample(0);
		pt.addSample(0);
		pt.addSample(0);
		pt.addSample(1);
		pt.addSample(0);
		pt.addSample(0);
		pt.addSample(0);
		pt.addSample(2);
		pt.addSample(0);
		pt.addSample(0);
		pt.addSample(1);
		System.out.println(pt);
	}
}
