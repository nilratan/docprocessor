package com.yahoo.example.docproc;

import org.testng.annotations.*;
import org.testng.Assert;

import java.util.Map;

public class MainAppTest {
	@Test
	public void testComputeHistogram() {
		String content = "hello world hello";
		Map<String, Integer> histogram = HistogramCalculator.computeHistogram(content);
		Assert.assertEquals(2, histogram.get("hello"));
		Assert.assertEquals(1, histogram.get("world"));
	}
}