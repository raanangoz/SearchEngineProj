package Model;

import org.json.JSONObject;

import java.util.HashMap;

public class Country {
    private String Country_Name;
    private String Capital_City;
    private String Currency;
    private String Population;
    private static int numberofcity = 0;
    private static HashMap<String, String> cityDocList = new HashMap<>();
    private static HashMap<String, String> languageDocList = new HashMap<>();
    private static HashMap<String, String> cityDocLocation = new HashMap<>();

    public Country(JSONObject information) {
        Country_Name = information.get("name").toString();
        Capital_City = information.get("capital").toString();
        Population = information.get("population").toString();
        Population = ConvertPop(Population);
        Currency = information.getJSONArray("currencies").getJSONObject(0).get("name").toString();

    }

    public static void clear() {
        cityDocList.clear();
        cityDocList.clear();
    }

    private String ConvertPop(String population) {
        String word = population;
        String newword = "";
        if (Parse.isInteger(population)) {
            double x = Double.parseDouble(population.replaceAll(",", ""));
            if (x > 999 && x < 999999) {
                x = x / 1000;
                word = x + "K";
            } else if (x > 999999 && x < 999999999) {
                x = x / 1000000;
                word = x + "M";
            } else if (x > 999999999) {
                x = x / 1000000000;
                word = x + "B";
            }
        }
        return word;
    }

    public Country(String countryName, String population, String currency, HashMap<String, String> docs) {
        Country_Name = countryName;
        Population = population;
        Currency = currency;
        cityDocList = docs;
    }

    //<editor-fold desc="Getter and Setter">

    public static void setLanguageList(String docLangauge, String docNo) {
        if (languageDocList.containsKey(docLangauge))
            languageDocList.put(docLangauge, languageDocList.get(docLangauge) + " " + docNo);
        else if (!docLangauge.equals("") && !docLangauge.equals(" "))
            languageDocList.put(docLangauge, docNo);
    }

    public static HashMap<String, String> getLanguageDoc() {
        return languageDocList;
    }

    public static void setCityDocsList(String city, String doc) {
        if (cityDocList.containsKey(city))
            cityDocList.put(city, cityDocList.get(city) + " " + doc);
        else {
            cityDocList.put(city, doc);
            //get numebr of city's  Itzik
            numberofcity++;
        }
    }

    public static void setCityDocLocation(String docName, String index) {
        if (cityDocLocation.containsKey(docName))
            cityDocLocation.put(docName, cityDocLocation.get(docName) + " " + index);
        else
            cityDocLocation.put(docName, index);
    }

    public String getCountryName() {
        return Country_Name;
    }

    public String getCapitalCity() {
        return Capital_City;
    }

    public String getCurrency() {
        return Currency;
    }

    public String getPopulation() {
        return Population;
    }

    public static HashMap<String, String> getDocs() {
        return cityDocList;
    }
    //</editor-fold>
}
