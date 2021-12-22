package company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

// Основной класс приложения
public class SudokuApp {
    private static final int WIDTH = 9; // Количество ячеек таблицы по оси X.
    private static final int HEIGHT = 9; // Количество ячеек таблицы по оси Y.
    private final Cell[][] mapCells; // Ячейки
    // Расположение последней задействованной ячейки
    private int lastX = 0;
    private int lastY = 0;
    private char text = '?'; // Последний введённый текст
    private int complexity = 20; // Сложность (кол-во пустых ячеек)
    Instant start;
    Instant finish;

    // Этот внутренний класс обрабатывает события мыши в основной сетке ячеек карты,
    // изменяя ячейки на основе состояния кнопки мыши и начального редактирования, которое было выполнено.
    private class CellHandler implements MouseListener {
        // Запускает операцию модификации.
        public void mousePressed(MouseEvent e) {
            Cell cell = (Cell) e.getSource();
            lastX = cell.getXCord();
            lastY = cell.getYCord();
            if (mapCells[lastX][lastY].isUnfixed())
                mapCells[lastX][lastY].toggleUnchanging();
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }
    }

    // Конструктор по умолчанию
    SudokuApp() { mapCells = new Cell[WIDTH][HEIGHT]; }

    // Запускает приложение
    public void start() { SwingUtilities.invokeLater(this::initGUI); }

    // Простой вспомогательный метод для настройки пользовательского интерфейса Swing.
    private void initGUI() {
        JFrame frame = new JFrame("Судоку");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = frame.getContentPane();
        GridBagLayout gbLayout = new GridBagLayout();
        GridBagConstraints gbConstraints = new GridBagConstraints();
        // панель клеток
        JPanel mapPanel = new JPanel(gbLayout);
        mapPanel.setBackground(Color.GRAY);
        CellHandler cellHandler = new CellHandler();
        // заполнение панели клетками
        for (int y = 0; y < HEIGHT; y++) {
            int top;
            int bottom = 1;
            if (y % 3 == 0) top = 3;
            else top = 0;
            if (y + 1 >= HEIGHT) bottom = 3;
            for (int x = 0; x < WIDTH; x++) {
                int left;
                int right = 1;
                if (x % 3 != 0) left = 0;
                else left = 3;
                if (x + 1 >= WIDTH) right = 2;
                gbConstraints.insets.set(top, left, bottom, right);
                mapCells[x][y] = new Cell(' ', x, y);
                gbConstraints.gridx = x;
                gbConstraints.gridy = y;
                gbLayout.setConstraints(mapCells[x][y], gbConstraints);
                mapPanel.add(mapCells[x][y]);
                mapCells[x][y].addMouseListener(cellHandler);
            }
        }
        // смена иконки
        ImageIcon img = new ImageIcon("logo.png");
        frame.setIconImage(img.getImage());
        // добавляем панель судоку
        contentPane.add(mapPanel, BorderLayout.CENTER);
        // добавляем кнопки
        JButton generateButton = new JButton("Generate");
        JButton checkButton = new JButton("Check");
        Choice choiceButton = new Choice();
        JPanel panel = new JPanel();
        JLabel header = new JLabel("Сложность:");
        frame.add(panel, BorderLayout.NORTH);
        panel.add(header);
        panel.add(choiceButton);
        choiceButton.add("Легко");
        choiceButton.add("Средне");
        choiceButton.add("Сложно");
        // слушаем изменения сложности
        choiceButton.addItemListener(e -> {
            if (e.getItem() == "Легко") complexity = 20;
            if (e.getItem() == "Средне") complexity = 40;
            if (e.getItem() == "Сложно") complexity = 56;
        });
        // генератор
        generate(complexity);
        // генерируем новую последовательнсть при нажатии кнопки
        generateButton.addActionListener(e -> generate(complexity));
        // проверяем корректность при нажатии кнопки
        checkButton.addActionListener(e -> {
            try {
                checkCorrect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        choiceButton.setFocusable(false);
        generateButton.setFocusable(false);
        checkButton.setFocusable(false);
        frame.setFocusable(true);
        JPanel down = new JPanel();
        contentPane.add(down, BorderLayout.SOUTH);
        down.add(generateButton);
        down.add(checkButton);
        // слушаем нажатия клавиш
        frame.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (mapCells[lastX][lastY].isUnfixed()) {
                    text = e.getKeyChar();
                    if (text < '1' || text > '9')
                        text = '?';
                    if (mapCells[lastX][lastY].isChanging()) {
                        mapCells[lastX][lastY].toggleUnchanging();
                        mapCells[lastX][lastY].setValue(text);
                    } else text = '?';
                }
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);


    }

    // Заполняет таблицу, копируя значения из генератора
    private void generate(int complexity) {
        Generator table = new Generator(complexity);
        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++)
                if (table.getValue(x, y) != 0) {
                    if (mapCells[x][y].isChanging())
                        mapCells[x][y].toggleUnchanging();
                    mapCells[x][y].setValue(table.getValue(x, y));
                    mapCells[x][y].setFixed(true);
                } else {
                    mapCells[x][y].setValue(' ');
                    mapCells[x][y].setFixed(false);
                }
        start = Instant.now();
    }

    private void recordStatistics(long elapsed, int mistakesNumber) throws IOException {
        FileWriter writer = new FileWriter("statistics.txt", true);
        writer.write("На игру затрачено " + elapsed + " сек, сделано " + mistakesNumber + " ошибок\n");
        writer.close();
    }

    // Проверка правильности заполнения таблицы
    private void checkCorrect() throws IOException {
        finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis() / 1000;
        int mistakesNumber = 0;
        boolean correct = true;
        for (int x = 0; x < WIDTH; x++) {
            int sum = 0;
            for (int y = 0; y < HEIGHT; y++) {
                mapCells[x][y].setCorrect(true);
                sum += mapCells[x][y].getValue();
            }
            if (sum != 45) {
                correct = false;
                for (int y = 0; y < HEIGHT; y++)
                    if (mapCells[x][y].isUnfixed())
                    {
                        mapCells[x][y].setCorrect(false);

                    }
            }
        }
        for (int y = 0; y < HEIGHT; y++) {
            int sum = 0;
            for (int x = 0; x < WIDTH; x++)
                sum += mapCells[x][y].getValue();
            if (sum != 45) {
                correct = false;
                for (int x = 0; x < WIDTH; x++)
                    if (mapCells[x][y].isUnfixed())
                    {
                        mapCells[x][y].setCorrect(false);

                    }
            }
        }
        for (int xx = 0; xx < HEIGHT / 3; xx++)
            for (int yy = 0; yy < WIDTH / 3; yy++) {
                int sum = 0;
                for (int x = 0; x < 3; x++)
                    for (int y = 0; y < 3; y++)
                        sum += mapCells[xx * 3 + x][yy * 3 + y].getValue();
                if (sum != 45) {
                    correct = false;
                    for (int x = 0; x < 3; x++)
                        for (int y = 0; y < 3; y++)
                            if (mapCells[xx * 3 + x][yy * 3 + y].isUnfixed())
                            {
                                mapCells[xx * 3 + x][yy * 3 + y].setCorrect(false);
                                mistakesNumber++;
                            }
                }
            }
        if (correct)
            for (int x = 0; x < WIDTH; x++)
                for (int y = 0; y < HEIGHT; y++)
                    mapCells[x][y].setFixed(true);
        recordStatistics(elapsed, mistakesNumber);
    }
}