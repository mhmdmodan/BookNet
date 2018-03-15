import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Pair {

    private String x;
    private String y;

    public Pair(String x, String y) {
        this.x = x;
        this.y = y;
    }

    public static Set<Pair> pairUp(Collection<String> labels) {
        Set<Pair> set = new HashSet<>();
        for (String str1:labels) {
            for (String str2:labels) {
                if (!str1.equals(str2)) {
                    set.add(new Pair(str1, str2));
                }
            }
        }
        return set;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair classPair = (Pair) o;

        if (x.equals(classPair.x) && y.equals(classPair.y)) return true;
        if (x.equals(classPair.y) && y.equals(classPair.x)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return x.hashCode() + y.hashCode();
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + "}";
    }
}
