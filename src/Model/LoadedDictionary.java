package Model;

import Model.Excpetions.SearcherException;
import Model.Excpetions.SuccessException;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class LoadedDictionary {

    private String savePath;
    private static LoadedDictionary singleton = null;
    private HashMap<String, Integer>[] dictionary;

    public LoadedDictionary(String savePath){
        this.savePath=savePath;
    }
    public static LoadedDictionary getInstance(String savePath) {
        if (singleton == null)
            singleton = new LoadedDictionary(savePath);
        return singleton;
    }

    public void loadDic() throws SearcherException, IOException {
        File fromFile = new File(savePath + "\\Dictionary.txt");
        BufferedReader br = new BufferedReader(new FileReader(fromFile));
        String st;
        String[] words;
        String term = "";
        int DF = 0;
        //int TF=0;
        while ((st = br.readLine()) != null) {
            term = "";
            words = st.split(" ");
            int i;
            for (i = 0; i < words.length && !words[i].equals("DF"); i++) {
                term += (words[i]);

            }
            DF = Integer.parseInt(words[i + 1]);
            //TF = Integer.parseInt(words[i + 3]);


            int location = correctCellDictionary(term);
            dictionary[location].put(term, DF);

        }//while
        br.close();
        throw new SuccessException();

    }
    private int correctCellDictionary(String termToFind) {

        if (termToFind.charAt(0) >= 'a' && termToFind.charAt(0) <= 'z')
            return (int) termToFind.charAt(0) - 97;
        else if (termToFind.charAt(0) >= 'A' && termToFind.charAt(0) <= 'Z')
            return (int) termToFind.charAt(0) - 65;
        else
            return 26;
    }

    public HashMap<String, Integer>[] getDictionary() {
        return dictionary;
    }
}
