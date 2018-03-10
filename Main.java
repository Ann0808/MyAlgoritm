package myalgoritm;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Main {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    String dirPath, stopListPath;
    int totalDocuments = 0;
    public static final String whiteSpacePattern = "\\s";
    ArrayList<String> stopWordList = new ArrayList<String>();
    TreeMap<String, Integer> wordList = new TreeMap<String, Integer>();
    TreeMap<String, TreeMap<String, Integer>> wordOccurrence = new TreeMap<String, TreeMap<String, Integer>>();
    TreeMap<String, TreeMap<String, Integer>> wordOccurrenceSmall = new TreeMap<String, TreeMap<String, Integer>>();
    Map<String, Integer> documentLength = new HashMap<String, Integer>();
    String punctuationMarks = "[^a-zA-Zа\\s]";

    public Main(String directory, String stoplist) {
        dirPath = directory;
        stopListPath = stoplist;
    }

    public double countTF(double wordsOccur, double wordsCount) {

        return wordsOccur/wordsCount;
    }

    public double countIDF(double documentsCount, double documentsWithWordCount) {

        return Math.log10(documentsCount/documentsWithWordCount);
    }

    /**
     *
     * @throws IOException
     *
     * Create a stop list from the stop-list file provided
     */
    public void createStopList() throws IOException {
        FileInputStream fstream = null;
        String line;
        try {
            fstream = new FileInputStream(stopListPath);
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist. Set correct path");
        }

        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        while((line = br.readLine()) != null) {
            try {
                stopWordList.add(Snow.stemSnowBall(line.trim().toLowerCase()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            //System.out.println("stop: "+stopWordList);
        }
    }

    /**
     *
     * @param word
     * @return Whether the term is a stop list word or not
     */
    public boolean isStopListWord(String word) {
        if (stopWordList.contains(word.toLowerCase()))
            return true;

        return false;
    }


    /**
     *
     * @param filePath
     * @param fileName
     * @throws IOException
     *
     * Parse a single file having path filePath and name fileName and populate word list and posting data
     */
    private void parseFile(String filePath, String fileName) throws IOException {
        FileInputStream fstream = null;
        String line, word;
        TreeMap<String, Integer> wordFreq = null;
        int wordCounter = 0, i;

        try {
            fstream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist. Set correct path");
        }

        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        while((line = br.readLine()) != null) {
            if (line.length()==0) {
                continue;
            }
            line = line.replaceAll(punctuationMarks,"");
            String wordsInLine[] = line.split(whiteSpacePattern);
            for (i = 0; i < wordsInLine.length; i++) {
                word = wordsInLine[i].trim().toLowerCase();

                if(word.length()==0) {
                    continue;
                }


                try {
                    word = Snow.stemSnowBall(word);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (isStopListWord(word)) {
                    wordCounter+=1;
                    continue;
                }

                if (wordList.containsKey(word)) {
                    wordFreq = wordOccurrence.get(word);
                    if (wordFreq.containsKey(fileName)) {
                        wordFreq.put(fileName, wordFreq.get(fileName) + 1);
                    } else {
                        wordFreq.put(fileName, 1);
                        wordList.put(word, wordList.get(word) + 1);
                    }
                } else {
                    wordList.put(word, 1);
                    wordFreq = new TreeMap<String, Integer>();
                    wordFreq.put(fileName, 1);
                    wordOccurrence.put(word, wordFreq);
                }
                wordCounter+=1;
            }
        }

        in.close();
        documentLength.put(fileName, wordCounter);
    }

    public void iterateOverDirectory() throws IOException {
        File dir = new File(dirPath);
        for (File file : dir.listFiles()) {
            if (file.getName().equals(".") ||
                    file.getName().equals("..") ||
                    file.isHidden() ||
                    file.isDirectory())
                continue;

            parseFile(file.getAbsolutePath(), file.getName());
            totalDocuments++;

        }


        //System.out.println("total documents: "+ totalDocuments);

        for (String word: stopWordList
             ) {
            //System.out.println("stop-word: " + word);
        }

        for (Map.Entry<String, Integer> entry : wordList.entrySet()) {
            //System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue());
        }

        for (Map.Entry<String,TreeMap<String,Integer>> entry : wordOccurrence.entrySet()) {
            double tf=0, idf=0;
            String word = entry.getKey();
            System.out.println("word: " + word);
            for (Map.Entry<String,Integer>entryEntry:
                 entry.getValue().entrySet()) {

                Integer count = entryEntry.getValue();
                String file = entryEntry.getKey();
                System.out.println("file: " + file + " count: " + count);
                tf = this.countTF((double)count,(double)documentLength.get(file));
                idf = this.countIDF((double)totalDocuments,(double)entry.getValue().size());
            }

            System.out.println("tf: "+tf);
            System.out.println("idf: "+tf);
            if (tf*idf>0.01){
                System.out.println(ANSI_RED + "tf-idf: " + tf*idf+ANSI_RESET);
            }
            System.out.println("countDocum: "+ entry.getValue().size());
        }

//        for (Map.Entry<String,TreeMap<String,Integer>> entry : wordOccurrence.entrySet()) {
//            System.out.println("word: " + entry.getKey());
//            for (Map.Entry<String,Integer>entryEntry:
//                    entry.getValue().entrySet()) {
//                System.out.println("file: " + entryEntry.getKey() + " count: " + entryEntry.getValue());
//            }
//        }

    }

    public static void main(String[] args) {

        Main myObject = new Main("/home/anna/textsForLSI","/home/anna/projects/LSA/stop.txt");

        try {
            myObject.createStopList();
            myObject.iterateOverDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
