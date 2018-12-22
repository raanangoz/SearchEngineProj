package Model;


import Model.Excpetions.BadPathException;
import Model.Excpetions.SearcherException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadFile {
    public Parse p;
    private static int fileIndex;
    private int numbOfDocsParsed;

    public ReadFile(String workPath, String savePath, boolean checkbox_value) {
        p = new Parse(workPath, savePath, checkbox_value);
        fileIndex = 1;
        numbOfDocsParsed = 0;
        int i = 0;
    }

    public int getNumberOfParsedDocs() {
        return numbOfDocsParsed;
    }

    public static int getFileIndex() {
        return fileIndex;
    }

    public List<File> listf(String directoryName) throws IOException, SearcherException {
        File directory = new File(directoryName);
        if (!directory.exists())
            throw new BadPathException();
        List<File> resultList = new ArrayList<File>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        assert fList != null;
        resultList.addAll(Arrays.asList(fList));

        for (File file : fList) {
            if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        //for each file, send to parse and print time
        for (File file : fList) {
            if (!file.isDirectory())
                sendDocParse(file);
            fileIndex++;
        }
        return resultList;
    }


    public void sendDocParse(File f) {
        StringBuilder DocNum = new StringBuilder();
        StringBuilder DocText = new StringBuilder();
        StringBuilder DocDate = new StringBuilder();
        StringBuilder DocHT = new StringBuilder();
        StringBuilder DocCountry = new StringBuilder();
//        System.out.println(f);
        //go over each doc, and separate to doc/text and send to parser
        try (BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()))) {
            String st;
            int i = 0;
            while ((st = br.readLine()) != null) {
                if (st.equals("<DOC>")) {
                    DocText = new StringBuilder();
                    DocHT = new StringBuilder();
                    DocNum = new StringBuilder();
                    DocCountry = new StringBuilder();
                }
                if (st.equals("<TEXT>")) {
                    while (((st = br.readLine()) != null) && (!st.equals("</TEXT>"))) {
                        if (st.startsWith("<F P=104>")) {
                            String CountrysWholeText = st;
                            String[] wordLine = CountrysWholeText.split("\\s+");
                            if (wordLine.length > 3)
                                DocCountry.append(wordLine[2]);
                        }
                        DocText.append(st + " ");
                    }

                    // TODO: 07/12/2018 if above does not work, do like last time, was deleted in merge -parse top Itzik
//                    String cWord = words[i];
//                    if (!currentDoc.getCity().equals(""))
//                        if (cWord.equals(currentDoc.getCity())) {
//                            String index = ""+i;
//                            Country.setCityDocLocation(currentDoc.getDocNo(), index);
                }
                if (st.equals("<HT>")) {
                    while (((st = br.readLine()) != null) && (!st.equals("</HT>")))
                        DocHT.append(st);
                }

                if (st.startsWith("<F P=104>")) {
                    String CountrysWholeText = st;
                    String[] wordLine = CountrysWholeText.split("\\s+");
                    if (wordLine.length > 3)
                        DocCountry.append(wordLine[2]);
                }

                if (st.startsWith("<DOCNO>")) {
                    int j;
                    int k;
                    int docNoStartIndex = 0;
                    for (j = 0; j < st.length(); j++) {
                        if (st.charAt(j) == '>') {
                            break;
                        }
                    }
                    while (st.charAt(j + 1) == ' ')
                        j++;
                    docNoStartIndex = j;
                    int docNoEndIndex = 0;
                    for (k = docNoStartIndex + 1; k < st.length(); k++) {
                        if (st.charAt(k) == '<') {
                            while (st.charAt(k - 1) == ' ')
                                k--;
                            break;
                        }
                    }
                    docNoEndIndex = k;
                    String docno = st.substring(docNoStartIndex + 1, docNoEndIndex);
                    DocNum.append(docno);
                }

                //need only text now....
//                    if (st.equals("<DATE1>")){
//                        while ( ((st = br.readLine()) != null) || (st.equals("</DATE>")==false) ) {
//                            DocDate.append(st);
//                        }
//                    }
                //at end of doc, send to parse the doc
                if (st.equals("</DOC>")) {
                    Doc splitDoc = new Doc(DocHT.toString(), DocText.toString(), DocNum.toString());
                    if (DocCountry.toString() != null) {
                        splitDoc.setDocCountry(DocCountry.toString());
                        Country.setCityDocsList(splitDoc.getCity(), splitDoc.getDocNo());
                    }
                    DocCountry = null;
                    p.parse(splitDoc);
                    numbOfDocsParsed++;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updatefileIndex() {
        fileIndex++;
    }
}



