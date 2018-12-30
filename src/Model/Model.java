package Model;

import Model.Excpetions.BadPathException;
import Model.Excpetions.SearcherException;
import Model.Excpetions.SuccessException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Model {

    private String savePath;
    private String workPath;
    private boolean stemmimng = false;

    private static Model singleton = null;
    private ReadFile readFile = new ReadFile("", "", false);
    private ReadQuery readQuery = new ReadQuery("", "", false);

    private Model() {
    }

    public static Model getInstance() {
        if (singleton == null)
            singleton = new Model();
        return singleton;

    }

    public String getSavePath() {
        return this.savePath;
    }

    public String getWorkPath() {
        return this.workPath;
    }

    public Boolean getStemmimng() {
        return this.stemmimng;
    }

    public void mergePartialPosting(String workPath, String savePath) {
        this.savePath = savePath;
        this.workPath = workPath;
        this.readFile.p.getIndexer().mergePartialPosting(workPath, savePath, this.stemmimng);
    }

    public void parse(String workPath, String savePath, boolean checkbox_value) throws SearcherException, IOException {
        this.savePath = savePath;
        this.workPath = workPath;
        this.stemmimng = checkbox_value;
        //read corpus files from folder
        this.readFile = new ReadFile(workPath, savePath, checkbox_value);
        readFile.listf(workPath + "\\corpus");
//        System.out.println("total number of docs that parsed: " + readFile.getNumberOfParsedDocs());
//        System.out.println("city with most shows: " + readFile.p.cityname + " number of appearance " + readFile.p.maxcount);

    }


    public void resetButton(String savePath) throws SearcherException {
        this.savePath = savePath;
        File directory = new File(savePath);
        if (!directory.exists())
            throw new BadPathException();
        List<File> resultList = new ArrayList<File>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));

        for (File file : fList) {
            if (file.getName().endsWith(".txt"))
                file.delete();
        }
        Country.clear();
        readFile.p.getIndexer().cleardic();
        throw new SuccessException();
        //clear data- need more
