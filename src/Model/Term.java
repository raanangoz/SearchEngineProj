package Model;

import java.util.LinkedHashMap;

public class Term {
    private int DF;
    private String name;
    private LinkedHashMap<Doc, Integer> postingList;

    public Term(String name) {
        this.name = name;
        postingList = new LinkedHashMap<>();
        DF = 0;
    }

    public void addToPostlist(Doc doc, int TF) {
        postingList.put(doc, TF);
    }

    public void increaseDF() {
        this.DF++;
    }

    public void updateName(String newName) {
        this.name = newName;
    }
}

