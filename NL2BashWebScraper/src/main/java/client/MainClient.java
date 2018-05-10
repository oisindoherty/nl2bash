package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import com.google.gson.Gson;

public class MainClient {
	private static int numScraped = 0;
	private static int page = 1;
	
	public static void main(String[] args) {
		// Setting up our vars
		String questionPage1 = "https://stackoverflow.com/questions/tagged/shell+bash?page=";
		String questionPage2 = "&sort=votes&pagesize=50";
		
		// There are currently 508 pages, so this is fine.
		while (page <= 500) {
			try {
				// Scrape the specified page for links to new questions.
				System.out.println("Scraping page " + page + "...");
				StartScraping(questionPage1 + page + questionPage2);
			} catch (Exception e) {
				// oops?
			}
			// go to the next page
			page++;
		}
		
	}

	// Scrapes a webpage for urls to scrape!
	public static void StartScraping(String WebpageURL) throws Exception {
			// Create a new directory to store our newly scraped pages.
			new File("ScrapedPages").mkdirs();
			
			// Connect to the stackoverflow url containing the top 50 bash/shell questions.
			Document Questions = Jsoup.connect(WebpageURL).get();
			
			// Get a collection of all the URLs linked in the list.
			Elements QuestionUrls = Questions.getElementsByClass("question-hyperlink");

			// For each link, scrape it.
			for (Element question : QuestionUrls) {
				ScrapePage(question.attr("abs:href"));
				Thread.sleep(1000);
			}
	}

	// Scrapes a webpage and stores the content in a .verify file
	public static int ScrapePage(String WebpageURL) {
		// Create a Gson to help us create our JSON object.
		Gson GSONClient = new Gson();
		
		numScraped++;
		System.out.printf("[Page %s / Num %s] Parsing Page: %s: Result: ", page, numScraped, WebpageURL);

		// Create the .verify file that we're going to write to.
		File verificationFile = new File("ScrapedPages/" + WebpageURL.split("/")[4] + ".verify");
		try {
			verificationFile.createNewFile();
		} catch (IOException e) {
			System.out.println("failed to create verification file.");
			return -1;
		}
		
		// Open a writer to our new file.
		PrintWriter verificationWriter;
		try {
			verificationWriter = new PrintWriter(verificationFile);
		} catch (FileNotFoundException e) {
			System.out.println("failed to open verification file.");
			return -2;
		}

		try {
			// Create a new  ScrapedPage object to hold information scraped from the page.
			ScrapedPage JSON = new ScrapedPage();
			
			// Connect to the URI from the WebpageURL parameter.
			Document stackOverflow = Jsoup.connect(WebpageURL).get();

			// Get the question.
			Element question = stackOverflow.getElementsByClass("question-hyperlink").first();
			JSON.setTitle(question.text());
			
			// Get a collection of all the answers.
			Elements answers = stackOverflow.getElementsByClass("answer");
			for (int i = 0; i <= 4; i++) {
				// Grab the container that holds the answer.
				Element answer = answers.get(i).getElementsByClass("answercell post-layout--right").first();
				
				// Grab all commands that are wrapped in a code block.
				Elements commands = answer.getElementsByTag("pre");
				if (commands.size() == 0) {
					// If the question didn't have commands, set the command to null.
					JSON.setCommand(null, i);
				} else {
					// Otherwise, combine all the commands found in the answer into one large string.
					String fullCommand = "";
					for (Element command : commands) {
						fullCommand += command.text() + "\n";
					}
					// Removes the last \n that we appended. (fence posting is hard)
					fullCommand.substring(0, fullCommand.length() - 2);
					
					// Apply the change to our ScrapedPage object.
					JSON.setCommand(fullCommand, i);
				}
			}
			
			// Write to the .verify file the JSON version of our 
			verificationWriter.print(GSONClient.toJson(JSON));
			verificationWriter.flush();
			verificationWriter.close();
			
			System.out.println("successfuly scraped webpage.");

			return 1;
		} catch (Exception e) {
			// whoops
			verificationWriter.close();
			verificationFile.delete();
			
			System.out.println("failed to scrape webpage.");
			return -3;
		}
	}
}
