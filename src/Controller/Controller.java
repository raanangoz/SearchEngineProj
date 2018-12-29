package Controller;

import Model.Excpetions.BadPathException;
import Model.Excpetions.SearcherException;
import Model.Model;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.IOException;
import java.util.ArrayList;
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

    public int getDicSize() {
        return model.getDicSize();
    }

    public void runQueryFile(String queryText, String workPath, String savePath, Boolean checkbox_semantic, boolean checkbox_value, List<String> chosenCities, boolean tosave, String savefolder) throws IOException, BadPathException {
        model.runQueryFile(queryText, workPath, savePath, checkbox_semantic, checkbox_value, chosenCities,tosave, savefolder);
    }

    public HashMap<String, String> getCountryList() {
        return model.getCountrList();
    }

    public List<String> loadCity(String savePath) throws IOException {
        return model.loadCity(savePath);
    }

    public void runQueryString(String queryText, String workPath, String savePath, boolean checkbox_semantic, boolean checkbox_stemming, List<String> chosenCities, boolean tosave, String saveFolder) throws IOException, BadPathException {
        model.runQueryString(queryText,workPath,savePath,checkbox_semantic,checkbox_stemming,chosenCities,tosave,saveFolder);
    }
}