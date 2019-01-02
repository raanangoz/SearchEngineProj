package Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Query implements Serializable {
    String queryNum;
    String queryTitle;
    String queryDesc;
    String queryNarr;
    Map<String, Integer> terms;


    public Query(String queryNum, String queryTitle, String queryDesc, String queryNarr) {
        this.queryNum = queryNum;
        this.queryTitle = queryTitle;
        this.queryDesc = queryDesc;
        this.queryNarr = queryNarr;
        this.terms = new HashMap<>();
    }

    //<editor-fold desc="Getter and Setter">
    public String getQueryNum() {
        return queryNum;
    }

    public Map<String, Integer> getTerms() {
        return terms;
    }

    public void setTerms(Map<String, Integer> terms) {
        this.terms = terms;
    }

    public void addTerms(Map<String, Integer> newTerms) {
        int c;
        for (Map.Entry<String, Integer> s : newTerms.entrySet())
            if (!terms.containsKey(s.getKey()))
                terms.put(s.getKey(), 1);
            else {
                c = terms.get(s.getKey());
                terms.put(s.getKey(), ++c);
            }
    }

    public void setQueryNum(String queryNum) {
        this.queryNum = queryNum;
    }

    public String getQueryTitle() {
        return queryTitle;
    }

    public void setQueryTitle(String queryTitle) {
        this.queryTitle = queryTitle;
    }

    public String getQueryDesc() {
        return queryDesc;
    }

    public void setQueryDesc(String queryDesc) {
        this.queryDesc = queryDesc;
    }

    public String getQueryNarr() {
        return queryNarr;
    }

    public void setQueryNarr(String queryNarr) {
        this.queryNarr = queryNarr;
    }

    public void removeTerm(Map.Entry<String, Integer> s) {
        if (terms.containsKey(s.getKey()))
            terms.remove(s);
    }
    //</editor-fold>
}