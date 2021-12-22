package company;

import javax.swing.*;
import java.awt.*;

// Этот класс представляет собой настраиваемый компонент Swing для представления отдельной ячейки карты на 2D-карте.
public class Cell extends JLabel {
    private final int xCord; // x координата расположения ячейки
    private final int yCord; // y координата расположения ячейки
    private static final Font FONT = new Font("Comic Sans MS", Font.PLAIN, 20); // Шрифт ячейки
    private static final Dimension CELL_SIZE = new Dimension(40, 40);
    private char value; // значение клетки
    private boolean unchanging; // изменяется ли клетка в данный момент
    private boolean fixed; // можно ли изменять данную клетку пользователем
    private boolean correct; // корректно ли значение клетки

    // Конструктор инициализации
    public Cell(char val, int x, int y) {
        setPreferredSize(CELL_SIZE);
        value = val;
        xCord = x;
        yCord = y;
        value = '?';
        unchanging = true;
        fixed = false;
        correct = true;
    }

    // Методы получения координат клетки
    protected int getXCord(){
        return xCord;
    }
    protected int getYCord(){
        return yCord;
    }

    // Метод получения значения клетки
    protected int getValue() {
        return (int) value - '0';
    }

    // Изменяется ли клетка
    protected boolean isChanging() {
        return !unchanging;
    }

    // Можно ли изменять клетку
    protected boolean isUnfixed() { return !fixed; }

    protected void toggleUnchanging() {
        setUnchanging(isChanging());
    }

    // сеттеры
    protected void setUnchanging(boolean unchanging) {
        this.unchanging = unchanging;
        updateAppearance();
    }

    protected void setFixed(boolean fixed) {
        this.fixed = fixed;
        updateAppearance();
    }

    protected void setCorrect(boolean correct) {
        this.correct = correct;
        updateAppearance();
    }

    protected void setValue(char value) {
        this.value = value;
        correct = true;
        updateAppearance();
    }

    protected void setValue(int value) {
        int value1 = value + (int) '0';
        this.value = (char) value1;
        updateAppearance();
    }

    // Обновление клетки
    private void updateAppearance() {
        if (!correct && unchanging) setBackground(Color.RED);
        else if (unchanging) setBackground(Color.WHITE);
        else {
            setBackground(Color.GREEN);
            correct = true;
            value = '?';
        }
        if (fixed) setBackground(Color.lightGray);
    }

    // Метод отрисовки
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.setFont(FONT);
        g.drawString(String.valueOf(value), (getWidth() - 10) / 2, (getHeight() + 10) / 2);
    }
}