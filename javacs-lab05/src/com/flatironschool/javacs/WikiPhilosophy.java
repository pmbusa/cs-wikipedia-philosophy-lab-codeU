package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

import static java.lang.System.console;
import static java.lang.System.exit;

public class WikiPhilosophy {

	final static WikiFetcher wf = new WikiFetcher();

	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 *
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 *
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

        // some example code to get you started

		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        ArrayList visited= new ArrayList<String>();




        do {
            System.out.println("Crawling " + url);

            // Check if the link has already been visited
            if (visited.contains(url)) {
                // Already visited the page, exiting
                printVisited(visited);
                System.out.println("This page has already been visited resulting in a cycle exiting program.\n");
                exit(-1);
            } else if (url.equals("https://en.wikipedia.org/wiki/Philosophy")) {
                // Success, exiting

                printVisited(visited);
                System.out.println("Successfully reached the philosophy page");
                exit(0);
            }
            else {
                // The page has not been visited, adding to the visited list
                visited.add(url);
            }


            // Download the pages
            Elements paragraphs = wf.fetchWikipedia(url);
            Element firstPara = paragraphs.get(0);


            /* Dont think I need this
            if (paragraphs.isEmpty()) {
                printVisited(visited);
                System.out.println("No more paragraphs to verify");
                exit(-1);
            }
            */



            // in the proper first paragraph

            // get all the children nodes from that paragraph
            Elements children = firstPara.children();
            String paraText = firstPara.text();

            // Extract the first link from the page
            for (Element child: children) {
                String tag = child.tagName();

                System.out.println("Checking child: " + child.ownText());

                if (tag != "a") {           // Checks if the node is a link
                    continue;
                } else if (tag == "a") {    // found a link
                    String text = child.ownText();
                    // found a link, running checks
                    String firstLink = child.absUrl("href");


                    // Checking for a valid link            *** put this in another method
                    // uppercase character check
                    if (Character.isUpperCase(text.charAt(0))) {
                        continue;
                    }
                    // check if page links back to its self
                    else if (url.equals(firstLink)) {
                        continue;
                    }
                    // check if link is to external website
                    else if (!firstLink.contains("//en.wikipedia.org/wiki/")) {
                        continue;
                    }
                    // italics check
                    else if (italicsCheck(child.children())) {
                        continue;
                    }



                    // Parentheses check
                    int childTextStart = paraText.indexOf(child.ownText());
                    int parenthesisCount = 0;
                    for (int i = 0; i < childTextStart; i++) {
                        if (paraText.charAt(i) == '(' || paraText.charAt(i) == '[') {
                            parenthesisCount++;
                        } else if (paraText.charAt(i) == ')' || paraText.charAt(i) == ']') {
                            parenthesisCount--;
                        }
                    }
                    if (parenthesisCount != 0) {
                        continue;
                    }




                    // found the right tag
                    url = firstLink;
                    break;
                }
            }



        } while (!visited.contains(url));


        /*
		Iterable<Node> iter = new WikiNodeIterable(firstPara);
		for (Node node: iter) {
			if (node instanceof TextNode) {
				System.out.print(node);
			}
        }
        */

        // the following throws an exception so the test fails
        // until you update the code
        String msg = "Complete this lab by adding your code and removing this statement.";
        throw new UnsupportedOperationException(msg);





	}

    public static void printVisited(ArrayList<String> visited) {

        System.out.println("\nPath from start to finish:");

        for(String str:visited) {
            System.out.println(str);
        }

        System.out.println("");

        return;
    }


    // Checks for italics in any of the subchildren of the link
    public static boolean italicsCheck(Elements nodes) {

        for(Element child:nodes) {
            String grandTag = child.tagName();
            if (grandTag.equals("i")) {
                return true;
            }

            if (italicsCheck(child.children())) {
                return true;
            }
        }

        return false;
    }
}
