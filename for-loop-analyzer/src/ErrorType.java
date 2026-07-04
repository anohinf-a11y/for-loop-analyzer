// Перечисление типов ошибок
public enum ErrorType {
    NO_ERROR("Нет ошибок"),
    SYNTAX_ERROR("Синтаксическая ошибка"),
    ID_TOO_LONG("Идентификатор длиннее 12 символов"),
    RESERVED_IDENTIFIER("Идентификатор является зарезервированным словом"),
    INT_OUT_OF_RANGE("Целое число вне диапазона -32768..32767"),
    VAR_MISMATCH("Переменная в cond-expr или loop-expr не совпадает с переменной цикла");

    private final String message;
    ErrorType(String message) { this.message = message; }
    public String getMessage() { return message; }
}
