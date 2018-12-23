package Model;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class ReadQuery {

    private Searcher pq;

    public ReadQuery(String workPath, String savePath, boolean checkbox_value) {
        //TODO WORKPATH ETC...
        pq = new Searcher(workPath, savePath, checkbox_value);
    }


    public Query ParseQueryString(String queryText) {
        Query q = new Query("", queryText, "", "");
        q = pq.parse(q);
        return q;
    }

    public List<Query> ParseQueryFile(File f) {
        StringBuilder QueryTitle = new StringBuilder();
        List<Query> queries = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()))) {
            String st;
            String ParseMe = "";
            String[] wordLine = {};
            int i = 0;
            while ((st = br.readLine()) != null) {
                if (st.equals("<top>")) {
                    QueryTitle = new StringBuilder();
                }
                if (st.startsWith("<title>")) {
                    ParseMe = st;
                }
                if (st.equals("</top>")) {
                    Query q = new Query("", ParseMe.substring(8), "", "");
                    q = pq.parse(q);
                    queries.add(q);
                }
            }
            return queries;
        } catch (
                Exception e) {
            System.out.println(e.getMessage());
        }

        return queries;
    }
}
