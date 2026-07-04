import java.util.LinkedHashSet;
import java.util.Set;
// Статический класс для проверки
public class CheckForStatement {
    // Состояния автомата
    private enum State {
        START, F,
        S1, S2, S3, S4,
        S5, S6, S7, S8, S9, S10, S11, S12, S13, S14, S15,
        S16, S17, S18, S19, S20, S21, S22, S23, S24,
        S25, S26, S27, S28, S29, S30, S31,
        S32, S33, S34, S35, S36,
        S37, S38, S39, S40, S41, S42, S43,
        S44, S45, S46, S47, S48, S49, S50, S51, S52,
        S53, S54, S55, S56, S57, S58, S59, S60, S61, S62, S63, S64,
        S65, S66, S67, S68, S69, S70, S71,
        S72, S73, S74, S75,
        ERROR
    }

    private static final Set<String> RESERVED_WORDS = Set.of("int", "for", "return", "if", "else", "while");

    private static String input;
    private static int pos;
    private static CheckForStatement.State state;
    private static ErrorType error;
    private static int errorPos;
    private static String expected;

    private static final StringBuilder buffer = new StringBuilder();
    private static final Set<String> identifiers = new LinkedHashSet<>();
    private static final Set<String> constants = new LinkedHashSet<>();

    // Для семантической проверки
    private static String loopVariable;
    private static boolean inCondExpr;
    private static boolean inLoopExpr;

    // Главная функция проверки
    public static Result check(String inputString) {
        input = inputString;
        pos = 0;
        state = CheckForStatement.State.START;
        error = ErrorType.NO_ERROR;
        errorPos = -1;
        expected = "";
        buffer.setLength(0);
        identifiers.clear();
        constants.clear();
        loopVariable = null;
        inCondExpr = false;
        inLoopExpr = false;

        forStatement();
        return new Result(errorPos, error, identifiers, constants, loopVariable, expected);
    }

    // Реализация конечного автомата
    private static void forStatement() {
        while (state != CheckForStatement.State.ERROR && state != CheckForStatement.State.F && pos <= input.length()) {
            char ch = getChar();

            if (ch == '\0' && pos >= input.length()) {
                if (state == CheckForStatement.State.S70 || state == CheckForStatement.State.S26 || state == CheckForStatement.State.S43) {
                    state = CheckForStatement.State.F;
                } else if (state != CheckForStatement.State.F && error == ErrorType.NO_ERROR) {
                    setError(ErrorType.SYNTAX_ERROR, pos - 1, getExpected(state));
                    state = CheckForStatement.State.ERROR;
                }
                break;
            }

            processState(ch);
            pos++;
        }
    }

