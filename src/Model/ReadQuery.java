package Model;


import java.io.*;
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

    public List<Query> ParseQueryFile(File f) throws IOException {

        List<Query> queries = new LinkedList<>();
        StringBuilder QueryTitle = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String st;

        String ParseMe = "";
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
    }
}
