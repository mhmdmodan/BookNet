public class Count {
    private int count;

    public Count(int count) {
        this.count = count;
    }

    public synchronized int getCount() {
        return count;
    }

    public void decrement() {
        if (count > 0) count--;
    }

    public synchronized void increment() {
        count++;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return Integer.toString(count);
    }
}
