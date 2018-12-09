package Model;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class Country {
    private String Country_Name;
    private String Capital_City;
    private String Currency;
    private String Population;
    // TODO: 07/12/2018 get numebr of city's  Itzik
    public static int numberofcity = 0;
    private static HashMap<String, String> cityDocList = new HashMap<>();
    private static HashMap<String, String> cityDocLocation = new HashMap<>();

    public Country(JSONObject information) {
        Country_Name = information.get("name").toString();
        Capital_City = information.get("capital").toString();
        Population = information.get("population").toString();
        Population = ConvertPop(Population);
        Currency = information.getJSONArray("currencies").getJSONObject(0).get("name").toString();

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
//
//            int i;
//            for(i = 0 ; i < word.length();i++){
//                if(word.charAt(i)=='.') {
//                    newword += word.charAt(i);
//                    i++;
//                    break;
//                }
//                newword+=word.charAt(i);
//            }
//            int count=2;
//            for(; i<word.length();i++){
//                if (count==0)
//                    break;
//                count--;
//                newword+=word.charAt(i);
//            }
        }
        return word;
    }

    public Country(String countryName, String population, String currency, HashMap<String, String> docs) {
        Country_Name = countryName;
        Population = population;
        Currency = currency;
        this.cityDocList = docs;
    }

    public static void setCityDocsList(String city, String doc) {
        if (cityDocList.containsKey(city))
            cityDocList.put(city, cityDocList.get(city) + " " + doc);
        else {
            cityDocList.put(city, doc);
            // TODO: 07/12/2018 get numebr of city's  Itzik
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

    public HashMap<String, String> getDocs() {
        return cityDocList;
    }
}
