package FifaScrape;

import java.io.*;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.svg.SvgImage;


public class FifaScrape {

    /* function to save a file with Strings for directory name, file name, and file buffer */
    private static void saveFile(String dir, String filename, String buffer) {
        File file = new File(dir + filename);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(buffer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* helper function to go to the next team's page */
    private static HtmlPage nextPage(HtmlPage page, int i) {
        List<HtmlAnchor> anchors = page.getByXPath("//div[@class='d3-o-media-object d3-o-media-object--vertical fi-o-media-object--teaser']//a");
        HtmlAnchor anchor = anchors.get(i);
        HtmlPage nextPage = null;
        try {
            nextPage = anchor.click();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nextPage;
    }

    /* function to save all players pictures given a fifa teams page and int marker for womens or mens */
    private static void savePlayers(HtmlPage page, int k) {
        List<HtmlElement> teams = page.getByXPath("//div[@class='col-xs-12 col-sm-4 col-md-3 col-flex']");
        if (teams.isEmpty()) {
            System.out.println("There are no teams!");
        } else {
            String buffer = "";
            String dir = "src/main/resources/";

            for (int i = 0; i < teams.size(); i++) {
                HtmlPage teamPage = nextPage(page, i);

                String teamCountry;
                List<HtmlElement> playerNames;
                if (k == 0) {
                    teamCountry = teamPage.getTitleText().split(" - ")[1];
                    playerNames = teamPage.getByXPath("//span[contains(@class,'fi-p__nLonger ')]");
                } else {
                    teamCountry = teamPage.getTitleText().split(" - ")[2];
                    playerNames = teamPage.getByXPath("//span[contains(@class,'fi-p__nShorter ')]");
                }
                teamCountry = teamCountry.replace(" ", "");

                List<SvgImage> playerImgs = teamPage.getByXPath("//a[@class='fi-p--link']//div[@class='fi-p__picture']//*[local-name()='svg']//*[@class='image-r image-responsive']");
                List<HtmlSvg> playerSvgs = teamPage.getByXPath("//a[@class='fi-p--link']//div[@class='fi-p__picture']//*[local-name()='svg']");
                List<HtmlElement> playerRoles = teamPage.getByXPath("//div[@class='fi-p__info--role']");
                List<HtmlElement> playerAges = teamPage.getByXPath("//span[@class='fi-p__info--ageNum']");

                for (int j = 0; j < playerNames.size(); j++) {

                    String playerUrl = (playerImgs.get(j)).getAttribute("xlink:href");
                    String playerSvg = (playerSvgs.get(j)).asXml();
                    String playerAge = (playerAges.get(j)).asText();
                    String playerName = (playerNames.get(j)).asText();
                    String playerPosition = (playerRoles.get(j)).asText();

                    /* formatting svg image since it is standalone, need to add xmlns */
                    playerSvg = playerSvg.replaceFirst(">", " xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink'>");
                    String filename = teamCountry.replace(" ", "+") + "_" + playerName.replace(" ", "+") + "_" + playerAge + "_" + playerPosition;

                    if (k == 0) {
                        saveFile(dir+"images/womens/", filename+".svg", playerSvg);
                    } else {
                        saveFile(dir+"images/mens/", filename+".svg", playerSvg);
                    }

                    Player player = new Player(playerName, teamCountry, playerPosition, playerUrl, new Integer(playerAge));
                    buffer += player.toString();
                }

                saveFile(dir, "players.txt", buffer);
            }

        }
    }

    public static void main(String[] args) {

        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        HtmlPage womenPage = null;
        HtmlPage menPage = null;
        try {
            String womenSearchUrl = "https://www.fifa.com/womensworldcup/teams/";
            String menSearchUrl = "https://www.fifa.com/worldcup/teams/";
            womenPage = client.getPage(womenSearchUrl);
            menPage = client.getPage(menSearchUrl);
        }catch(Exception e){
            e.printStackTrace();
        }

        savePlayers(womenPage, 0);
        savePlayers(menPage, 1);
    }
}
