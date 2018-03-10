package myalgoritm;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Main {

    String dirPath, stopListPath;
    int totalDocuments = 0;
    public static final String whiteSpacePattern = "\\s";
    ArrayList<String> stopWordList = new ArrayList<String>();
    TreeMap<String, Integer> wordList = new TreeMap<String, Integer>();
    TreeMap<String, TreeMap<String, Integer>> wordOccurrence = new TreeMap<String, TreeMap<String, Integer>>();
    TreeMap<String, Integer> documentLength = new TreeMap<String, Integer>();
    String punctuationMarks = "[^a-zA-Zа]";

    public Main(String directory, String stoplist) {
        dirPath = directory;
        stopListPath = stoplist;
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
            stopWordList.add(line.trim().toLowerCase());
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
            String wordsInLine[] = line.split(whiteSpacePattern);
            for (i = 0; i < wordsInLine.length; i++) {
                word = wordsInLine[i].trim().toLowerCase();
                word = word.replaceAll(punctuationMarks,"");

                if (isStopListWord(word)) {
                    wordCounter+=1;
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
            System.out.println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
        }

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
