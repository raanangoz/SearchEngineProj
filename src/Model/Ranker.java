package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Ranker {

    public Ranker() {

    }

    public List<String> getOrderedDocs(List<Query> queriesToRanker, List<String> chosenCities) {

        //TODO ALSO FOR FP 104
        HashSet<String> docsAfterFilterCities = new HashSet<>();
        try {
            for (String city : chosenCities) {
                String [] words;
                String term = "";
                String st = "";
                int i = correctCellDictionary(city);
                File fromFile = new File("C:\\Users\\Raanan\\Desktop\\Part2\\posts\\Posting" + i + ".txt");
                BufferedReader br = new BufferedReader(new FileReader(fromFile));
                while ((st = br.readLine()) != null) {
                    if(st.startsWith(city)) {
                        words = st.split(" ");
                        if (words[0].equals(city)) {
                            for (int k = 2; k < st.length(); k++) {
                                docsAfterFilterCities.add(words[k]);
                            }
                        }
                    }
                }
            }
        }
        catch(Exception e){

        }
        return null;
    }
    private int correctCellDictionary(String termToFind) {
        if (termToFind.charAt(0) >= 'a' && termToFind.charAt(0) <= 'z')
            return (int) termToFind.charAt(0) - 97;
        else if (termToFind.charAt(0) >= 'A' && termToFind.charAt(0) <= 'Z')
            return (int) termToFind.charAt(0) - 65;
        else
            return 26;
    }
}
