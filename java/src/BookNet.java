import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookNet {
    private int queueSize;
    private String directory;
    private AdjacencyMatrix matrix;
    private List<String> names;
    private List<Matcher> matchers;

    public BookNet(String directory, int queueSize) {
        this.directory = directory;
        names = new ArrayList<>();
        matchers = new ArrayList<>();
        getNames();
        matrix = new AdjacencyMatrix(names);
        this.queueSize = queueSize;
    }

    private void getNames() {
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
            matchers.add(Pattern.compile(next).matcher(""));
        }
    }

    public void doAll() {
        File[] fileArray = new File(directory).listFiles();
        List<File> fileList = Arrays.asList(fileArray);
        for (File file:fileList) {
            if (file.equals(new File(directory + "//names.txt"))) continue;
            String name = file.getAbsolutePath();
            doOneFile(name);
        }
        matrix.print();
    }

    private void doOneFile(String fullFilePath) {
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
        Queue<String> words = new ArrayDeque<>(queueSize);

        int currentWord = 0;
        while (scanner.hasNext()) {
            currentWord++;
            if (currentWord % 1000 == 0) System.out.println(currentWord);
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
        new BookNet("data", 15).doAll();
//        System.out.println("Hello Jon how are you".matches(" Jon"));
//        Pattern pattern = Pattern.compile(" Jon");
//        Matcher matcher = pattern.matcher("Hello sJon's how are you");
//        System.out.println(matcher.find());
//        matcher.reset("Hello Jon's how are you");
//        System.out.println(matcher.find());
    }
}
