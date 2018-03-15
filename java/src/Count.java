public class Count {
    private int count;

    public Count(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void decrement() {
        if (count > 0) count--;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
