package Model;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ReadQuery {
    public ParseQuery pq;

    public ReadQuery(String queryText) {
        pq = new ParseQuery(queryText);
    }


    public void ParseQueryString(String queryText) {
        Query q = new Query("",queryText,"","");
        pq.parse(q);
    }

    public void ParseQueryFile(File f) {
        StringBuilder QueryTitle = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()))) {
            String st;
            String ParseMe="";
            String[] wordLine={};
            int i = 0;
            while ((st = br.readLine()) != null) {
                if (st.equals("<top>")) {
                    QueryTitle = new StringBuilder();
                }
                if (st.startsWith("<title>")) {
                    ParseMe = st;
                }
                if (st.equals("</top>")) {
                    Query PQ = new Query("",ParseMe,"","");
                    p.parse(PQ);
                }
            }
        } catch (
                Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
