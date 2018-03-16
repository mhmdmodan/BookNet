/**
 * A mutable int which doesn't go below zero
 */
public class Count {
    private int count;

    /**
     * Initialize with a starting count
     * @param count initial count
     */
    public Count(int count) {
        this.count = count;
    }

    public synchronized int getCount() {
        return count;
    }

    /**
     * Decrements count by one, never below 0
     */
    public void decrement() {
        if (count > 0) count--;
    }

    /**
     * Increments count by one
     */
    public synchronized void increment() {
        count++;
    }

    /**
     * Setss count
     * @param count count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return Integer.toString(count);
    }
}