    private static void processState(char ch) {
        switch (state) {
            case START -> {
                if (ch == 'f') state = CheckForStatement.State.S1;
                else setError(ErrorType.SYNTAX_ERROR, pos, "'f'");
            }
            case S1 -> {
                if (ch == 'o') state = CheckForStatement.State.S2;
                else setError(ErrorType.SYNTAX_ERROR, pos, "'o'");
            }
            case S2 -> {
                if (ch == 'r') state = CheckForStatement.State.S3;
                else setError(ErrorType.SYNTAX_ERROR, pos, "'r'");
            }
            case S3 -> {
                if (ch == ' ') state = CheckForStatement.State.S3;
                else if (ch == '(') state = CheckForStatement.State.S4;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или '('");
            }
            case S4 -> {
                if (ch == ' ') state = CheckForStatement.State.S4;
                else if (ch == 'i') state = CheckForStatement.State.S5;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или 'i'");
            }
            case S5 -> {
                if (ch == 'n') state = CheckForStatement.State.S6;
                else setError(ErrorType.SYNTAX_ERROR, pos, "'n'");
            }
            case S6 -> {
                if (ch == 't') state = CheckForStatement.State.S7;
                else setError(ErrorType.SYNTAX_ERROR, pos, "'t'");
            }
            case S7 -> {
                if (ch == ' ') state = CheckForStatement.State.S75;
                else setError(ErrorType.SYNTAX_ERROR, pos, "'пробел'");
            }
            case S75 -> {
                if (ch == ' ') state = CheckForStatement.State.S75;
                else if (isLetter(ch) || ch == '_') { startId(ch); state = CheckForStatement.State.S8; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или буква/'_'");
            }
            case S8 -> {
                if (isLetterOrDigit(ch) || ch == '_') append(ch);
                else if (ch == ' ') { flushId(); state = CheckForStatement.State.S9; }
                else if (ch == '=') { flushId(); state = CheckForStatement.State.S10; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "буква/цифра/'_', пробел или '='");
            }
            case S9 -> {
                if (ch == ' ') state = CheckForStatement.State.S9;
                else if (ch == '=') state = CheckForStatement.State.S10;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или '='");
            }
            case S10 -> {
                if (ch == ' ') state = CheckForStatement.State.S10;
                else if (ch >= '1' && ch <= '9') { startNum(ch); state = CheckForStatement.State.S11; }
                else if (ch == '0') { startNum(ch); state = CheckForStatement.State.S12; }
                else if (ch == '-') { startNum(ch); state = CheckForStatement.State.S13; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, цифра или '-'");
            }
            case S11 -> {
                if (Character.isDigit(ch)) append(ch);
                else if (ch == ' ') { flushNum(); state = CheckForStatement.State.S14; }
                else if (ch == ';') { flushNum(); state = CheckForStatement.State.S15; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра, пробел или ';'");
            }
            case S12 -> {
                if (ch == ' ') { flushNum(); state = CheckForStatement.State.S14; }
                else if (ch == ';') { flushNum(); state = CheckForStatement.State.S15; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или ';'");
            }
            case S13 -> {
                if (ch >= '1' && ch <= '9') { append(ch); state = CheckForStatement.State.S11; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра 1-9");
            }
            case S14 -> {
                if (ch == ' ') state = CheckForStatement.State.S14;
                else if (ch == ';') state = CheckForStatement.State.S15;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или ';'");
            }
            case S15 -> {
                if (ch == ' ') state = CheckForStatement.State.S15;
                else if (isLetter(ch) || ch == '_') {
                    inCondExpr = true;  // ВХОДИМ В cond-expr
                    startId(ch);
                    state = CheckForStatement.State.S16;
                }
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или буква/'_'");
            }
            case S16 -> {
                if (isLetterOrDigit(ch) || ch == '_') append(ch);
                else if (ch == ' ') { flushId(); state = CheckForStatement.State.S17; }
                else if (ch == '=' || ch == '!') { flushId(); state = CheckForStatement.State.S18; }
                else if (ch == '<' || ch == '>') { flushId(); state = CheckForStatement.State.S72; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "буква/цифра/'_', пробел, '=', '!', '<' или '>'");
            }
            case S17 -> {
                if (ch == ' ') state = CheckForStatement.State.S17;
                else if (ch == '=' || ch == '!') state = CheckForStatement.State.S18;
                else if (ch == '<' || ch == '>') state = CheckForStatement.State.S72;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, '=', '!', '<' или '>'");
            }
            case S18 -> {
                if (ch == '=') {
                    inCondExpr = false;  // ← ВЫХОДИМ ИЗ cond-expr
                    state = CheckForStatement.State.S19;
                }
                else setError(ErrorType.SYNTAX_ERROR, pos, "'='");
            }
            case S19 -> {
                if (ch == ' ') state = CheckForStatement.State.S19;
                else if (isLetter(ch) || ch == '_') { startId(ch); state = CheckForStatement.State.S20; }
                else if (ch >= '1' && ch <= '9') { startNum(ch); state = CheckForStatement.State.S21; }
                else if (ch == '0') { startNum(ch); state = CheckForStatement.State.S22; }
                else if (ch == '-') { startNum(ch); state = CheckForStatement.State.S23; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, буква/'_', цифра или '-'");
            }
            case S72 -> {
                if (ch == '=') {
                    inCondExpr = false;
                    state = CheckForStatement.State.S19;
                }
                else if (ch == ' ') {
                    inCondExpr = false;
                    state = CheckForStatement.State.S73;
                }
                else if (isLetter(ch) || ch == '_') {
                    inCondExpr = false;
                    startId(ch);
                    state = CheckForStatement.State.S20;
                }
                else if (ch >= '1' && ch <= '9') {
                    inCondExpr = false;
                    startNum(ch);
                    state = CheckForStatement.State.S21;
                }
                else if (ch == '0') {
                    inCondExpr = false;
                    startNum(ch);
                    state = CheckForStatement.State.S22;
                }
                else if (ch == '-') {
                    inCondExpr = false;
                    startNum(ch);
                    state = CheckForStatement.State.S23;
                }
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, '=', буква/'_', цифра или '-'");
            }
            case S73 -> {
                inCondExpr = false;
                if (ch == ' ') state = CheckForStatement.State.S73;
                else if (isLetter(ch) || ch == '_') { startId(ch); state = CheckForStatement.State.S20; }
                else if (ch >= '1' && ch <= '9') { startNum(ch); state = CheckForStatement.State.S21; }
                else if (ch == '0') { startNum(ch); state = CheckForStatement.State.S22; }
                else if (ch == '-') { startNum(ch); state = CheckForStatement.State.S23; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, буква/'_', цифра или '-'");
            }
            case S20 -> {
                if (isLetterOrDigit(ch) || ch == '_') append(ch);
                else if (ch == ' ') { flushId(); state = CheckForStatement.State.S74; }
                else if (ch == '[') { flushId(); state = CheckForStatement.State.S25; }
                else if (ch == ';') { flushId(); state = CheckForStatement.State.S26; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "буква/цифра/'_', пробел, '[' или ';'");
            }
            case S74 -> {
                if (ch == ' ') state = CheckForStatement.State.S74;
                else if (ch == '[') state = CheckForStatement.State.S25;
                else if (ch == ';') state = CheckForStatement.State.S26;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, '[' или ';'");
            }
            case S25 -> {
                if (isLetter(ch) || ch == '_') { startId(ch); state = CheckForStatement.State.S27; }
                else if (ch >= '1' && ch <= '9') { startNum(ch); state = CheckForStatement.State.S28; }
                else if (ch == '0') { startNum(ch); state = CheckForStatement.State.S29; }
                else if (ch == '-') { startNum(ch); state = CheckForStatement.State.S30; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "буква/'_', цифра или '-'");
            }
            case S27 -> {
                if (isLetterOrDigit(ch) || ch == '_') append(ch);
                else if (ch == ']') { flushId(); state = CheckForStatement.State.S31; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "буква/цифра/'_' или ']'");
            }
            case S28 -> {
                if (Character.isDigit(ch)) append(ch);
                else if (ch == ']') { flushNum(); state = CheckForStatement.State.S31; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра или ']'");
            }
            case S29 -> {
                if (ch == ']') { flushNum(); state = CheckForStatement.State.S31; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "']'");
            }
            case S30 -> {
                if (ch >= '1' && ch <= '9') { append(ch); state = CheckForStatement.State.S28; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра 1-9");
            }
            case S31 -> {
                if (ch == ' ') state = CheckForStatement.State.S31;
                else if (ch == ';') state = CheckForStatement.State.S26;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или ';'");
            }
            case S26 -> {
                if (ch == ' ') state = CheckForStatement.State.S26;
                else if (isLetter(ch) || ch == '_') {
                    inLoopExpr = true;
                    startId(ch);
                    state = CheckForStatement.State.S37;
                }
                else if (ch == '\0') state = CheckForStatement.State.F;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, буква/'_' или конец");
            }
            case S21 -> {
                if (Character.isDigit(ch)) append(ch);
                else if (ch == '.') { append(ch); state = CheckForStatement.State.S32; }
                else if (ch == ' ') { flushNum(); state = CheckForStatement.State.S24; }
                else if (ch == ';') { flushNum(); state = CheckForStatement.State.S26; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра, '.', пробел или ';'");
            }
            case S22 -> {
                if (ch == '.') { append(ch); state = CheckForStatement.State.S32; }
                else if (ch == ' ') { flushNum(); state = CheckForStatement.State.S24; }
                else if (ch == ';') { flushNum(); state = CheckForStatement.State.S26; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "'.', пробел или ';'");
            }
            case S23 -> {
                if (ch >= '1' && ch <= '9') { append(ch); state = CheckForStatement.State.S21; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра 1-9");
            }
            case S24 -> {
                if (ch == ' ') state = CheckForStatement.State.S24;
                else if (ch == ';') state = CheckForStatement.State.S26;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или ';'");
            }
            case S32 -> {
                if (Character.isDigit(ch)) { append(ch); state = CheckForStatement.State.S33; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра");
            }
            case S33 -> {
                if (Character.isDigit(ch)) append(ch);
                else if (ch == ' ') { flushNum(); state = CheckForStatement.State.S24; }
                else if (ch == ';') { flushNum(); state = CheckForStatement.State.S26; }
                else if (ch == 'E') { append(ch); state = CheckForStatement.State.S34; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра, пробел, ';' или 'E'");
            }
            case S34 -> {
                if (ch >= '1' && ch <= '9') { append(ch); state = CheckForStatement.State.S35; }
                else if (ch == '-') { append(ch); state = CheckForStatement.State.S36; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра 1-9 или '-'");
            }
            case S35 -> {
                if (Character.isDigit(ch)) append(ch);
                else if (ch == ' ') { flushNum(); state = CheckForStatement.State.S24; }
                else if (ch == ';') { flushNum(); state = CheckForStatement.State.S26; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра, пробел или ';'");
            }
            case S36 -> {
                if (ch >= '1' && ch <= '9') { append(ch); state = CheckForStatement.State.S35; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра 1-9");
            }
            case S37 -> {
                if (isLetterOrDigit(ch) || ch == '_') append(ch);
                else if (ch == ' ') { flushId(); state = CheckForStatement.State.S38; }
                else if (ch == '+') { flushId(); state = CheckForStatement.State.S39; }
                else if (ch == '-') { flushId(); state = CheckForStatement.State.S40; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "буква/цифра/'_', пробел, '+' или '-'");
            }
            case S38 -> {
                if (ch == ' ') state = CheckForStatement.State.S38;
                else if (ch == '+') state = CheckForStatement.State.S39;
                else if (ch == '-') state = CheckForStatement.State.S40;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, '+' или '-'");
            }
            case S39 -> {
                if (ch == '+') state = CheckForStatement.State.S41;
                else setError(ErrorType.SYNTAX_ERROR, pos, "'+'");
            }
            case S40 -> {
                if (ch == '-') state = CheckForStatement.State.S42;
                else setError(ErrorType.SYNTAX_ERROR, pos, "'-'");
            }
            case S41 -> {
                inLoopExpr = false;  // ВЫХОДИМ ИЗ loop-expr
                if (ch == ' ') state = CheckForStatement.State.S41;
                else if (ch == ')') state = CheckForStatement.State.S43;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или ')'");
            }
            case S42 -> {
                inLoopExpr = false;  // ВЫХОДИМ ИЗ loop-expr
                if (ch == ' ') state = CheckForStatement.State.S42;
                else if (ch == ')') state = CheckForStatement.State.S43;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или ')'");
            }
            case S43 -> {
                if (ch == ' ') state = CheckForStatement.State.S43;
                else if (isLetter(ch) || ch == '_') { startId(ch); state = CheckForStatement.State.S44; }
                else if (ch == '\0') state = CheckForStatement.State.F;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, буква/'_' или конец");
            }
            case S44 -> {
                if (isLetterOrDigit(ch) || ch == '_') append(ch);
                else if (ch == ' ') { flushId(); state = CheckForStatement.State.S45; }
                else if (ch == '[') { flushId(); state = CheckForStatement.State.S46; }
                else if (ch == '=') { flushId(); state = CheckForStatement.State.S47; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "буква/цифра/'_', пробел, '[' или '='");
            }
            case S45 -> {
                if (ch == ' ') state = CheckForStatement.State.S45;
                else if (ch == '[') state = CheckForStatement.State.S46;
                else if (ch == '=') state = CheckForStatement.State.S47;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, '[' или '='");
            }
            case S46 -> {
                if (isLetter(ch) || ch == '_') { startId(ch); state = CheckForStatement.State.S48; }
                else if (ch >= '1' && ch <= '9') { startNum(ch); state = CheckForStatement.State.S49; }
                else if (ch == '0') { startNum(ch); state = CheckForStatement.State.S50; }
                else if (ch == '-') { startNum(ch); state = CheckForStatement.State.S51; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "буква/'_', цифра или '-'");
            }
            case S48 -> {
                if (isLetterOrDigit(ch) || ch == '_') append(ch);
                else if (ch == ']') { flushId(); state = CheckForStatement.State.S52; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "буква/цифра/'_' или ']'");
            }
            case S49 -> {
                if (Character.isDigit(ch)) append(ch);
                else if (ch == ']') { flushNum(); state = CheckForStatement.State.S52; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра или ']'");
            }
            case S50 -> {
                if (ch == ']') { flushNum(); state = CheckForStatement.State.S52; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "']'");
            }
            case S51 -> {
                if (ch >= '1' && ch <= '9') { append(ch); state = CheckForStatement.State.S49; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра 1-9");
            }
            case S52 -> {
                if (ch == ' ') state = CheckForStatement.State.S52;
                else if (ch == '=') state = CheckForStatement.State.S47;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или '='");
            }
            case S47 -> {
                if (ch == ' ') state = CheckForStatement.State.S47;
                else if (isLetter(ch) || ch == '_') { startId(ch); state = CheckForStatement.State.S53; }
                else if (ch >= '1' && ch <= '9') { startNum(ch); state = CheckForStatement.State.S54; }
                else if (ch == '0') { startNum(ch); state = CheckForStatement.State.S55; }
                else if (ch == '-') { startNum(ch); state = CheckForStatement.State.S56; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, буква/'_', цифра или '-'");
            }
            case S53 -> {
                if (isLetterOrDigit(ch) || ch == '_') append(ch);
                else if (ch == ' ') { flushId(); state = CheckForStatement.State.S71; }
                else if (ch == '[') { flushId(); state = CheckForStatement.State.S58; }
                else if (ch == ';') { flushId(); state = CheckForStatement.State.S70; }
                else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%') {
                    flushId(); state = CheckForStatement.State.S47;
                }
                else setError(ErrorType.SYNTAX_ERROR, pos, "буква/цифра/'_', пробел, '[', ';' или арифметический знак");
            }
            case S71 -> {
                if (ch == ' ') state = CheckForStatement.State.S71;
                else if (ch == '[') state = CheckForStatement.State.S58;
                else if (ch == ';') state = CheckForStatement.State.S70;
                else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%') {
                    state = CheckForStatement.State.S47;
                }
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, '[', ';' или арифметический знак");
            }
            case S58 -> {
                if (isLetter(ch) || ch == '_') { startId(ch); state = CheckForStatement.State.S60; }
                else if (ch >= '1' && ch <= '9') { startNum(ch); state = CheckForStatement.State.S61; }
                else if (ch == '0') { startNum(ch); state = CheckForStatement.State.S62; }
                else if (ch == '-') { startNum(ch); state = CheckForStatement.State.S63; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "буква/'_', цифра или '-'");
            }
            case S60 -> {
                if (isLetterOrDigit(ch) || ch == '_') append(ch);
                else if (ch == ']') { flushId(); state = CheckForStatement.State.S64; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "буква/цифра/'_' или ']'");
            }
            case S61 -> {
                if (Character.isDigit(ch)) append(ch);
                else if (ch == ']') { flushNum(); state = CheckForStatement.State.S64; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра или ']'");
            }
            case S62 -> {
                if (ch == ']') { flushNum(); state = CheckForStatement.State.S64; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "']'");
            }
            case S63 -> {
                if (ch >= '1' && ch <= '9') { append(ch); state = CheckForStatement.State.S61; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра 1-9");
            }
            case S64 -> {
                if (ch == ' ') state = CheckForStatement.State.S64;
                else if (ch == ';') state = CheckForStatement.State.S70;
                else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%') {
                    state = CheckForStatement.State.S47;
                }
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, ';' или арифметический знак");
            }
            case S54 -> {
                if (Character.isDigit(ch)) append(ch);
                else if (ch == '.') { append(ch); state = CheckForStatement.State.S65; }
                else if (ch == ' ') { flushNum(); state = CheckForStatement.State.S57; }
                else if (ch == ';') { flushNum(); state = CheckForStatement.State.S70; }
                else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%') {
                    flushNum(); state = CheckForStatement.State.S47;
                }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра, '.', пробел, ';' или арифметический знак");
            }
            case S55 -> {
                if (ch == '.') { append(ch); state = CheckForStatement.State.S65; }
                else if (ch == ' ') { flushNum(); state = CheckForStatement.State.S57; }
                else if (ch == ';') { flushNum(); state = CheckForStatement.State.S70; }
                else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%') {
                    flushNum(); state = CheckForStatement.State.S47;
                }
                else setError(ErrorType.SYNTAX_ERROR, pos, "'.', пробел, ';' или арифметический знак");
            }
            case S56 -> {
                if (ch >= '1' && ch <= '9') { append(ch); state = CheckForStatement.State.S54; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра 1-9");
            }
            case S57 -> {
                if (ch == ' ') state = CheckForStatement.State.S57;
                else if (ch == ';') state = CheckForStatement.State.S70;
                else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%') {
                    state = CheckForStatement.State.S47;
                }
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел, ';' или арифметический знак");
            }
            case S65 -> {
                if (Character.isDigit(ch)) { append(ch); state = CheckForStatement.State.S66; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра");
            }
            case S66 -> {
                if (Character.isDigit(ch)) append(ch);
                else if (ch == ' ') { flushNum(); state = CheckForStatement.State.S57; }
                else if (ch == ';') { flushNum(); state = CheckForStatement.State.S70; }
                else if (ch == 'E') { append(ch); state = CheckForStatement.State.S67; }
                else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%') {
                    flushNum(); state = CheckForStatement.State.S47;
                }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра, пробел, ';', 'E' или арифметический знак");
            }
            case S67 -> {
                if (ch >= '1' && ch <= '9') { append(ch); state = CheckForStatement.State.S68; }
                else if (ch == '-') { append(ch); state = CheckForStatement.State.S69; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра 1-9 или '-'");
            }
            case S68 -> {
                if (Character.isDigit(ch)) append(ch);
                else if (ch == ' ') { flushNum(); state = CheckForStatement.State.S57; }
                else if (ch == ';') { flushNum(); state = CheckForStatement.State.S70; }
                else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%') {
                    flushNum(); state = CheckForStatement.State.S47;
                }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра, пробел, ';' или арифметический знак");
            }
            case S69 -> {
                if (ch >= '1' && ch <= '9') { append(ch); state = CheckForStatement.State.S68; }
                else setError(ErrorType.SYNTAX_ERROR, pos, "цифра 1-9");
            }
            case S70 -> {
                if (ch == ' ') state = CheckForStatement.State.S70;
                else if (ch == '\0') state = CheckForStatement.State.F;
                else setError(ErrorType.SYNTAX_ERROR, pos, "пробел или конец");
            }
            default -> setError(ErrorType.SYNTAX_ERROR, pos, "корректный символ");
        }
    }

    // Вспомогательные методы
    private static void startId(char ch) {
        buffer.setLength(0);
        buffer.append(ch);
    }

    private static void startNum(char ch) {
        buffer.setLength(0);
        buffer.append(ch);
    }

    private static void append(char ch) {
        buffer.append(ch);
    }

    private static void flushId() {
        String id = buffer.toString();

        // Проверка длины идентификатора
        if (id.length() > 12) {
            setError(ErrorType.ID_TOO_LONG, pos - id.length(), "");
            state = CheckForStatement.State.ERROR;
            return;
        }

        // Проверка зарезервированных слов
        if (RESERVED_WORDS.contains(id)) {
            setError(ErrorType.RESERVED_IDENTIFIER, pos - id.length(), "");
            state = CheckForStatement.State.ERROR;
            return;
        }

        identifiers.add(id);

        // Семантическая проверка переменной цикла
        if (loopVariable == null && state == CheckForStatement.State.S8) {
            loopVariable = id;
        }

        // Проверка совпадения переменной В cond-expr
        if (inCondExpr && loopVariable != null && !id.equals(loopVariable)) {
            setError(ErrorType.VAR_MISMATCH, pos - id.length(), "");
            state = CheckForStatement.State.ERROR;
            return;
        }

        // Проверка совпадения переменной В loop-expr
        if (inLoopExpr && loopVariable != null && !id.equals(loopVariable)) {
            setError(ErrorType.VAR_MISMATCH, pos - id.length(), "");
            state = CheckForStatement.State.ERROR;
            return;
        }

        buffer.setLength(0);
    }

    private static void flushNum() {
        String num = buffer.toString();

        // Проверка целых чисел на диапазон
        if (!num.contains(".") && !num.contains("E")) {
            try {
                int val = Integer.parseInt(num);
                if (val < -32768 || val > 32767) {
                    setError(ErrorType.INT_OUT_OF_RANGE, pos - num.length(), "");
                    state = CheckForStatement.State.ERROR;
                    return;
                }
            } catch (NumberFormatException e) {
                // Не целое число, пропускаем
            }
        }

        constants.add(num);
        buffer.setLength(0);
    }

    private static char getChar() {
        if (pos >= input.length()) return '\0';
        return input.charAt(pos);
    }

    private static boolean isLetter(char ch) {
        return Character.isLetter(ch);
    }

    private static boolean isLetterOrDigit(char ch) {
        return Character.isLetterOrDigit(ch);
    }

    private static void setError(ErrorType errType, int position, String expectedStr) {
        if (error == ErrorType.NO_ERROR) {
            error = errType;
            errorPos = position;
            expected = expectedStr;
        }
        state = CheckForStatement.State.ERROR;
    }

    private static String getExpected(CheckForStatement.State s) {
        return switch (s) {
            case START -> "'f'";
            case S1 -> "'o'";
            case S2 -> "'r'";
            case S3 -> "пробел или '('";
            case S4 -> "пробел или 'i'";
            case S5 -> "'n'";
            case S6 -> "'t'";
            case S7 -> "пробел";
            case S75 -> "пробел или буква/'_'";
            case S8 -> "буква/цифра/'_', пробел или '='";
            case S9 -> "пробел или '='";
            case S10 -> "пробел, цифра или '-'";
            case S11 -> "цифра, пробел или ';'";
            case S12 -> "пробел или ';'";
            case S13 -> "цифра 1-9";
            case S14 -> "пробел или ';'";
            case S15 -> "пробел или буква/'_'";
            case S16 -> "буква/цифра/'_', пробел, '=', '!', '<' или '>'";
            case S17 -> "пробел, '=', '!', '<' или '>'";
            case S18 -> "'='";
            case S19 -> "пробел, буква/'_', цифра или '-'";
            case S72, S73 -> "пробел, '=', буква/'_', цифра или '-'";
            case S20 -> "буква/цифра/'_', пробел, '[' или ';'";
            case S74 -> "пробел, '[' или ';'";
            case S25 -> "буква/'_', цифра или '-'";
            case S27 -> "буква/цифра/'_' или ']'";
            case S28 -> "цифра или ']'";
            case S29 -> "']'";
            case S30 -> "цифра 1-9";
            case S31 -> "пробел или ';'";
            case S26 -> "пробел, буква/'_' или конец";
            case S21, S22 -> "цифра, '.', пробел или ';'";
            case S23 -> "цифра 1-9";
            case S24 -> "пробел или ';'";
            case S32 -> "цифра";
            case S33 -> "цифра, пробел, ';' или 'E'";
            case S34 -> "цифра 1-9 или '-'";
            case S35 -> "цифра, пробел или ';'";
            case S36 -> "цифра 1-9";
            case S37 -> "буква/цифра/'_', пробел, '+' или '-'";
            case S38 -> "пробел, '+' или '-'";
            case S39 -> "'+'";
            case S40 -> "'-'";
            case S41, S42 -> "пробел или ')'";
            case S43 -> "пробел, буква/'_' или конец";
            case S44 -> "буква/цифра/'_', пробел, '[' или '='";
            case S45 -> "пробел, '[' или '='";
            case S46 -> "буква/'_', цифра или '-'";
            case S48 -> "буква/цифра/'_' или ']'";
            case S49 -> "цифра или ']'";
            case S50 -> "']'";
            case S51 -> "цифра 1-9";
            case S52 -> "пробел или '='";
            case S47 -> "пробел, буква/'_', цифра или '-'";
            case S53 -> "буква/цифра/'_', пробел, '[', ';' или арифметический знак";
            case S71 -> "пробел, '[', ';' или арифметический знак";
            case S58 -> "буква/'_', цифра или '-'";
            case S60 -> "буква/цифра/'_' или ']'";
            case S61 -> "цифра или ']'";
            case S62 -> "']'";
            case S63 -> "цифра 1-9";
            case S64 -> "пробел, ';' или арифметический знак";
            case S54, S55 -> "цифра, '.', пробел, ';' или арифметический знак";
            case S56 -> "цифра 1-9";
            case S57 -> "пробел, ';' или арифметический знак";
            case S65 -> "цифра";
            case S66 -> "цифра, пробел, ';', 'E' или арифметический знак";
            case S67 -> "цифра 1-9 или '-'";
            case S68 -> "цифра, пробел, ';' или арифметический знак";
            case S69 -> "цифра 1-9";
            case S70 -> "пробел или конец";
            default -> "корректный символ";
        };
    }
}
