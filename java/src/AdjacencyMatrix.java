import java.util.*;

public class AdjacencyMatrix {
    private Map<String, Integer> indices;
    private Vector<Vector<Count>> adjacencyMatrix;
    private int dim;

    public AdjacencyMatrix(List<String> names) {
        dim = names.size();
        indices = new HashMap<>();
        adjacencyMatrix = new Vector<>(dim);
        for (int i = 0; i < dim; i++) {
            indices.put(names.get(i), i);
            adjacencyMatrix.add(new Vector<>(dim));
            for (int j = 0; j < dim; j++) {
                adjacencyMatrix.get(i).add(j, new Count(0));
            }
        }
    }

    public void increment(Pair pair) {
        Integer index1 = indices.get(pair.getX());
        Integer index2 = indices.get(pair.getY());
        if (index1 == null || index2 == null) return;

        adjacencyMatrix.get(index1).get(index2).increment();
        adjacencyMatrix.get(index2).get(index1).increment();
    }

    public double[][] getAdjacencyMatrix() {
        double[][] toReturn = new double[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                toReturn[i][j] = adjacencyMatrix.get(i).get(j).getCount();
            }
        }

        return toReturn;
    }

    public void print() {
        for (int i = 0; i < dim; i++) {
            System.out.println(adjacencyMatrix.get(i));
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
        System.out.println(Arrays.deepToString(matrix.getAdjacencyMatrix()));
    }
}
