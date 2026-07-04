import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame{
    // GUI компоненты
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JButton analyzeBtn;
    private JButton clearBtn;

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Анализатор оператора for");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Основная панель
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Верхняя панель с описанием
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        JTextArea descArea = new JTextArea(3, 80);
        descArea.setText(
                "Синтаксис: for (int id = int-numb; id rel-oper term; id ++/--) var = var/any-numb { math-oper var/any-numb };\n" +
                        "Пример: for (int ind = 0; ind == 100; ind++) mass[ind] = mass[ind] % ind;\n" +
                        "Ограничения: id <= 12 символов, не зарезервирован, int-numb в диапазоне [-32768, 32767]\n" +
                        "Переменная в cond-expr и loop-expr должна совпадать с переменной цикла");
        descArea.setEditable(false);
        descArea.setBackground(new Color(240, 240, 240));
        descArea.setFont(new Font("Dialog", Font.PLAIN, 11));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        topPanel.add(descArea, BorderLayout.CENTER);

        // Панель ввода
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Входная строка"));

        inputArea = new JTextArea(5, 80);
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        inputPanel.add(inputScroll, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));

        analyzeBtn = new JButton("Анализировать");
        clearBtn = new JButton("Очистить");

        analyzeBtn.setFont(new Font("Dialog", Font.PLAIN, 13));
        clearBtn.setFont(new Font("Dialog", Font.PLAIN, 13));

        analyzeBtn.setPreferredSize(new Dimension(150, 30));
        clearBtn.setPreferredSize(new Dimension(150, 30));

        buttonPanel.add(analyzeBtn);
        buttonPanel.add(clearBtn);

        // Панель вывода
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Результат анализа"));

        outputArea = new JTextArea(12, 80);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        outputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outputPanel.add(outputScroll, BorderLayout.CENTER);


        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(0, 10));
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(outputPanel, BorderLayout.SOUTH);

        mainPanel.setPreferredSize(new Dimension(800, 400));
        add(mainPanel);

        // Обработчики событий
        analyzeBtn.addActionListener(e -> analyze());
        clearBtn.addActionListener(e -> clear());
    }

    private void analyze() {
        String code = inputArea.getText();
        if (code.trim().isEmpty()) {
            outputArea.setText("Ошибка: Введите код для анализа");
            return;
        }

        Result result = CheckForStatement.check(code);

        StringBuilder sb = new StringBuilder();

        if (result.hasError()) {
            sb.append("========== ОШИБКА АНАЛИЗА ==========\n\n");
            sb.append("Тип ошибки: ").append(result.getErrorMessage()).append("\n");

            if (result.getErrorPosition() >= 0) {
                sb.append("Позиция ошибки: ").append(result.getErrorPosition()).append("\n");
                if (result.getExpected() != null && !result.getExpected().isEmpty()) {
                    sb.append("Ожидалось: ").append(result.getExpected()).append("\n");
                }


                int start = Math.max(0, result.getErrorPosition() - 30);
                int end = Math.min(code.length(), result.getErrorPosition() + 30);
                String context = code.substring(start, end);
                sb.append("\n").append(context).append("\n");

                int pointerPos = result.getErrorPosition() - start;
                for (int i = 0; i < pointerPos; i++) {
                    sb.append(" ");
                }
                sb.append("^\n");
            }
        } else {
            sb.append("========== АНАЛИЗ УСПЕШНО ЗАВЕРШЕН ==========\n\n");
            sb.append("Переменная цикла: ").append(result.getLoopVariable()).append("\n\n");

            sb.append("ТАБЛИЦА ИДЕНТИФИКАТОРОВ:\n");
            sb.append("----------------------------------------\n");
            if (result.getIdentifiers().isEmpty()) {
                sb.append("(нет)\n");
            } else {
                int i = 1;
                for (String id : result.getIdentifiers()) {
                    sb.append(i++).append(". ").append(id).append("\n");
                }
            }

            sb.append("\nТАБЛИЦА КОНСТАНТ:\n");
            sb.append("----------------------------------------\n");
            if (result.getConstants().isEmpty()) {
                sb.append("(нет)\n");
            } else {
                int i = 1;
                for (String c : result.getConstants()) {
                    sb.append(i++).append(". ").append(c).append("\n");
                }
            }
        }

        outputArea.setText(sb.toString());
    }

    private void clear() {
        inputArea.setText("");
        outputArea.setText("");
    }
}
