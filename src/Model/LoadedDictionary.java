package Model;

import Model.Excpetions.SearcherException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class LoadedDictionary {

    private String savePath;
    private boolean stemmimng;
    private static HashMap<String, Integer>[] dictionary;

    public LoadedDictionary(String savePath, boolean stemmimng) {
        dictionary = new HashMap[27];
        for (int i = 0; i < dictionary.length; i++)
            dictionary[i] = new HashMap<>();
        this.savePath = savePath;
        this.stemmimng = stemmimng;
    }

    public void loadDic() throws SearcherException, IOException {
        String path;
        if (this.stemmimng == false)
            path = savePath + "\\Dictionary.txt";
        else
            path = savePath + "\\DictionaryS.txt";
        File fromFile = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(fromFile));
        String st;
        String[] words;
        String term = "";
        int DF = 0;
        while ((st = br.readLine()) != null) {
            term = "";
            words = st.split(" ");
            int i;
            for (i = 0; i < words.length && !words[i].equals("DF"); i++) {
                term += (words[i] + " ");

            }
            term = term.substring(0, term.length() - 1);
            DF = Integer.parseInt(words[i + 1]);

            int location = correctCellDictionary(term);
            dictionary[location].put(term, DF);

        }//while
        br.close();
    }

    private int correctCellDictionary(String termToFind) {

        if (termToFind.charAt(0) >= 'a' && termToFind.charAt(0) <= 'z')
            return (int) termToFind.charAt(0) - 97;
        else if (termToFind.charAt(0) >= 'A' && termToFind.charAt(0) <= 'Z')
            return (int) termToFind.charAt(0) - 65;
        else
            return 26;
    }

    public static HashMap<String, Integer>[] getDictionary() {
        return dictionary;
    }
}
