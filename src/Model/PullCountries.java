package Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class PullCountries {
    private HashMap<String, Country> Countries;

    public PullCountries(JSONObject countryInfo) throws IOException {
        Countries = new HashMap<>();
        JSONArray result = countryInfo.getJSONArray("result");
        for (Object obj : result) {
            JSONObject data = (JSONObject) obj;
            Country country = new Country(data);
            Countries.put(country.getCapitalCity(), country);
        }
    }

    public Country getCountryByCapital(String capitalCityName) {
        return Countries.get(capitalCityName);
    }

    public HashMap<String, Country> getCountries() {
        return Countries;
    }

//    // TODO: 05/12/2018   Itzik remove test class
//    public static void main(String[] args) throws IOException {
//        HttpRequest http = new HttpRequest("https://restcountries.eu/rest/v2/all");
//        PullCountries c = new PullCountries(http.getJsonObject()); //all my information is here
//        System.out.println(c.getCountryByCapital("Tokyo").getCountryName());
//        System.out.println(" ");
//    }
}
