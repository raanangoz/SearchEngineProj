package Model;

import java.io.*;
import java.util.*;

//TODO ALL SPLITS (" ") SHOULD BE ("//S+") BUT CHECK THAT IT WORK.
public class Ranker {
    private HashSet<String> docsAfterFilterCities = new HashSet<>();
    private List<Query> queriesToRanker;
    private List<String> chosenCities;

    public Ranker(List<Query> queriesToRanker, List<String> chosenCities) {
        this.queriesToRanker = queriesToRanker;
        this.chosenCities = chosenCities;
    }


    public void filterDocsByCities() {
        //TODO ALSO FOR FP 104
        try {
            for (String city : chosenCities) {
                String[] words;
                String term = "";
                String st = "";
                int i = correctCellDictionary(city);
                File fromFile = new File(Model.getInstance().getSavePath() + "\\Posting " + i + ".txt");
                BufferedReader br = new BufferedReader(new FileReader(fromFile));
                String city1 = city.toUpperCase();
                String city2 = city.toLowerCase();
                while ((st = br.readLine()) != null) {
                    if (st.startsWith(city1) || st.startsWith(city2)) {
                        words = st.split(" ");
                        if (words[0].equals(city1) || words[0].equals(city2)) {
                            for (int k = 2; k < words.length; k++) {
                                docsAfterFilterCities.add(words[k]);
                            }
                            break;
                        }
                    }
                }
                br.close();
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
                        String[] postingList = st.split("\\s+"); //split with space
                        if (postingList[0].equals(queryWord)) {
                            for (int l = 2; l < postingList.length; l += 2) { //go over all the docs
                                if (docsAfterFilterCities.size() == 0) //if no country slected
                                    // now the postinglist[l] contains the tf aswell
                                    docsAndTF.put(postingList[l], Integer.parseInt(postingList[l + 1]));
                                else if (docsAfterFilterCities.contains(postingList[l])) //if doc is equale to a doc from cities
                                    docsAndTF.put(postingList[l], Integer.parseInt(postingList[l + 1])); //add it to list
                            }


                            if (docsAndTF.size() != 0) {
                                docsForEachTerm[wordIndexInQuery] = new HashMap<>();
                                docsForEachTerm[wordIndexInQuery].putAll(docsAndTF);
                            }
                            //TODO maybe if should include this line too
                            docsForEachTerm[wordIndexInQuery].put(queryWord, -1);//PLASTER to save the term.
                            wordIndexInQuery++;
                            br.close();
                            docsAndTF.clear();
                            break;
                        }
                        //else then keep looking for the word.
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

    //get doc size
    /*
    public double getDocSize() throws IOException {
        String savePath = Model.getInstance().getSavePath();
        File fromFile = new File(savePath + "\\CorpusAvgDocLength.txt");
        BufferedReader br = new BufferedReader(new FileReader(fromFile));
        String size = br.readLine();
        return Double.parseDouble(size);
    }
*/

    /*
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
*/

    /**
     * FQID IS ARRAY OF TERMS ( string,tf)! OF ONE QUERY.
     * the terms arent here, but only their posting lists.
     *
     * @param postsOfAllQueriesTerms: all postings of all terms that appeared on all queries.
     * @return
     */
    public Map<String, Double>[] applyBM25Algorithm(Map<String, Integer>[] postsOfAllQueriesTerms, double avgDL, int docsNumber, HashMap<String, Integer> docsLength) {//docsNumber - 470000
        double k1 = 1.35;
        double b = 0.25;
        double IDF = 0;
        Map<String, Double>[] results = new HashMap[queriesToRanker.size()];//each cell contains map of docno and docvalue

        for (int i = 0; i < results.length; i++) {
            Map<String, Double> docsAndValuesOfQuery = new HashMap<>();
            results[i] = new HashMap<>();
            Query q = queriesToRanker.get(i);
            Map<String, Integer> terms = q.getTerms();
            for (Map.Entry<String, Integer> entry : terms.entrySet()) {
                String term = entry.getKey();//we have a term, now rate it's docs.
                int k = correctCellDictionary(term);
                if (LoadedDictionary.getDictionary()[k].containsKey(term)) {
                    int DF = LoadedDictionary.getDictionary()[k].get(term);
                    IDF = Math.log((docsNumber - DF + 0.5) / (DF + 0.5));
                    //postOfAllQueriesTerm[k] is the posting list for the term. now rate docs.
                    for (String docNo : postsOfAllQueriesTerms[k].keySet()) {
                        if (docsAfterFilterCities.contains(docNo) || docsAfterFilterCities.size() == 0) {
                            if (postsOfAllQueriesTerms[k].get(docNo) != -1) {
                                int TF = postsOfAllQueriesTerms[k].get(docNo);
                                if (TF != -1) {//plaster because i insert the a term with value -1, and not only docNo-DF
                                    int DL = docsLength.get(docNo);
                                    double docRelevance = IDF * ((TF * (k1 + 1)) / (TF +( k1 * (1 - b + (b * DL / avgDL)))));
                                    if (docsAndValuesOfQuery.containsKey(docNo)) {

                                        docsAndValuesOfQuery.put(docNo, docsAndValuesOfQuery.get(docNo) + docRelevance);//update docrelevance because 2 terms of query existed on this doc.

                                    }
                                    else docsAndValuesOfQuery.put(docNo, docRelevance);
                                }
                            }
                        }

                    }
                }

                //sum all docs values for this query and save 50 highest and return for this query.
            }
            results[i].putAll(docsAndValuesOfQuery);
            //now next doc of same term.
        }
        return results;
    }


    public double getAverageDocumentLength () {
        BufferedReader br;
        double answer = 0;
        String st = "";
        File file = new File(Model.getInstance().getSavePath() + "\\CorpusAvgDocLength.txt");
        try {
            br = new BufferedReader(new FileReader(file));
            if ((st = br.readLine()) != null) {
                answer = Double.parseDouble(st);
            }
            br.close();
        } catch (Exception e) {

        }
        return answer;
    }

    public int getTotalDocumentsNumber () {
        BufferedReader br;
        int answer = 0;
        String st = "";
        File file = new File(Model.getInstance().getSavePath() + "\\CorpusTotalDocNumber.txt");
        try {
            br = new BufferedReader(new FileReader(file));
            if ((st = br.readLine()) != null) {
                answer = Integer.parseInt(st);
            }
            br.close();
        } catch (Exception e) {

        }
        return answer;
    }

    public HashMap<String, Integer>[] loadPostingListsForAllQueries (ArrayList < Query > queriesToRanker) {
        List<String>[] termsOfAllQueries = new LinkedList[27];//each cell contains only terms starting with same char.
        for (int i = 0; i < termsOfAllQueries.length; i++)
            termsOfAllQueries[i] = new LinkedList<>();
        for (Query q : queriesToRanker) {
            Map<String, Integer> terms = q.getTerms();
            for (Map.Entry<String, Integer> entry : terms.entrySet()) {
                String term = entry.getKey();
                int where = correctCellDictionary(term);
                termsOfAllQueries[where].add(term);
            }

        }

        HashMap<String, Integer>[] allPosts = new HashMap[termsOfAllQueries.length];//[0]:  gas -1 fbis3 5 fbis4 6...
        //allposts[i] contains random posting list of a term.
        for (int i = 0; i < 27; i++) {//iterate the terms
            allPosts[i] = new HashMap<>();
            int numOfPostingListsFoundForGroup = 0;//2 terms start with e, we want to open posting list e once.
            String st;
            File file = new File(Model.getInstance().getSavePath() + "\\Posting " + i + ".txt");
            try {
                BufferedReader fr = new BufferedReader(new FileReader(file));
                while ((st = fr.readLine()) != null) {//read all lines that might contain term's posts
                    for (String term : termsOfAllQueries[i]) {//terms in same group
                        if (st.startsWith(term)) {
                            String[] words = st.split("\\s+");
                            if (words[0].equals(term)) {
                                HashMap<String, Integer> postingList = new HashMap<>();
                                for (int k = 2; k < words.length; k += 2) {
                                    postingList.put(words[k], Integer.parseInt(words[k + 1]));
                                }
                                postingList.put(term, -1);//plaster to recognize the term.
                                numOfPostingListsFoundForGroup++;

                                allPosts[i].putAll(postingList);
                            }
                        }
                    }
                    if (numOfPostingListsFoundForGroup == termsOfAllQueries[i].size())
                        break;//dont read more lines , all postings of this group are in memory.
                }
                fr.close();

            } catch (Exception e) {

            }

        }
        return allPosts;
    }


    public HashMap<String, Integer> getAllDocsLengthsForQueriesGroup (Map < String, Integer >[]termsAndPostings){
        HashMap<String, Integer> docLengths = new HashMap<>();
        String st;
        BufferedReader br;
        File file = new File(Model.getInstance().getSavePath() + "\\EachDocSize.txt");
        try {
            br = new BufferedReader(new FileReader(file));
            Set<String> allDocNo = new HashSet<>();
            for (int i = 0; i < termsAndPostings.length; i++) {
                if (null != termsAndPostings[i])
                    for (String docNo : termsAndPostings[i].keySet()) {
                        allDocNo.add(docNo);
                    }
            }
            while ((st = br.readLine()) != null) {
                String[] line = st.split(" ");
                if (allDocNo.contains(line[0])) {
                    int DL = Integer.parseInt(line[1]);
                    docLengths.put(line[0], DL);
                    allDocNo.remove(line[0]);
//                    break;
                }
            }
            br.close();

        } catch (Exception e) {
            System.out.println(e);
        }
        return docLengths;
    }

    public List<String>[] get50relevant (Map < String, Double >[]allQueriestResults){
//TODO I DONT THINK THE 50 RETURNED ARE SORTED FROM HIGHEST TO LOWEST, BUT THEY ARE THE BEST 50.
        List<String>[] finalResult = new LinkedList[allQueriestResults.length];

        for (int i = 0; i < allQueriestResults.length; i++) {
            finalResult[i] = new LinkedList<>();
            List<Map.Entry<String, Double>> greatest = findGreatest(allQueriestResults[i], 50);
            for (Map.Entry<String, Double> entry : greatest) {

                finalResult[i].add(entry.getKey());
            }
        }


        return finalResult;
    }

    private static <String, Double extends Comparable<? super Double>>List<Map.Entry<String, Double>>
    findGreatest(Map < String, Double > map, int n){
        Comparator<? super Map.Entry<String, Double>> comparator =
                new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> e0, Map.Entry<String, Double> e1) {
                        Double v0 = e0.getValue();
                        Double v1 = e1.getValue();
                        return v0.compareTo(v1);
                    }
                };
        PriorityQueue<Map.Entry<String, Double>> highest =
                new PriorityQueue<Map.Entry<String, Double>>(n, comparator);
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            highest.offer(entry);
            while (highest.size() > n) {
                highest.poll();
            }
        }

        List<Map.Entry<String, Double>> result = new ArrayList<Map.Entry<String, Double>>();
        while (highest.size() > 0) {
            result.add(highest.poll());
        }
        return result;
    }

}





