import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Object that reads a directory of text files and generates
 * an adjacency matrix of characters in those text files.
 * A connection is made when characters appear within
 * queueSize tokens of each other, delimited by spaces.
 * The directory must contain one "names.txt" file which
 * consists of regular expressions one each line to search for, for a
 * given character, ie "Jaime|Kingslayer" would match for
 * Jaime Lannister in ASOIAF.
 */
public class BookNet {
    private int queueSize;
    private String directory;
    private AdjacencyMatrix matrix;
    private List<String> names;

    /**
     * Initializes the BookNet object
     * @param directory path of the directory containing names.txt and other txt files
     * @param queueSize size of the window to check for matches
     */
    public BookNet(String directory, int queueSize) {
        this.directory = directory;
        names = new ArrayList<>();
        readNames();
        matrix = new AdjacencyMatrix(names);
        this.queueSize = queueSize;
    }

    /**
     * Get the regex of characters
     * @return string array of regular expressions of characters
     */
    public String[] getNames() {
        return names.toArray(new String[names.size()]);
    }

    /**
     * Read in the names from the names.txt file
     * @throws NoSuchFileException when names.txt not found
     */
    private void readNames() {
        FileInputStream nameStream;

        try {
            nameStream = new FileInputStream(directory + "//names.txt");
        } catch (FileNotFoundException e) {
            throw new NoSuchFileException();
        }
        Scanner scanner = new Scanner(nameStream);
        while (scanner.hasNext()) {
            String next = scanner.nextLine();
            names.add(next);
        }
    }

    /**
     * Calles doOneFile for every text file, on a new thread
     */
    public void doAll() {
        File[] fileArray = new File(directory).listFiles();
        List<File> fileList = Arrays.asList(fileArray);
        fileList = new ArrayList<>(fileList);
        fileList.remove(new File(directory + "//names.txt"));
        fileList
                .stream()
                .parallel()
                .map(File::getAbsolutePath)
                .forEach(this::doOneFile);
    }

    /**
     * Get the adjacency matrix
     * @return a double[][] adjacency matrix, for R
     */
    public double[][] getAdjacencyMatrix() {
        return matrix.getAdjacencyMatrix();
    }

    /**
     * For a given file, will iterate along on a queueSize-token
     * window. On each iteration, will put every character regular
     * expression that it sees in the window into a set. Every possible
     * pairing of these characters will be generated, and each pairing
     * will be incremented by one in the adjacency matrix.
     *
     * Additionally, a map of how many tokens a given token pair was seen
     * is kept. If a pair is seen, the count is set to queueSize. Then, at
     * each iteration, every count is decremented by one. This ensures that
     * if a pair is "seen," it cannot be seen again for queueSize tokens.
     * @param fullFilePath path of the file to analyze
     */
    private void doOneFile(String fullFilePath) {
        List<Matcher> matchers = new ArrayList<>(names.size());
        for (String name:names) {
            matchers.add(Pattern.compile(name).matcher(""));
        }
        //Create a scanner for the file
        FileInputStream stream;
        try {
            stream = new FileInputStream(fullFilePath);
        } catch (FileNotFoundException e) {
            throw new NoSuchFileException();
        }
        Scanner scanner = new Scanner(stream);

        //initialize a map of character pairs that have been seen
        Map<Pair, Count> seen = new HashMap<>();
        for (Pair pair:Pair.pairUp(names)) {
            seen.put(pair, new Count(0));
        }
        //Initialize the queue
        Queue<String> words = new LinkedBlockingQueue<>(queueSize);

        while (scanner.hasNext()) {
            //"Age" every entry in the seen map
            for (Map.Entry<Pair, Count> entry:seen.entrySet()) {
                entry.getValue().decrement();
            }

            //Add the new word to the queue, removing the oldest
            String token = scanner.next();
            boolean offered = words.offer(token);
            if (!offered) {
                words.remove();
                words.add(token);
            }

            //Build a string from the queue to apply regex
            StringBuilder currentWindowBuilder = new StringBuilder();
            for (String word:words) {
                currentWindowBuilder.append(" ");
                currentWindowBuilder.append(word);
            }
            String currentWindow = currentWindowBuilder.toString();

            //Initialize a set for characters in this window
            Set<String> inThisWindow = new HashSet<>();

            //Add every character I see to inThisWindow
            for (int i = 0; i < names.size(); i++) {
                matchers.get(i).reset(currentWindow);
                if (matchers.get(i).find()) {
                    inThisWindow.add(names.get(i));
                }
            }

            for (Pair pair:Pair.pairUp(inThisWindow)) {
                if (seen.get(pair).getCount() > 0) continue;
                matrix.increment(pair);
                seen.get(pair).setCount(queueSize);
            }
        }
    }

    public static void main(String[] args) {
        BookNet net = new BookNet("data", 15);
        net.doAll();
        System.out.println(net.getNames().length);
    }
}
