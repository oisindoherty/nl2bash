package clienttests;

import org.junit.Test;
import client.MainClient;
import static org.junit.Assert.*;

// Simple Unit Tests for the MainClient.java

public class ClientTest {
	@Test public void testScraper() {
		// This should error out, this is an invalid url found in the training data.
		int retVal = MainClient.ScrapePage("https://stackoverflow.blog/2018/04/26/stack-overflow-isnt-very-welcoming-its-time-for-that-to-change/");
		//assertTrue("placeholder test", retVal == -1);
		
		// This should not error.
		retVal = MainClient.ScrapePage("https://stackoverflow.com/questions/8748831/when-do-we-need-curly-braces-around-shell-variables");
		//assertTrue("placeholder test", retVal == - 1);
	}
}
