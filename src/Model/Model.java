package Model;

import Model.Excpetions.BadPathException;
import Model.Excpetions.SearcherException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Model {

    private String savePath;
    private String workPath;


    private static Model singleton = null;
    private ReadFile readFile = new ReadFile("", "", false);
    //TODO WORKPATH FOR READQUERY
    private ReadQuery readQuery = new ReadQuery("", "", false);

    private Model() {
    }

    public static Model getInstance() {
        if (singleton == null)
            singleton = new Model();
        return singleton;

    }

    public String getSavePath(){return this.savePath;}

    public String getWorkPath(){return this.workPath;}

    public void mergePartialPosting(String workPath, String savePath) {
        this.savePath=savePath;
        this.workPath=workPath;
        this.readFile.p.getIndexer().mergePartialPosting(workPath, savePath);
    }

    public void parse(String workPath, String savePath, boolean checkbox_value) throws SearcherException, IOException {
        this.savePath=savePath;
        this.workPath=workPath;
        //read corpus files from folder
        this.readFile = new ReadFile(workPath, savePath, checkbox_value);
        readFile.listf(workPath + "\\corpus");
//        System.out.println("total number of docs that parsed: " + readFile.getNumberOfParsedDocs());
//        System.out.println("city with most shows: " + readFile.p.cityname + " number of appearance " + readFile.p.maxcount); // TODO: 12/9/2018 remove

    }


    public void resetButton(String savePath) throws SearcherException {
        this.savePath=savePath;
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
        //clear data- need more
//        Parse.clearData();
    }

    public void showDic(String savePath) throws IOException {
        this.savePath=savePath;
        File fromFile = new File(savePath + "\\Dictionary.txt");
        Desktop.getDesktop().open(fromFile);

    }

    public void loadDic(String savePath) {

        this.savePath=savePath;

        //TODO remove this:        readFile.p.getIndexer().loadDic(savePath);??????
        LoadedDictionary loadedDictionary = new LoadedDictionary(savePath);
        try {
            loadedDictionary.loadDic();
        } catch (Exception e) {
            loadedDictionary = null;
        }
    }

    public void merg_func(String workPath, String savePath) {
        this.savePath=savePath;
        this.workPath=workPath;

        readFile.p.getIndexer().mergePartialPosting(workPath, savePath);
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

    public void runQuery(String queryText, String workPath, String savePath, boolean checkbox_value) {
        this.savePath=savePath;
        this.workPath=workPath;

        Query q = readQuery.ParseQueryString(queryText);
        List<Query> queries = new LinkedList<>();
        queries.add(q);
    }

    // TODO: 12/22/2018  queryText
    // TODO: 12/23/2018  should have postings and dictionary on disk for stemmed/unstemmed.
    public void runQueryFile(String queryText, String workPath, String savePath, boolean checkbox_value, List<String> chosenCities) {
        try {
            this.savePath=savePath;
            this.workPath=workPath;

            File queryFile = new File(queryText);
            if (!queryFile.exists())
                System.out.println("error in file query load"); // TODO: 22/12/2018 throw exception  Itzik
            ReadQuery read = new ReadQuery(workPath,savePath,checkbox_value);
            this.readQuery=read;
            ArrayList<Query> queriesToRanker = readQuery.ParseQueryFile(queryFile);
            Ranker ranker = new Ranker(queriesToRanker,chosenCities);
            ranker.filterDocsByCities();
            int docsNumber = ranker.getTotalDocumentsNumber();
            double avgDL = ranker.getAverageDocumentLength();
            Map<String,Double>[] allQueriestResults = new HashMap[queriesToRanker.size()];



            //sorted


            for(int i = 0 ; i < queriesToRanker.size();i++){
                //for one query: FQID[0]->firstTerm (string doc, int tf)(string doc, int tf)
                //               FQID[1]->secondTerm(...)
                Map<String,Integer>[] FQID = ranker.getDocsAndTfForEachTerm(i);//for one query! String is doc, integer is tf,

                Map<String,Double> qResult = ranker.applyBM25Algorithm(FQID,avgDL,docsNumber);// doc1 0.8  doc2 0.1 ...

                // each cell of array shows ordered docs result of a query.(remove integer)
                allQueriestResults[i]=qResult;//add specific query result (=ordered doc list).


            }
            for(int i = 0; i < allQueriestResults.length;i++){
                
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public HashMap<String, String> getCountrList() {
        return Country.getDocs();
    }

    public List<String> loadCity(String savePath) throws IOException {
        this.savePath=savePath;

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
}