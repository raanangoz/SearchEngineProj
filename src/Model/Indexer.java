package Model;

import Model.Excpetions.SearcherException;
import Model.Excpetions.SuccessException;

import java.io.*;
import java.util.*;

public class Indexer {
    private boolean onetime = true;
    private int postingCounter;
    private HashMap<String, PostingList>[] dictionary;

    public Indexer() {
        postingCounter = 0;
        dictionary = new HashMap[27];
        for (int i = 0; i < dictionary.length; i++)
            dictionary[i] = new HashMap<>();
    }

    private void saveTermsOnDoc(Doc currentDoc, LinkedHashMap<String, Integer>[] terms) {
        currentDoc.setDocTerms(terms);
    }

    public int getDicSize() {
        int size = dictionary.length;
        return size;
    }

    public int getDF(String term) {
        int location = correctCellDictionary(term);
        if (null != dictionary[location].get(term))
            return dictionary[location].get(term).getDF();
        return 0;
    }


    public void finishDocAlgorithm(Doc currentDoc, LinkedHashMap<String, Integer>[] terms, String savepath) {
        saveTermsOnDoc(currentDoc, terms);
        currentDoc.setNumberOfDifferentWords();
        currentDoc.setMostFrequentTermValue();
        currentDoc.setDocumentLength();
        Parse.saveTFandUniq(currentDoc);
        updateDictionary(currentDoc, terms);

        if ((ReadFile.getFileIndex() % 10 == 0) && (onetime == true)) {
            writeToDiskAlgorithm(savepath);
        }
        if ((ReadFile.getFileIndex() % 10) - 1 == 0 && ReadFile.getFileIndex() != 1)
            onetime = true;
    }


    private void writeToDiskAlgorithm(String savepath) {
        writePartialPostToDisk(savepath);
        deletePostingFromMemory();
        onetime = false;
    }

    private void deletePostingFromMemory() {
        for (int i = 0; i < dictionary.length; i++) {
            for (Map.Entry<String, PostingList> entry : dictionary[i].entrySet()) {
                entry.getValue().clearPosts();
            }
        }
    }

    public HashMap<String, PostingList>[] getDictionary() {
        return dictionary;
    }


    private void updateDictionary(Doc currentDoc, LinkedHashMap<String, Integer>[] terms) {
        for (int i = 0; i < terms.length; i++) {
            for (Map.Entry<String, Integer> entry : terms[i].entrySet()) {
                String newTerm = entry.getKey();
                // If this is first occurrence of element
                // Insert the element
                int where = correctCellDictionary(newTerm);

                if (dictionary[where].get(newTerm) == null) {
                    PostingList p = new PostingList(newTerm);
                    dictionary[where].put(newTerm, p);
                }
                // If elements already exists in hash map
                // Increment the count of element by 1
                int tf = terms[i].get(newTerm).intValue();
                dictionary[where].get(newTerm).add(currentDoc, tf);
                dictionary[where].get(newTerm).increaseDF();
                dictionary[where].get(newTerm).setTotalOccurrences(tf);

            }
        }
    }

