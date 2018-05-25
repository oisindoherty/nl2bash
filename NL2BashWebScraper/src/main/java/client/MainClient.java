package client;

import java.io.*;

import java.util.HashSet;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import com.google.gson.Gson;

public class MainClient {
    private static final int DELAY = 500;
    private static final int PAGES_TO_SCRAPE = 600;
    private static final int TOP_N_ANSWERS = 5;

    public enum ScrapeStatus {
        GOOD_CACHE, GOOD_NOCACHE, BAD_FILEWRITE, BAD_FILEOPEN, BAD_OTHER;
    }

    private static int numScraped = 0;
    private static int page = 1;

    private static HashSet<Integer> cache;

    public static void main(String[] args) {
        cache = new HashSet<Integer>();
        File file = new File("ScrapedPages/cache.txt");
        BufferedReader reader = null;

        // open our cache and add all the IDs to it.
        try {
            reader = new BufferedReader(new FileReader(file));
            String SOID = null;
            while ((SOID = reader.readLine()) != null) {
                cache.add(Integer.parseInt(SOID));
            }
        } catch (Exception e) {
            // oops
        }

        // There are currently 510 pages, so this is fine.
        while (page <= PAGES_TO_SCRAPE) {
            try {
                // Scrape the specified page for links to new questions.
                System.out.println("Scraping page " + page + "...");
                String pageUrl = String.format(
                        "https://stackoverflow.com/questions/tagged/shell+bash?page=%d&sort=votes&pagesize=50", page);
                System.out.println(pageUrl);
                StartScraping(pageUrl);
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
            String pageURL = question.attr("abs:href");
            int id = Integer.parseInt(pageURL.split("/")[4]);
            if (!cache.contains(id)) {
                if (ScrapePage(pageURL) == ScrapeStatus.GOOD_CACHE) {
                    cache.add(id);
                    try (FileWriter fw = new FileWriter("ScrapedPages/cache.txt", true);
                            BufferedWriter bw = new BufferedWriter(fw);
                            PrintWriter out = new PrintWriter(bw)) {
                        out.println(id);
                    } catch (IOException e) {
                        // oops
                    }
                }
                ;
                Thread.sleep(DELAY);
            }
        }
    }

    // Scrapes a webpage and stores the content in a .verify file
    public static ScrapeStatus ScrapePage(String WebpageURL) {
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
