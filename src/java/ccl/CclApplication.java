package ccl;

import ccl.domain.Deck;
import ccl.constants.CclConstants.ApplicationConstants;
import com.sun.tools.javac.util.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CclApplication {
    private static List<String> input;
    private static StringBuilder output;

    public static void main(String[] args) {
        input = new ArrayList<>();
        readInput();
        output = new StringBuilder();
        Map<String, String> shops = ApplicationConstants.shops;
        for (String key: shops.keySet()) {
            String shopHeader = key + ") " + shops.get(key) + "\n";
            output.append(shopHeader);
            appendDeckHeader(shops.get(key));
            output.append("Total: $\n");
        }
        output.append("e) Misc\n");
        writeToFile();
    }

    private static void readInput() {
        try {
            Scanner s = new Scanner(new File("src/resources/in/input.txt"));
            while (s.hasNextLine()) {
                input.add(s.nextLine());
            }
            s.close();
        } catch (IOException e) {
            System.out.println("Oops");
        }
    }

    private static void writeToFile() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("src/resources/out/output.txt"));
            out.write(output.toString());
            out.close();
        } catch (IOException e) {
            System.out.println("Oops");
        }
    }

    private static void appendDeckHeader(String shop) {
        for (int i = 0; i < Deck.values().length; i++) {
            if (isInShop(i + 1, shop)) {
                String deckHeader = "  " + Deck.values()[i].getName() + "\n";
                output.append(deckHeader);
                appendCardList(getCardList(i + 1), shop);
            }
        }
    }
    
    private static void appendCardList(List<Integer> range, String shop) {
        String shopUID = ApplicationConstants.shopUIDs.get(shop);
        for (int i = range.get(0); i <= range.get(1); i++) {
            if (input.get(i).contains(shopUID)) {
                Matcher m = Pattern.compile(".*\\*\\s\\dx\\s(.*?)([+|^]+).*").matcher(input.get(i));
                String temp = "";
                if (m.matches()) {
                    int numCards = m.group(2).length() - m.group(2).replace(shopUID, "").length();
                    temp = "    " + numCards + "x " + m.group(1) + "\n";
                }
                output.append(temp);
            }
        }
    }

    private static List<Integer> getCardList(int deckNum) {
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            if (line.matches(".*\\d+\\..*") && line.contains(Integer.toString(deckNum))) {
                int j = i + 1;
                while (j < input.size() && !input.get(j).matches(".*\\d+\\..*")) {
                    j++;
                }
                return Arrays.asList(i, (j - 1));
            }
        }
        return Arrays.asList(-1, -1);
    }

    private static boolean isInShop(int deckNum, String shop) {
        List<Integer> range = getCardList(deckNum);
        return (input.subList(range.get(0), range.get(1) + 1)).toString().contains((ApplicationConstants.shopUIDs.get(shop)));
    }
}
