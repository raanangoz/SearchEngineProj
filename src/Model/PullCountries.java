package Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

public class PullCountries {
    private HashMap<String, Country> Countries;

    public PullCountries(JSONObject countryInfo) {
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

    //test code github - dont need just for testing
    public static void pullinfos(String cityName) {
        try {
            URL url = new URL("https://restcountries.eu/rest/v2/capital/" + cityName.toLowerCase() + "/?fields=name;capital;currencies;population");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuffer answer = new StringBuffer();
            while ((input = bufferedReader.readLine()) != null)
                answer.append(input);
            bufferedReader.close();
            //System.out.println(answer.toString());
            connection.disconnect();
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[]args) throws IOException {
        DatamuseQuery test = new DatamuseQuery();
        String x = test.findSimilar("vehicle");
        System.out.println(x);

        JSONArray array = new JSONArray(x);

        for(int i=0; i<array.length(); i++) {
            JSONObject jsonObj = array.getJSONObject(i);
            System.out.println(jsonObj.getString("word"));
            System.out.println(jsonObj.getInt("score"));
//            System.out.println(jsonObj.getString("score"));
        }
        pullinfos("jerusalem");
    }
}
