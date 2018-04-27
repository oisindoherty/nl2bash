package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class MainClient {

	public static void main(String[] args) {
		StartScraping("https://stackoverflow.com/questions/tagged/shell+bash?sort=votes&pagesize=50");
	}
	
	public static void StartScraping(String WebpageURL) {
		try {
			new File("ScrapedPages").mkdirs();
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
		
		File verificationFile = new File("ScrapedPages/" + WebpageURL.split("/")[4] + ".verify");
		try {
			verificationFile.createNewFile();
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
		PrintWriter verificationWriter;
		try {
			verificationWriter = new PrintWriter(verificationFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		
		try {
			Document stackOverflow = Jsoup.connect(WebpageURL).get();

			
			Element question = stackOverflow.getElementsByClass("question-hyperlink").first();
			Element answer = stackOverflow.getElementsByClass("answer accepted-answer").first().getElementsByClass("answercell post-layout--right").first();
			
			// String parsedAnswer = answer.text().split("share|improve")[0];
			
			verificationWriter.println(question.text());

			//System.out.println("\nQuestion: " + question.text());
			//System.out.println("\nAnswer: " + parsedAnswer);
			
			//System.out.println("\nCommands found in answer:");
			Elements commands = answer.getElementsByTag("pre");
			for (Element command : commands) {
				verificationWriter.println(command.text());
			}
			// System.out.println("");
			
			verificationWriter.flush();
			verificationWriter.close();
		} catch (Exception e) { 
			verificationWriter.close();
			verificationFile.delete();
			e.printStackTrace();
		}
	}
}
