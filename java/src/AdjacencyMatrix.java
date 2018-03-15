import java.util.*;

public class AdjacencyMatrix {
    private Map<String, Integer> indices;
    private double[][] adjacencyMatrix;
    private int dim;

    public AdjacencyMatrix(List<String> names) {
        dim = names.size();
        indices = new HashMap<>();
        for (int i = 0; i < dim; i++) {
            indices.put(names.get(i), i);
        }
        adjacencyMatrix = new double[dim][dim];
    }

    public void increment(Pair pair) {
        Integer index1 = indices.get(pair.getX());
        Integer index2 = indices.get(pair.getY());
        if (index1 == null || index2 == null) return;

        adjacencyMatrix[index1][index2]++;
        adjacencyMatrix[index2][index1]++;
    }

    public double[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public void print() {
        for (int i = 0; i < dim; i++) {
            System.out.println(Arrays.toString(adjacencyMatrix[i]));
        }
    }

    public static void main(String[] args) {
        List<String> list= new LinkedList<>();
        list.add("John");
        list.add("Fred");
        list.add("Jimmy");
        list.add("Billy");
        list.add("Bobby");
        AdjacencyMatrix matrix = new AdjacencyMatrix(list);
        matrix.increment(new Pair("John", "Fred"));
        matrix.increment(new Pair("John", "Fred"));
        matrix.increment(new Pair("Jimmy", "Billy"));
        matrix.print();
    }
}
