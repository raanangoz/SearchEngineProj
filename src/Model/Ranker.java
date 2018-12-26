package Model;

import java.io.*;
import java.util.*;

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

    public Map<String, Integer>[] getDocsAndTfForEachTerm(int i) {//<Qi: hello:FBIS3 23, GAS:FBIS4, 10...,
        Query CurrentQuery = queriesToRanker.get(i); // a specific query for extample "hello gas station"
        Map<String, Integer> words = CurrentQuery.getTerms();//new list to keep all words - hello,gas,station - after parsing
        Map<String, Integer> docsAndTF = new HashMap<>(); //list to keep docs of SPECIFIC word
        Map<String, Integer>[] docsForEachTerm = new HashMap[words.size()]; // query: 2 terms. first cell: docs for term 1. second cell: docs for term 2.
        try {
            int wordIndexInQuery = 0;
            for (Map.Entry<String, Integer> key : words.entrySet()) {
                String queryWord = key.getKey(); //quertyword = 1st word - hello
                int index = correctCellDictionary(queryWord); //get right index file
                String st = "";
                String SavePath = Model.getInstance().getSavePath();
                File fromFile = new File(SavePath + "\\Posting " + index + ".txt");
                BufferedReader br = new BufferedReader(new FileReader(fromFile));
                while ((st = br.readLine()) != null) { //go over all the posting file
                    if (st.startsWith(queryWord)) { //if found the line in posting file
                        String[] postingList = st.split(" "); //split with space
                        if (postingList[0].equals(queryWord)) {
                            for (int l = 2; l < postingList.length; l++) { //go over all the docs
                                if (docsAfterFilterCities.size() == 0) //if no country slected
                                    //TODO CHANGE THE ZEROES
                                    docsAndTF.put(postingList[l], 0);
                                else if (docsAfterFilterCities.contains(postingList[l])) //if doc is equale to a doc from cities
                                    docsAndTF.put(postingList[l], 0); //add it to list
                            }
                            docsForEachTerm[wordIndexInQuery] = docsAndTF;
                            wordIndexInQuery++;
                            break;
                        }
                        //TODO IF POSTING LIST IS SORTED THEN STOP LOOKING FOR A WORD like query word:stopp, and on posting list its stopt then break
                        //else then keep looking for the word.
                        // TODO: 12/26/2018  if line of a term on posting list is very long and actually is seen on 2 lines on notepad its ok ?
                        // TODO: 25/12/2018 add break?  Itzik
                    }
                }
                br.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return docsForEachTerm;
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

    public HashMap<String, Integer> getAllDocSize() throws IOException {
        String savePath = Model.getInstance().getSavePath();
        File fromFile = new File(savePath + "\\EachDocSize.txt");
        BufferedReader br = new BufferedReader(new FileReader(fromFile));
        String st;
        Map<String, Integer> eachDocSize = new HashMap<String, Integer>();
        while ((st = br.readLine()) != null) {
            String[] words = st.split(" ");
            eachDocSize.put(words[0], Integer.parseInt(words[2]));
        }
        return (HashMap<String, Integer>) eachDocSize;
    }

    /**
     * FQID IS ARRAY OF TERMS ( string,tf)! OF ONE QUERY.
     * the terms arent here, but only their posting lists.
     * @param fqid
     * @return
     */
    public Map<String,Integer> applyBM25Algorithm(Map<String, Integer>[] fqid, double avgDL, int docsNumber, ArrayList<String> terms) {//docsNumber - 470000
        double k1 = 1.5;//TODO a number between 1.2 to 2 to our choice.
        double b = 0.75;
        double IDF=0;
        Map<String,Integer> docsAndValues = new HashMap<>();
        for (int i = 0; i < fqid.length; i++) {//iterate words' posting.
            String queryWord = queriesToRanker.get(i);
            for (Map.Entry<String, Integer> key : fqid[i].entrySet()) {// key holds docname and tf
                String docNo = key.getKey();//docname
                int TF = fqid[i].get(docNo);
                int DL = 0;
                String st;
                BufferedReader br;
                File file = new File(Model.getInstance().getSavePath() + "\\EachDocSize.txt");
                try {
                    br = new BufferedReader(new FileReader(file));
                    while ((st = br.readLine()) != null) {
                        if (st.startsWith(docNo)) {
                            String[] line = st.split(" ");
                            if (line[0].equals(docNo)) {
                                DL = Integer.parseInt(line[1]);
                                break;
                            }
                        }
                        //TODO MAYBE DONT NEED TO CONTINUE
                    }
                    br.close();
                } catch (Exception e) {
                    System.out.println(e);
                }

                int DF;
                DF = LoadedDictionary.getDictionary()[correctCellDictionary(queryWord)].get(queryWord);
                IDF = Math.log((docsNumber - DF + 0.5)/(DF+0.5))/Math.log(2);
                double docRelevance = IDF*((TF * k1 + 1) / (TF + k1 * (1 - b + b * DL / avgDL)));
                //now next doc of same term.
                docsAndValues.put()
            }

        }
    }

    public double getAverageDocumentLength() {
        BufferedReader br;
        double answer = 0;
        String st = "";
        File file = new File(Model.getInstance().getSavePath() + "\\CorpusAvgDocLength.txt");
        try {
            br = new BufferedReader(new FileReader(file));
            if ((st = br.readLine()) != null) {
                answer = Integer.parseInt(st);
            }
        } catch (Exception e) {

        }
        return answer;
    }

    public int getTotalDocumentsNumber() {
        BufferedReader br;
        int answer = 0;
        String st = "";
        File file = new File(Model.getInstance().getSavePath() + "\\CorpusTotalDocNumber");
        try {
            br = new BufferedReader(new FileReader(file));
            if ((st = br.readLine()) != null) {
                answer = Integer.parseInt(st);
            }
        } catch (Exception e) {

        }
        return answer;
    }
}