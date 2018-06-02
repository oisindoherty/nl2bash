package client;

import java.io.*;

import java.util.HashSet;

public class MainClient {
	// Each Question Page has 50 actual questions on it.
	// There are currently ~500 questions, so this is fine.
	private static final int QUESTION_PAGES_TO_SCRAPE = 600;

	// More granular status messages for the results of ScrapePage().
	public enum ScrapeStatus {
		GOOD_CACHE, GOOD_NOCACHE, BAD_FILEWRITE, BAD_FILEOPEN, BAD_OTHER;
	}

	private static int page = 1;
	private static HashSet<Integer> cache;

	public static void main(String[] args) {
		// Create a new cache, and attempt to read in cached answers from
		// previous executions stored in "ScrapedPages/cache.txt"
		cache = new HashSet<Integer>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("ScrapedPages/cache.txt")));
			String SOID = null;
			while ((SOID = reader.readLine()) != null) {
				cache.add(Integer.parseInt(SOID));
			}
			reader.close();
		} catch (Exception e) {
			// If any part of this fails, it's fine. Any exception can just
			// fall through and we won't have the cache from previous executions.
		}

		// Main loop to iterate over Question Pages to scrape for questions.
		while (page <= QUESTION_PAGES_TO_SCRAPE) {
			try {
				// Scrape the specified page for links to new questions.
				System.out.println("Scraping page " + page + "...");
				String pageUrl = String.format(
						"https://stackoverflow.com/questions/tagged/shell+bash?page=%d&sort=votes&pagesize=50", page);
				System.out.println(pageUrl);
				ScrapingUtilities.startScraping(pageUrl, cache, page);
			} catch (Exception e) {
				// There's nothing we can do about an error'd StartScraping
				// for that page, except for retrying that page. Results
				// are cached at this point, so we're fine.
				page--;
			}
			// go to the next page
			page++;
		}

	}
}
