package Model;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

public class Doc implements Serializable {

    private String docName;
    private String docNo;
    private String Date;
    private String T1;
    private String text;
    private String city;
    private int mostFrequentTermValue;
    private int numberOfDifferentWords;
    private int documentLength;
    private LinkedHashMap<String, Integer>[] docTerms;
    private List<Map.Entry<String, Integer>> docDominantEntities;

    public Doc(String docName, String text, String DocNo) {
        this.docName = docName;
        this.text = text;
        this.docNo = DocNo;
        this.docTerms = new LinkedHashMap[27];
        this.city = "";
        this.documentLength = 0;


    }

    public void freeDocTerms() {
        for (int i = 0; i < docTerms.length; i++)
            docTerms[i].clear();
    }

    //<editor-fold desc="Getter and Setter">
    public int getDocumentLength() {
        return documentLength;
    }

    public LinkedHashMap<String, Integer>[] getDocTerms() {
        return docTerms;
    }

    public void setDocTerms(LinkedHashMap<String, Integer>[] fromDocTerms) {
        this.docTerms = fromDocTerms;
    }

    public String getCity() {
        return city;
    }

    public String getDocName() {
        return docName;
    }

    public String getDocNo() {
        return docNo;
    }

    public String getDate() {
        return Date;
    }

    public String getT1() {
        return T1;
    }

    public String getText() {
        return text;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setT1(String t1) {
        T1 = t1;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getMostFrequentTermValue() {
        return mostFrequentTermValue;
    }

    public int getNumberOfDifferentWords() {
        return numberOfDifferentWords;
    }



    public void setDocumentLength() {
        int ans = 0;
        for (LinkedHashMap<String, Integer> x : docTerms) {
            for (Integer value : x.values())
                ans += value;
        }
        this.documentLength = ans;
    }

    public void setNumberOfDifferentWords() {
        int ans = 0;
        for (LinkedHashMap<String, Integer> x : docTerms) {
            ans += x.size();
        }
        this.numberOfDifferentWords = ans;
    }

    public void setDocCountry(String docCity) {
        this.city = docCity;
    }



    public void setMostFrequentTermValue() {
        int max = 0;
        for (LinkedHashMap<String, Integer> x : docTerms) {
            if (x.size() > 0) {
                Comparator<Map.Entry<String, Integer>> comparator =
                        new Comparator<Map.Entry<String, Integer>>() {
                            public int compare(
                                    Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                                return e1.getValue().compareTo(e2.getValue());
                            }
                        };
                max = Math.max(Collections.max(x.entrySet(), comparator).getValue(), max);
            }

        }
        this.mostFrequentTermValue = max;
    }

    public List<Map.Entry<String, Integer>> getDocDominantEntities() {
        return docDominantEntities;
    }

    public void saveEntities() {
        int max = 0;
        LinkedHashMap<String, Integer> docEntities = new LinkedHashMap<>();
        for(int i = 0 ; i < docTerms.length;i++){

            Set<String> keys = docTerms[i].keySet();
            for(String k:keys){
                if(Character.isUpperCase(k.charAt(0))){
                    docEntities.put(k,docTerms[i].get(k));
                }

            }
        }
        docDominantEntities = findGreatest(docEntities,5);
    }
    private static <String, Double extends Comparable<? super Double>>List<Map.Entry<String, Double>>
    findGreatest(Map < String, Double > map, int n){
        Comparator<? super Map.Entry<String, Double>> comparator =
                new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> e0, Map.Entry<String, Double> e1) {
                        Double v0 = e0.getValue();
                        Double v1 = e1.getValue();
                        return v0.compareTo(v1);
                    }
                };
        PriorityQueue<Map.Entry<String, Double>> highest =
                new PriorityQueue<Map.Entry<String, Double>>(n, comparator);
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            highest.offer(entry);
            while (highest.size() > n) {
                highest.poll();
            }
        }

        List<Map.Entry<String, Double>> result = new ArrayList<Map.Entry<String, Double>>();
        while (highest.size() > 0) {
            result.add(highest.poll());
        }
        return result;
    }
}
//</editor-fold>
