package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import client.MainClient.ScrapeStatus;

public class ScrapingUtilities {
	// Delay between scraping pages, helps to avoid RECAPTCHA.
	private static final int DELAY = 500;
	// How many answers should we pull from per question?
	private static final int TOP_N_ANSWERS = 5;

	private static int numScraped = 0;

	/**
	 * Scrapes a StackOverflow Question Page for question urls to scrape!
	 * 
	 * @param WebpageURL
	 *            a StackOverflow Question Page to scrape questions from.
	 * @throws Exception
	 *             throws either an IO exception in the case that writing to the
	 *             cache fails, but may also throw an InteruptedException in the
	 *             rare case that Thread.sleep fails.
	 */
	public static void startScraping(String WebpageURL, HashSet<Integer> cache, int page) throws Exception {
		// Create a new directory to store our newly scraped pages.
		new File("ScrapedPages").mkdirs();

		// Connect to the stackoverflow url containing the top 50 bash/shell questions.
		Document Questions = Jsoup.connect(WebpageURL).get();

		// Get a collection of all the URLs linked in the list.
		Elements QuestionUrls = Questions.getElementsByClass("question-hyperlink");

		// Iterate over each link on the page and attempt to scrape it.
		for (Element question : QuestionUrls) {
			String pageURL = question.attr("abs:href");
			// Check if the URL is valid, and can potentially have an id.
			if (pageURL.split("/").length <= 5) {
				throw new Exception("INVALID URL");
			}
			// Get the ID of the question.
			int id = Integer.parseInt(pageURL.split("/")[4]);
			// If we haven't previously cached this question before...
			if (!cache.contains(id)) {
				// Scrape the page and see if we should cache it.
				if (scrapePage(pageURL, page) == ScrapeStatus.GOOD_CACHE) {
					cache.add(id);
					// TODO: Maybe try not instantiating a new FileWriter every time we encounter
					// a page we want to add to the cache. We definitely want to allow execution
					// to be stopped at any point, but we can make a private fw for this class.
					try (FileWriter fw = new FileWriter("ScrapedPages/cache.txt", true);
							BufferedWriter bw = new BufferedWriter(fw);
							PrintWriter out = new PrintWriter(bw)) {
						out.println(id);
					}
				}
				// Sleep in between scraping pages to avoid RECAPTCHA.
				Thread.sleep(DELAY);
			}
		}
	}

	/**
	 * Scrapes a StackOverflow question for answers, storing them in a .verify file.
	 * 
	 * @param WebpageURL
	 *            the StackOverflow question to scrape and store.
	 * @return a ScrapeStatus indicating whether or not the operation succeeded.
	 */
	public static ScrapeStatus scrapePage(String WebpageURL, int page) {
		// Create a new Gson to help us create our JSON object.
		Gson GSONClient = new Gson();

		numScraped++;
		System.out.printf("[Page %s / Num %s] Parsing Page: %s: Result: ", page, numScraped, WebpageURL);

		// Create the .verify file that we're going to write to.
		if (WebpageURL.split("/").length <= 5) {
			return ScrapeStatus.BAD_OTHER;
		}
		File verificationFile = new File("ScrapedPages/" + WebpageURL.split("/")[4] + ".verify");
		try {
			verificationFile.createNewFile();
		} catch (IOException e) {
			System.out.println("failed to create verification file.");
			return ScrapeStatus.BAD_FILEWRITE;
		}

		// Open a writer to our new file.
		PrintWriter verificationWriter;
		try {
			verificationWriter = new PrintWriter(verificationFile);
		} catch (FileNotFoundException e) {
			System.out.println("failed to open verification file.");
			return ScrapeStatus.BAD_FILEOPEN;
		}

		try {
			// Create a new ScrapedPage object to hold information scraped from the page.
			ScrapedPage JSON = new ScrapedPage();

			// Connect to the URI from the WebpageURL parameter.
			Document stackOverflow = Jsoup.connect(WebpageURL).get();

			// Get the question.
			Element question = stackOverflow.getElementsByClass("question-hyperlink").first();
			JSON.setTitle(question.text());

			// Get a collection of all the answers.
			Elements answers = stackOverflow.getElementsByClass("answer");
			for (int i = 0; i < TOP_N_ANSWERS; i++) {
				// Grab the container that holds the answer.
				Element answer = answers.get(i).getElementsByClass("answercell post-layout--right").first();

				// Grab all commands that are wrapped in a code block.
				Elements commands = answer.getElementsByTag("pre");
				if (commands.size() == 0) {
					// If the question didn't have commands, set the command to null.
					JSON.setCommand(null, i);
				} else {
					// Otherwise, combine all the commands found in the answer into one large
					// string.
					String fullCommand = "";
					for (Element command : commands) {
						fullCommand += command.text() + " && ";
					}
					// Removes the last \n that we appended. (fence posting is hard)
					fullCommand.substring(0, fullCommand.length() - 4);

					// Apply the change to our ScrapedPage object.
					JSON.setCommand(fullCommand, i);
				}
			}

			// Write to the .verify file the JSON version of our
			verificationWriter.print(GSONClient.toJson(JSON));
			verificationWriter.flush();
			verificationWriter.close();

			System.out.println("successfuly scraped webpage.");

			if (stackOverflow.getElementsByClass("vote-accepted-on").size() == 0) {
				return ScrapeStatus.GOOD_NOCACHE;
			}

			return ScrapeStatus.GOOD_CACHE;
		} catch (Exception e) {
			// whoops
			verificationWriter.close();
			verificationFile.delete();

			System.out.println("failed to scrape webpage.");
			return ScrapeStatus.BAD_OTHER;
		}
	}
}
