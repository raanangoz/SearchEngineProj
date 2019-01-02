package Model;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadQuery {

    private Searcher pq;

    public ReadQuery(String workPath, String savePath, boolean checkbox_value) {
        pq = new Searcher(workPath, savePath, checkbox_value);
    }

    public Query ParseQueryString(String queryText) {
        Query q = new Query("", queryText, "", "");
        q = pq.parse(q, false);
        return q;
    }

    public ArrayList<Query> ParseQueryFile(File f, boolean stemming) throws IOException {
        ArrayList<Query> queries = new ArrayList<>();
        StringBuilder QueryTitle = new StringBuilder();
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(f));
        String st;

        String ParseMe = "";
        int i = 0;
        String queryNum = "";
        String queryDesc = "";
        while ((st = br.readLine()) != null) {
            if (st.equals("<top>")) {
                QueryTitle = new StringBuilder();
            } else if (st.startsWith("<title>")) {
                ParseMe = st;
            } else if (st.equals("</top>")) {
                Query q = new Query(queryNum, ParseMe.substring(8), queryDesc, "");
                q = pq.parse(q, stemming);
                queries.add(q);
            } else if (st.startsWith("<num>")) {
                String[] words = st.split(" ");
                queryNum = words[words.length - 1];
            } else if (st.startsWith("<desc>")) {
                String desc = "";
                st = br.readLine();
                while (!st.startsWith("<narr>")) {
                    desc += st;
                    st = br.readLine();
                }
                queryDesc = desc;
            }
        }
        br.close();
        return queries;
    }
}