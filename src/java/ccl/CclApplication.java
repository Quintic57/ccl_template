package ccl;

import ccl.domain.Deck;
import ccl.constants.CclConstants.ApplicationConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
            if (ApplicationConstants.active[i] && isInShop(i + 1, shop)) {
                String deckHeader = "  " + Deck.values()[i].getName() + "\n";
                output.append(deckHeader);
            }
        }
    }
    
    private static void appendDeckList(List<String> cards) {

    }

    private static boolean isInShop(int deckNum, String shop) {
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            if (line.matches(".*\\d+\\..*") && line.contains(Integer.toString(deckNum))) {
                for (int j = i + 1; j < input.size() && !input.get(j).matches(".*\\d+\\..*"); j++) {
                    String item = input.get(j);
                    if (item.contains(ApplicationConstants.shopUIDs.get(shop))) {
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }
}