//        Parse.clearData();
    }

    public void showDic(String savePath) throws IOException {
        this.savePath = savePath;
        File fromFile;
        if (this.stemmimng == false)
            fromFile = new File(savePath + "\\Dictionary.txt");
        else
            fromFile = new File(savePath + "\\DictionaryS.txt");
        Desktop.getDesktop().open(fromFile);

    }

    public void loadDic(String savePath) throws IOException, SearcherException {

        this.savePath = savePath;
        LoadedDictionary loadedDictionary = new LoadedDictionary(savePath, this.stemmimng);
        loadedDictionary.loadDic();
    }

    public void merg_func(String workPath, String savePath) {
        this.savePath = savePath;
        this.workPath = workPath;

        readFile.p.getIndexer().mergePartialPosting(workPath, savePath, this.stemmimng);
    }

    public void writeLastDocsToDisk(String savePath) {
        readFile.p.getIndexer().writePartialPostToDisk(savePath);
    }

    public int getNumberOfIndexed() {
        return readFile.getNumberOfParsedDocs();
    }

    public int getDicSize() {
        return readFile.p.getIndexer().getDicSize();
    }


    // TODO: 12/22/2018  queryText
    // TODO: 12/23/2018  should have postings and dictionary on disk for stemmed/unstemmed.
    public void runQueryFile(String queryText, String workPath, String savePath, boolean checkbox_semantic, boolean checkbox_value, List<String> chosenCities, boolean tosave, String savefolder) throws IOException, BadPathException {
        File queryFile = new File(queryText);
        if (!queryFile.exists())
            throw new BadPathException();
        if (tosave == true) {
            if (savefolder.length() < 1)
                throw new BadPathException();
        }
        ArrayList<Query> queriesToRanker = readQuery.ParseQueryFile(queryFile);
        runQuery(workPath, savePath, checkbox_semantic, checkbox_value, chosenCities, tosave, savefolder, queriesToRanker);
    }


    public void runQueryString(String queryText, String workPath, String savePath, boolean checkbox_semantic, boolean checkbox_value, List<String> chosenCities, boolean tosave, String savefolder) throws IOException, BadPathException {
        ArrayList<Query> queriesToRanker = new ArrayList<Query>();
        Query q = new Query("11111", queryText, "", "");
        String[] querytext = queryText.split(" ");
        Map<String, Integer> terms = new HashMap<>();
        for (int i = 0; i < querytext.length; i++) {
            terms.put(querytext[i], 1);
        }
        q.setTerms(terms);
        queriesToRanker.add(q);
        runQuery(workPath, savePath, checkbox_semantic, checkbox_value, chosenCities, tosave, savefolder, queriesToRanker);
    }


    public void runQuery(String workPath, String savePath, boolean checkbox_semantic,
                         boolean checkbox_value, List<String> chosenCities, boolean tosave, String savefolder, ArrayList<Query> queriesToRanker) throws IOException, BadPathException {

        ReadQuery read = new ReadQuery(workPath, savePath, checkbox_value);
        this.savePath = savePath;
        this.workPath = workPath;
        this.stemmimng = checkbox_value;
        this.readQuery = read;
        if (checkbox_semantic == true) {
            Map<String, Integer> tempTerms;
            Map<String, Integer> termsToAdd = new HashMap<>();
            for (Query q : queriesToRanker) {
                tempTerms = q.getTerms();
                for (Map.Entry<String, Integer> s : tempTerms.entrySet()) {
                    List<String> seManticTerms = findsimiliar(s.getKey());
                    for (String newTerm : seManticTerms) {
                        termsToAdd.put(newTerm, 1);

                    }
                }
                q.addTerms(termsToAdd);
            }
        }

        Ranker ranker = new Ranker(queriesToRanker, chosenCities);
        ranker.filterDocsByCities();
        int docsNumber = ranker.getTotalDocumentsNumber();
        double avgDL = ranker.getAverageDocumentLength();
        Map<String, Double>[] allQueriestResults = new HashMap[queriesToRanker.size()];
        // [docNo, grade], [docNo, grade],  [docNo, grade],  [docNo, grade],
        //sorted
        HashMap<String, Integer>[] relevantPostsForAllQueries = ranker.loadPostingListsForAllQueries(queriesToRanker);
        HashMap<String, Integer> docLengths = ranker.getAllDocsLengthsForQueriesGroup(relevantPostsForAllQueries);
        allQueriestResults = ranker.applyBM25Algorithm(relevantPostsForAllQueries, avgDL, docsNumber, docLengths);// doc1 0.8  doc2 0.1 ...

        //Map<String, Double>[]q= ranker.sortReturnedDocsByValue(allQueriestResults); // each cell of array contains sorted docs from most relevant to least.
        List<String>[] fiftyRelevantDocs = ranker.get50relevant(allQueriestResults); // each cell of array contains sorted docs from most relevant to least.
        //TODO SHOULD BE able to save ANYWHERE THAT USER CHOOSE by the instructions
        if (tosave == true) {
            File toFile = new File(savefolder + "\\results.txt");
            BufferedWriter bw = null;
            bw = new BufferedWriter(new FileWriter(toFile));
            for (int i = 0; i < fiftyRelevantDocs.length; i++) {
                String queryNum = queriesToRanker.get(i).getQueryNum();
                for (String docNo : fiftyRelevantDocs[i]) {
                    bw.write(queryNum + "\t" + "0\t" + docNo + "\t 1 \t 12.23 \t mt");
                    bw.newLine();
//                System.out.println(queryNum + "\t" + "0\t" + docNo + "\t 1 \t 12.23 \t mt");
                }
            }
            bw.close();
        }

        //        this.savePath = savePath;
//        this.workPath = workPath;
//        this.stemmimng = checkbox_value;
//
//        Query q = readQuery.ParseQueryString(queryText);
//        List<Query> queries = new LinkedList<>();
//        queries.add(q);
    }


    private List<String> findsimiliar(String key) {
        List<String> Final = new LinkedList<>();
        DatamuseQuery getData = new DatamuseQuery();
        String allData = getData.findSimilar(key);
        JSONArray array = new JSONArray(allData);
        for (int i = 0; i < array.length() && i < 5; i++) {
            JSONObject jsonObj = array.getJSONObject(i);
            String word = (jsonObj.getString("word"));
//            int score = (jsonObj.getInt("score"));
            Final.add(word);
        }
        return Final;
    }

    public HashMap<String, String> getCountrList() {
        return Country.getDocs();
    }

    public List<String> loadCity(String savePath) throws IOException {
        this.savePath = savePath;

        File fromFile = new File(savePath + "\\City.txt");
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(fromFile));
        List<String> AllCountrysList = new LinkedList<>();
        String st;
        while ((st = br.readLine()) != null) {
            AllCountrysList.add(st);
        }
        br.close();
        return AllCountrysList;
    }

    public void writeEntitiesToDisk(String workPath, String savePath) {
        readFile.p.getIndexer().writeEntitiesToDisk(savePath);
    }

    public HashMap<String,String> getEntities(String savePath) throws IOException {
        this.savePath = savePath;
        File fromFile = new File(savePath + "\\docsEntities.txt");
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(fromFile));
        HashMap<String,String> AllEntities = new HashMap<>();
        String st;
        while ((st = br.readLine()) != null) {
            String word[] = st.split("->");
            AllEntities.put(word[0].replaceAll("\\s",""),word[1]);
        }
        br.close();
        return AllEntities;
    }
}