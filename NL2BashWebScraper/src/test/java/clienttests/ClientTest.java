package clienttests;

import org.junit.Test;
import client.MainClient;
import client.ScrapingUtilities;
import static org.junit.Assert.*;

// Simple Unit Tests for the MainClient.java

public class ClientTest {
	@Test
	// Tests the ScrapingUtilities.scrapePage method for different urls.
	public void testScrapePage() {
		MainClient.ScrapeStatus retVal;

		// This should return a BAD_OTHER, as this is an invalid url.
		retVal = ScrapingUtilities.scrapePage("badurl", 0);
		assertTrue("scrapePage invalidurl", retVal == MainClient.ScrapeStatus.BAD_OTHER);

		// This should error out, as this is a valid StackOverflow site but does not
		// have valid answers on it.
		retVal = ScrapingUtilities.scrapePage(
				"https://stackoverflow.blog/2018/04/26/stack-overflow-isnt-very-welcoming-its-time-for-that-to-change/", 0);
		assertTrue("scrapePage invalidStackOverflowurl", retVal == MainClient.ScrapeStatus.BAD_OTHER);

		// This represents a valid question page, and should exit with a GOOD_CACHE.
		retVal = ScrapingUtilities.scrapePage(
				"https://stackoverflow.com/questions/8748831/when-do-we-need-curly-braces-around-shell-variables", 0);
		assertTrue("scrapePage validurl", retVal == MainClient.ScrapeStatus.GOOD_CACHE);

		// TODO: Add more tests for the scrapePage caching. Needs
		// to instantiate a cache here and check against prior tests.

		// TODO: Add tests with BAD_FILEWRITE and BAD_FILEOPEN as valid results.
		// They should only occur when the user has insufficient permissions to
		// read/write the dir though.
	}
}
