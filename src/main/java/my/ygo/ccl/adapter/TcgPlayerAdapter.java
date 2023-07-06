package my.ygo.ccl.adapter;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

//TODO: In the future, this should be able to pull the cart information just from the user context, but since that code
// is obfuscated, for now just pull it from the HTML
@Component
public class TcgPlayerAdapter {

    public static void main(String[] args) throws IOException {
        final URL url = new URL("https://www.tcgplayer.com/cart");
//        url.
        final Scanner sc = new Scanner(url.openStream());
        //Instantiating the StringBuffer class to hold the result
        StringBuffer sb = new StringBuffer();
        while(sc.hasNext()) {
            sb.append(sc.next());
            //System.out.println(sc.next());
        }
        //Retrieving the String from the String Buffer object
        String result = sb.toString();
        System.out.println(result);
    }

//    public Map<String, List<Item>>

}
