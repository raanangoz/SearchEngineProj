package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Ranker {
    private HashSet<String> docsAfterFilterCities = new HashSet<>();
    private List<Query> queriesToRanker;
    private List<String> chosenCities;
    public Ranker(List<Query> queriesToRanker, List<String> chosenCities) {
        this.queriesToRanker= queriesToRanker;
        this.chosenCities=chosenCities;
    }

    // TODO: 24/12/2018 function 1 - filter which docs to take based on cities - do once - return list of docs Itzik
    //after that we compare each word from the query to the list , if exthist take it, else- ignore
    // TODO: 24/12/2018 function 2 -   Itzik
    public void filterDocsByCities() {
        //TODO IF CHOSENCITIES .SIZE IS 0 SO DONT FILTER CITIES.
        //TODO ALSO FOR FP 104
        //TODO raanan wants to fix this to be better algorithem
        try {
            for (String city : chosenCities) {
                String [] words;
                String term = "";
                String st = "";
                int i = correctCellDictionary(city);
                File fromFile = new File("C:\\Users\\itzik\\Desktop\\IR\\TestFolder\\output\\Posting " + i + ".txt");
                BufferedReader br = new BufferedReader(new FileReader(fromFile));
                while ((st = br.readLine()) != null) {
                    if(st.startsWith(city)) {
                        words = st.split(" ");
                        if (words[0].equals(city)) {
                            for (int k = 2; k < words.length; k++) {
                                docsAfterFilterCities.add(words[k]);
                            }
                            break;
                        }
                    }
                }
            }
        }
        catch(Exception e){
        }
    }


    private int correctCellDictionary(String termToFind) {
        if (termToFind.charAt(0) >= 'a' && termToFind.charAt(0) <= 'z')
            return (int) termToFind.charAt(0) - 97;
        else if (termToFind.charAt(0) >= 'A' && termToFind.charAt(0) <= 'Z')
            return (int) termToFind.charAt(0) - 65;
        else
            return 26;
    }

//    public List<String> getOrdredDocumentsForQuery(int i) {
//        Query CurrentQuery = queriesToRanker.get(i); // a specific query for extample "hello gas station"
//        String word[] = CurrentQuery.getTerms();
//        for (Map.Entry<String, Integer> entry : queryTerms.entrySet()) {
//            String word[] =
//            entry.getKey();
//        String key = queryTerms.get
//        foreach (String x: queryTerms
//             ) {
//
//        }
//    }
}
