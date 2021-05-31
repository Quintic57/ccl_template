package ccl;

import ccl.domain.Deck;
import ccl.constants.CclConstants.ApplicationConstants;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CclApplication {
    private final static List<String> input = new ArrayList<>();
    private final static Map<String, Integer> deckStartingLines = new LinkedHashMap<>();
    private final static StringBuilder output = new StringBuilder();

    private static int totalCardCount = 0;

    public static void main(String[] args) {
        readAndParseInput();
        Map<String, String> shops = ApplicationConstants.shops;
        for (String key: shops.keySet()) {
            String shopHeader = key + ") " + shops.get(key) + "\n";
            output.append(shopHeader);
            processInput(shops.get(key));
            output.append("Total: $\n\n");
        }
        output.append("Total Card Count: " + totalCardCount + "\n");
        output.append("Grand Total: $" + "\n");
        writeToFile();
    }

    private static void readAndParseInput() {
        try {
            Scanner s = new Scanner(new File("src/resources/in/input.txt"));
            int i = 0;
            while (s.hasNextLine()) {
                final String line = s.nextLine();
                final String deckName = findDeckNameIfInLine(line);
                final boolean isDeckLine = line.matches(".*\\d{4}-\\d{2} - .*") && !deckName.isEmpty();
                if (isDeckLine) {
                    deckStartingLines.put(deckName, i);
                }

                input.add(line);
                i++;
            }
            deckStartingLines.put("<END>", input.size() - 1);
            s.close();
        } catch (IOException e) {
            System.out.println("Oops");
        }
    }

    private static String findDeckNameIfInLine(final String line) {
        for (final String deckName: Deck.getDeckNames()) {
            if (line.contains(deckName)) {
                return deckName;
            }
        }
        return "";
    }

    private static void processInput(final String shop) {
        final String shopUID = ApplicationConstants.shopUIDs.get(shop);
        final List<String> decks = new ArrayList<>(deckStartingLines.keySet());
        int cardCount = 0;

        for (int i = 0; i < decks.size(); i++) {
            if (decks.get(i).equals("<END>")) {
                break;
            }

            int currentDeckIndex = deckStartingLines.get(decks.get(i));
            int nextDeckIndex = deckStartingLines.get(decks.get(i + 1));
            if (isInShop(shopUID, input.subList(currentDeckIndex, nextDeckIndex))) {
                String deckHeader = "  " + decks.get(i) + "\n";
                output.append(deckHeader);

                for (int j = currentDeckIndex; j < nextDeckIndex; j++) {
                    if (input.get(j).contains(shopUID)) {
                        Matcher m = Pattern.compile(".*\\*\\s\\dx\\s(.*?)([" + String.join("", ApplicationConstants.shopUIDs.values()) + "]+).*").matcher(input.get(j));
                        String temp = "";
                        if (m.matches()) {
                            int numCards = m.group(2).length() - m.group(2).replace(shopUID, "").length();
                            temp = "    " + numCards + "x " + m.group(1) + "\n";
                            cardCount = cardCount + numCards;
                        }
                        output.append(temp);
                    }
                }
            }
        }

        totalCardCount = totalCardCount + cardCount;
        output.append("Card Count: " + cardCount + "\n");
    }

    private static boolean isInShop(final String shopUID, final List<String> lines) {
        return lines.stream().anyMatch(line -> line.contains(shopUID));
    }

    private static void writeToFile() {
        try {
            LocalDate now = LocalDate.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String fileName = "Report_" + dtf.format(now) + ".txt";
            BufferedWriter out = new BufferedWriter(new FileWriter("src/resources/out/" + fileName));
            out.write(output.toString());
            out.close();
        } catch (IOException e) {
            System.out.println("Oops");
        }
    }

}
