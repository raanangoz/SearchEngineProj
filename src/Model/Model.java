package Model;

import Model.Excpetions.BadPathException;
import Model.Excpetions.SearcherException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Model {

    private static Model singleton = null;
    ReadFile readFile = new ReadFile("", "", false);
    ReadQuery readQuery = new ReadQuery("");

    private Model() {
    }

    public static Model getInstance() {
        if (singleton == null)
            singleton = new Model();
        return singleton;

    }

    public void mergePartialPosting(String workPath, String savePath) throws SearcherException, IOException {
        this.readFile.p.getIndexer().mergePartialPosting(workPath, savePath);
    }

    public void parse(String workPath, String savePath, boolean checkbox_value) throws SearcherException, IOException {
        //read corpus files from folder
        this.readFile = new ReadFile(workPath, savePath, checkbox_value);
        readFile.listf(workPath + "\\corpus");
//        System.out.println("total number of docs that parsed: " + readFile.getNumberOfParsedDocs());
//        System.out.println("city with most shows: " + readFile.p.cityname + " number of appearance " + readFile.p.maxcount); // TODO: 12/9/2018 remove

    }


    public void resetButton(String savePath) throws SearcherException {
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

    public void showDic(String savePath) throws SearcherException, IOException {
        File fromFile = new File(savePath + "\\Dictionary.txt");
        Desktop.getDesktop().open(fromFile);

    }

    public void loadDic(String savePath) throws SearcherException, IOException {
        readFile.p.getIndexer().loadDic(savePath);
    }

    public void merg_func(String workPath, String savePath) {
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

    public void runQuery(String queryText) {
        readQuery.ParseQueryString(queryText);
    }

    public void runQueryFile(String queryText) {
        try {
            File queryFile = new File(queryText);
            if (!queryFile.exists())
                System.out.println("error in file query load"); // TODO: 22/12/2018 throw exception  Itzik
            readQuery.ParseQueryFile(queryFile);
        } catch (Exception e) {
        }
    }
}
