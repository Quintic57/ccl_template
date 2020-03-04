package ccl.constants;

import java.util.HashMap;
import java.util.Map;

public class CclConstants {
    public static class APIConstants {

    }

    public static class ApplicationConstants {
        public static boolean[] active = {
                false,  //1
                false,  //2
                false,  //3
                false,  //4
                false,  //5
                false,  //6
                false,  //7
                false,  //8
                false,  //9
                false,  //10
                false,  //11
                false,  //12
                false,  //13
                false,  //14
                false,  //15
                false,  //16
                true,  //17
                false,  //18
                true   //19
        };

        public static Map<String, String> shops = new HashMap<String, String>() {{
            put("a", "Coolstuff");
            put("b", "Troll and Toad");
            put("c", "TCG");
            put("d", "Barter");
        }};

        public static Map<String, String> shopUIDs = new HashMap<String, String>() {{
            put("Coolstuff", "+");
            put("Troll and Toad", "|");
            put("TCG", "^");
            put("Barter", ";");
        }};
    }
}
