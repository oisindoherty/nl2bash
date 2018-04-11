package nl2bashscraper;

import java.io.IOException;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class Launcher {

	public static void main(String[] args) {
		StartScraping("https://stackoverflow.com/questions/tagged/shell+bash?sort=votes&pagesize=15");
	}
	
	public static void StartScraping(String WebpageURL) {
		try {
			Document Questions = Jsoup.connect(WebpageURL).get();
			Elements QuestionUrls = Questions.getElementsByClass("question-hyperlink");
			
			for (Element question : QuestionUrls) {
				ScrapePage(question.attr("abs:href"));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void ScrapePage(String WebpageURL) {
		System.out.println("Parsing Page: " + WebpageURL);
		
		try {
			Document stackOverflow = Jsoup.connect(WebpageURL).get();
			
			Element question = stackOverflow.getElementsByClass("question-hyperlink").first();
			Element answer = stackOverflow.getElementsByClass("answer accepted-answer").first().getElementsByClass("answercell post-layout--right").first();
			
			String parsedAnswer = answer.text().split("share|improve")[0];
			
			System.out.println("\nQuestion: " + question.text());
			System.out.println("\nAnswer: " + parsedAnswer);
			
			System.out.println("\nCommands found in answer:");
			Elements commands = answer.getElementsByTag("pre");
			for (Element command : commands) {
				System.out.println("\t" + command.text());
			}
			System.out.println("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
