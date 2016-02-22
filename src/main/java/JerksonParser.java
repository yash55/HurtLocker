import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yeshpal on 2/22/16.
 */
public class JerksonParser {

    static String raw;

    public  static String parsRaw(String rawIn) {
        raw = rawIn;
        String output = "";
        String s1 = "";  // name
        Pattern p1 = Pattern.compile("(name:)([a-z0]*);", Pattern.CASE_INSENSITIVE);
        Matcher m1 = p1.matcher(raw);
        Pattern p2 = Pattern.compile("(price:)([0-9.]*);", Pattern.CASE_INSENSITIVE);
        Matcher m2 = p2.matcher(raw);
        Pattern p3 = Pattern.compile("(type:)([a-z0]*)[;%\\\\*\\\\^!@]", Pattern.CASE_INSENSITIVE);
        Matcher m3 = p3.matcher(raw);
        List<String> nameList = new ArrayList<>();
        List<String> priceList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        ArrayList<String> alreadyName = new ArrayList<>();
        ArrayList<String> alreadyPrice = new ArrayList<>();
        int nameCount = 0;
        int errorCount = 0;

        while (m1.find()) {
            nameList.add(m1.group(2).toUpperCase());
        }
        while (m2.find()) {
            priceList.add(m2.group(2));
        }
        while (m3.find()) {
            typeList.add(m3.group(2));
        }
        System.out.println("names = " +nameList);
        System.out.println("prices = " +priceList);
        System.out.println("types = " +typeList);

        errorCount = handleError(nameList, priceList);
        for (int i = 0; i < nameList.size(); i++) {
            String currentName = nameList.get(i);
            if (currentName.isEmpty()) {
                errorCount++;
                continue;
            }
            if (alreadyName.contains(currentName)) {
                continue;
            }
            alreadyName.add(currentName);
            nameCount++;
            alreadyPrice.add(priceList.get(i));
            for (int j = i+1; j < nameList.size(); j++) {
                String currentJ = nameList.get(j);
                if (currentName.equals(nameList.get(j))) {
                    nameCount++;
                    alreadyPrice.add(priceList.get(j));
                }
            }
            System.out.printf("name:   %9s          seen %-1d times\n", nameList.get(i), nameCount);
            nameCount = 0;
            System.out.println("=================          ============");
            handlePrice(alreadyPrice);
            for (int k = 0; k < alreadyPrice.size(); k++) {
                alreadyPrice.remove(k);
                k--;
            }
        }
        System.out.printf("Errors                     seen %-1d times", errorCount);

        return output;
    }
    private static int handleError(List<String> names, List<String> prices) {
        int errors = 0;
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).isEmpty()) {
                errors++;
                prices.remove(i);
                names.remove(i);
                i--;
            }
            names.set(i, names.get(i).replace('0', 'O'));
        }
        for (int i = 0; i < prices.size(); i++) {
            if (prices.get(i).isEmpty()) {
                errors++;
                names.remove(i);
                prices.remove(i);
                i--;
            }
        }
        return errors;
    }
    private static void handlePrice(ArrayList<String> prices) {
        HashMap<String, Integer> priceMap = new HashMap<>();
        for (int p = 0; p < prices.size(); p++) {
            String price = prices.get(p);
            int pValue = 1;
            if (priceMap.containsKey(price)) {
                pValue = priceMap.get(price);
                pValue++;
                priceMap.replace(price, pValue);
            }
            else {
                priceMap.put(price, 1);
            }
        }
        for (String key : priceMap.keySet()) {
            System.out.printf("Price:%11s          seen %-1d times\n", key, priceMap.get(key));
            System.out.println("-----------------          ------------");
            //Price: 	 3.23		 seen: 5 times
        }
        priceMap.clear();

    }
}
