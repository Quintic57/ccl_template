package my.ygo.ccl.constants;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CclConstants {
    public static class APIConstants {

    }

    public static class ApplicationConstants {
        public static Map<String, String> shops = new HashMap<String, String>() {{
            put("a", "Coolstuff");
            put("b", "Troll and Toad");
            put("c", "TCG");
            put("d", "eBay");
        }};

        public static Map<String, String> shopUIDs = new LinkedHashMap<String, String>() {{
            put("Coolstuff", "+");
            put("Troll and Toad", "/");
            put("TCG", "^");
            put("eBay", "{");
        }};
    }
}
