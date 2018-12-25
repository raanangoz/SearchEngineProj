package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Ranker {
    private HashSet<String> docsAfterFilterCities = new HashSet<>();
    private List<Query> queriesToRanker;
    private List<String> chosenCities;

    public Ranker(List<Query> queriesToRanker, List<String> chosenCities) {
        this.queriesToRanker = queriesToRanker;
        this.chosenCities = chosenCities;
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
                String[] words;
                String term = "";
                String st = "";
                int i = correctCellDictionary(city);
                File fromFile = new File("C:\\Users\\itzik\\Desktop\\IR\\TestFolder\\output\\Posting " + i + ".txt");
                BufferedReader br = new BufferedReader(new FileReader(fromFile));
                while ((st = br.readLine()) != null) {
                    if (st.startsWith(city)) {
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
        } catch (Exception e) {
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

    public List<List<String>> getOrdredDocumentsForQuery(int i) {
        Query CurrentQuery = queriesToRanker.get(i); // a specific query for extample "hello gas station"
        List<List<String>> FinalListWithAllDocs = new ArrayList<>(); //new list to keep all words - hello,gas,station - after parsing
        List<String> word = CurrentQuery.getTerms(); //new list to keep all words - hello,gas,station - after parsing
        List<String> saveDocOfWord = new ArrayList<>(); //list to keep docs of the word
        try {
            for (int k = 0; k < word.size(); k++) {
                String quertyWord = word.get(k); //quertyword = 1st word - hello
                int index = correctCellDictionary(quertyWord); //get right index file
                String st = "";
                String SavePath=Model.getInstance().getSavePath();
                File fromFile = new File(SavePath+"\\Posting " + index + ".txt");
                BufferedReader br = new BufferedReader(new FileReader(fromFile));
                while ((st = br.readLine()) != null) { //go over all the posting file
                    if (st.startsWith(quertyWord)) { //if found the line in posting file
                        String[] words = st.split(" "); //split with space
                        for (int l = 2; l < words.length; l++) { //go over all the docs
                            if (docsAfterFilterCities.size() == 0) //if no country slected
                                saveDocOfWord.add(words[k]);
                            else if (docsAfterFilterCities.contains(words[l])) //if doc is equale to a doc from cities
                                saveDocOfWord.add(words[k]); //add it to list

                        }
                        FinalListWithAllDocs.add(saveDocOfWord);
                        // TODO: 25/12/2018 add break?  Itzik
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FinalListWithAllDocs;
    }

    // TODO: 25/12/2018 basicly words in the function above contains how many times it's in each doc  Itzik
    // TODO: 25/12/2018 so now we need to open dictonary if we want total DF\IDF  Itzik
    //get doc size
    public double getDocSize() throws IOException {
        String savePath = Model.getInstance().getSavePath();
        File fromFile = new File(savePath + "\\CorpusAvgDocLength.txt");
        BufferedReader br = new BufferedReader(new FileReader(fromFile));
        String size = br.readLine();
        return Double.parseDouble(size);
    }

}
