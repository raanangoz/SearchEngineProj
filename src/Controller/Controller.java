package Controller;

import Model.Excpetions.SearcherException;
import Model.Model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Controller {
    private Model model;
    private static Controller singleton = null;

    private Controller() {
        this.model = Model.getInstance();
    }

    public static Controller getInstance() {
        if (singleton == null)
            singleton = new Controller();
        return singleton;
    }

    public void mergePartialPosting(String workPath, String savePath) throws SearcherException, IOException {
        model.mergePartialPosting(workPath, savePath);
    }

    public void parse(String workPath, String savePath, boolean checkbox_value) throws SearcherException, IOException {
        model.parse(workPath, savePath, checkbox_value);
    }

    public void resetButton(String savePath) throws SearcherException {
        model.resetButton(savePath);
    }

    public void showDic(String savePath) throws SearcherException, IOException {
        model.showDic(savePath);
    }

    public void loadDic(String savePath) throws SearcherException, IOException {
        model.loadDic(savePath);
    }

    public void merg_func(String workPath, String savePath) {
        model.merg_func(workPath, savePath);
    }

    public void writeLastDocsToDisk(String savePath) {
        model.writeLastDocsToDisk(savePath);
    }

    public int getNumberOfIndexed() {
        return model.getNumberOfIndexed();
    }

    public int getDicSize(){
        return model.getDicSize();
    }

    public void runQuery(String queryPath,String workPath, String savePath, boolean checkbox_value) {
        model.runQuery(queryPath,workPath, savePath, checkbox_value);
    }

    public void runQueryFile(String queryText,String workPath, String savePath, boolean checkbox_value,List<String> chosenCities) throws IOException {
        model.runQueryFile(queryText, workPath, savePath, checkbox_value, chosenCities);
    }

    public HashMap<String, String> getCountryList() {
        return model.getCountrList();
    }

    public  List<String> loadCity(String savePath) throws IOException {
        return model.loadCity(savePath);
    }
}