package Model.Excpetions;

public class BadPathException extends SearcherException {
    public BadPathException() {
        super("Please fix path");
    }
}