    public void loadDic(String savePath) throws SearcherException, IOException {
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
            PostingList p = new PostingList(term);
            p.setDF(DF);
            dictionary[location].put(term, p);

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


    public void writePartialPostToDisk(String savepath) {
        for (int i = 0; i < dictionary.length; i++) {
            File tofile = null;
            FileWriter fw = null;
            BufferedWriter bw = null;

            try {
                tofile = new File(savepath + "\\group " + i + " iter " + postingCounter + ".txt");
                fw = new FileWriter(tofile);
                bw = new BufferedWriter(fw);
                for (Map.Entry<String, PostingList> entry : dictionary[i].entrySet()) {
                    if (entry.getValue().getPosts().size() > 0) {
                        bw.write(entry.getKey() + " -> ");
                        bw.write(entry.getValue().toString());
                        bw.newLine();
                    }
                }
                bw.close();
                fw.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        postingCounter++;
    }

    public void mergePartialPosting(String workPath, String savePath) {


        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        int index;
        //get list of txt files
        File folder = new File(savePath);
        File[] files = folder.listFiles();
        File[] txtFiles = new File[files.length];
        int count = 0;
        File fromFile;
        BufferedReader br = null;
        for (File file : files) {
            if (file.getAbsolutePath().endsWith(".txt")) {
                txtFiles[count] = file;
                count++;
            }
        }
        for (int i = 0; i < 27; i++) {
            for (File file : txtFiles) {

                if (!file.isDirectory() && file.getName().startsWith("group " + i + " iter ")) {

                    try {
                        fromFile = new File(file.getAbsolutePath());
                        br = new BufferedReader(new FileReader(fromFile));
                        String st;
                        String[] wordLine;
                        String term;
                        String termLocation;
                        TreeMap<String, String> dictionary1 = new TreeMap();
                        while ((st = br.readLine()) != null) {
                            wordLine = st.split(" ", 3);
                            term = wordLine[0];
                            termLocation = wordLine[2];
                            dictionary1.put(term, termLocation);
                        }
                        br.close();
                        list.add(dictionary1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Map<String, String> merged = new TreeMap<>();
            list.forEach(d ->
                    d.forEach((k, v) ->
                            merged.merge(k, v, (v1, v2) -> v1 + " " + v2)
                    )
            );
            Map<String, String> mergeUpAndLow = new TreeMap<>();
            for (Map.Entry<String, String> entry : merged.entrySet()) {
                String key = entry.getKey();
                String oldValueOfLowerCase;
                String oldValueOfUpperCase;
                if (key.charAt(0) >= 'A' && key.charAt(0) <= 'Z')
                    if (merged.containsKey(key.toLowerCase())) {
                        oldValueOfLowerCase = merged.get(key.toLowerCase());
                        //merged.remove(key.toLowerCase());
                        oldValueOfUpperCase = merged.get(key);
                        // merged.remove(key);
                        mergeUpAndLow.put(key.toLowerCase(), oldValueOfUpperCase + oldValueOfLowerCase);
                    }
            }
            for (Map.Entry<String, String> entry : mergeUpAndLow.entrySet()) {
                merged.remove(entry.getKey().toUpperCase());
                merged.put(entry.getKey(), entry.getValue());
            }
            writeFinalPosting(merged, i, savePath);
            list.clear();

        }
        deletePostFiles(savePath);
        writeDicToDisk(savePath);
        writeCitysToDisk(savePath);
        writeCitysPointToDocToDisk(savePath);
        writeAvgDocsSize(savePath);
        writeEachDocSizeToDisc(savePath);
//        System.out.println("number of citys in corpus: " + Country.numberofcity); // TODO: 12/9/2018 remove


    }

    private void writeEachDocSizeToDisc(String savePath) {
        Map<String, List<Integer>> docDetails = (HashMap<String, List<Integer>>) Parse.getMaxtfandterm();
        File file;
        FileWriter fw;
        BufferedWriter bw;
        file = new File(savePath + "\\EachDocSize.txt");
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            for (Map.Entry<String, List<Integer>> entry : docDetails.entrySet()) {
                bw.write(entry.getKey().toString() + " --> " + entry.getValue().get(2));
                bw.write("\n");
            }
            bw.close();
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void writeAvgDocsSize(String savePath) {
        Map<String, List<Integer>> docDetails = (HashMap<String, List<Integer>>) Parse.getMaxtfandterm();
        double sum = 0;
        for (Map.Entry<String, List<Integer>> entry : docDetails.entrySet()) {
            sum += entry.getValue().get(2);
        }
        File file;
        FileWriter fw;
        BufferedWriter bw;
        file = new File(savePath + "\\CorpusAvgDocLength.txt");
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            sum=sum/ReadFile.getNumberOfParsedDocs();
            String writeMe=""+sum;
            bw.write(writeMe);
            bw.close();
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(sum);
    }

    private void writeCitysToDisk(String savePath) {
        HashMap<String, String> writeCountrys = Country.getDocs();
        File file;
        FileWriter fw;
        BufferedWriter bw;
        file = new File(savePath + "\\City.txt");
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            for (Map.Entry<String, String> entry : writeCountrys.entrySet()) {
                String writeMe = entry.getKey();
                bw.write(writeMe);
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void writeCitysPointToDocToDisk(String savePath) {
        HashMap<String, String> writeCountrys = Country.getDocs();
        File file;
        FileWriter fw;
        BufferedWriter bw;
        file = new File(savePath + "\\CityPointToDoc.txt");
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            for (Map.Entry<String, String> entry : writeCountrys.entrySet()) {
                String writeMe = entry.getKey() + " --> ";
                writeMe += entry.getValue();
                bw.write(writeMe);
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void deletePostFiles(String savePath) {
        List<File> resultList = new ArrayList<File>();
        File directory = new File(savePath);
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));

        for (File file : fList) {
            if (file.getName().startsWith("group "))
                file.delete();
        }
    }


    private void writeFinalPosting(Map<String, String> merged, int index, String savepath) {
        File file;
        FileWriter fw;
        BufferedWriter bw;
        file = new File("Posting " + index + ".txt");
        try {
            fw = new FileWriter(savepath + "\\" + file);
            bw = new BufferedWriter(fw);
            for (Map.Entry<String, String> entry : merged.entrySet()) {
                String term = entry.getKey();
                String valueR = entry.getValue();
                bw.write(term + " -> " + valueR);
                bw.newLine();

            }
            bw.close();
            fw.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private void writeDicToDisk(String savePath) {
        TreeMap<String, PostingList> sorted = new TreeMap<>();
        for (int i = 0; i < dictionary.length; i++)
            sorted.putAll(dictionary[i]);
        File file;
        FileWriter fw;
        BufferedWriter bw;


//        for (int i = 0; i < dictionary.length; i++) {
        file = new File(savePath + "\\Dictionary.txt");
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            for (Map.Entry<String, PostingList> entry : sorted.entrySet()) {
                String updateTermOnDic = entry.getKey();
                int df = entry.getValue().getDF();
                int totalOccurences = entry.getValue().getTotalOccurrences();
                bw.write(updateTermOnDic + " DF " + df + " totalTF " + totalOccurences);
//                bw.write(totalOccurences + "  " + updateTermOnDic);
                bw.newLine();

            }
            bw.close();
            fw.close();

        } catch (Exception e) {
            System.out.println(e);
//            }
        }
    }

    //unused function
    public void writeDocsToDisk(ArrayList<Doc> docArray) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("TheDocs.txt", true));
            for (Doc d : docArray) {
                writer.append("doc is" + d.getDocNo());
                writer.newLine();
                LinkedHashMap<String, Integer>[] docTermsToDisk = d.getDocTerms();
                for (int i = 0; i < docTermsToDisk.length; i++) {
                    docTermsToDisk[i].forEach((term, termtf) -> {
                        try {
                            writer.append(term + "=>" + termtf);
                            writer.newLine();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    });
                }
            }
            writer.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }


    public void changeToLowerCase(String upperTerm) {
        //update dictionary entry
        int i = correctCellDictionary(upperTerm.toLowerCase());
        PostingList temp = dictionary[i].get(upperTerm);
        dictionary[i].put(upperTerm.toLowerCase(), temp);
        dictionary[i].remove(upperTerm);

    }


    private class PostingList {
        int DF;
        LinkedHashMap<Doc, Integer> posts;
        //String address;
        int totalOccurrences;
        String stemmedWord;

        public String getStemmedWord() {
            return stemmedWord;
        }

        public void setStemmedWord(String stemmedWord) {
            this.stemmedWord = stemmedWord;
        }

        private PostingList(String newTerm) {
            DF = 0;
            posts = new LinkedHashMap<>();
            //address = "posts/Posting" + newTerm.charAt(0);
            totalOccurrences = 0;
            stemmedWord = null;

        }

        public void add(Doc doc, int tf) {
            posts.put(doc, tf);
        }

        public int getDF() {
            return this.DF;
        }

        public void increaseDF() {
            this.DF++;
        }

        public void setDF(int df) {
            this.DF = df;
        }

        public LinkedHashMap<Doc, Integer> getPosts() {
            return posts;
        }

        public int getTotalOccurrences() {
            return totalOccurrences;
        }

        public void setTotalOccurrences(int totalOccurrences) {
            this.totalOccurrences += totalOccurrences;
        }

        @Override
        public String toString() {
            String ans = "";
            for (Map.Entry<Doc, Integer> entry : posts.entrySet()) {
                String key = entry.getKey().getDocNo();
                Integer value = entry.getValue();
                ans += key + "[" + value + "] ";
            }

            return ans;
        }

        public void clearPosts() {
            this.posts.clear();
        }
    }
}

class CustomizedHashMap implements Comparator<Map.Entry<String, LinkedList<String>>> {

    @Override
    public int compare(Map.Entry<String, LinkedList<String>> o1, Map.Entry<String, LinkedList<String>> o2) {
        if (o1.getKey().compareTo(o2.getKey()) > 0)
            return 1;
        else if (o1.getKey().compareTo(o2.getKey()) < 0)
            return -1;
        else return 0;
    }
}
