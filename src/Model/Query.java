package Model;

import java.io.Serializable;

public class Query implements Serializable {
    String queryNum;
    String queryTitle;
    String queryDesc;
    String queryNarr;

    public Query(String queryNum, String queryTitle, String queryDesc, String queryNarr) {
        this.queryNum = queryNum;
        this.queryTitle = queryTitle;
        this.queryDesc = queryDesc;
        this.queryNarr = queryNarr;
    }

    //<editor-fold desc="Getter and Setter">
    public String getQueryNum() {
        return queryNum;
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
    //</editor-fold>

}
