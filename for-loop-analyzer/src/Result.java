import java.util.LinkedHashSet;
import java.util.Set;
// Класс для передачи результата в интерфейс
public class Result {
    private final int errorPosition;
    private final ErrorType error;
    private final Set<String> identifiers;
    private final Set<String> constants;
    private final String loopVariable;
    private final String expected;

    public Result(int errorPosition, ErrorType error, Set<String> identifiers,
                  Set<String> constants, String loopVariable, String expected) {
        this.errorPosition = errorPosition;
        this.error = error;
        this.identifiers = new LinkedHashSet<>(identifiers);
        this.constants = new LinkedHashSet<>(constants);
        this.loopVariable = loopVariable;
        this.expected = expected;
    }

    public int getErrorPosition() { return errorPosition; }
    public ErrorType getError() { return error; }
    public String getErrorMessage() { return error.getMessage(); }
    public Set<String> getIdentifiers() { return identifiers; }
    public Set<String> getConstants() { return constants; }
    public String getLoopVariable() { return loopVariable; }
    public String getExpected() { return expected; }
    public boolean hasError() { return error != ErrorType.NO_ERROR; }
}
