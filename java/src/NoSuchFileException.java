public class NoSuchFileException extends RuntimeException {
    public NoSuchFileException() {
        super("No such file/directory exists");
    }
}
