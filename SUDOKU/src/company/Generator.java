package company;

public class Generator {
    private final int[][] mat; // матрица чисел
    private static final int N = 9; // Количество строк и столбцов
    private static final int SRN = (int)Math.sqrt(N); // Квадратный корень из N
    private final int k; // Количество пустых клеток

    protected int getValue(int x, int y) {
        return mat[x][y];
    }

    // Конструктор инициализации
    Generator(int k) {
        this.k = k;
        mat = new int[N][N];
        fillValues();
    }

    // Генератор судоку
    private void fillValues() {
        // Заполнение диагональных матриц
        fillDiagonal();
        // Заполнение оставшихся клеток
        fillRemaining(0, SRN);
        // Удаление k случайных клеток
        removeKDigits();
    }

    // Заполнение диагональных матриц
    private void fillDiagonal() {
        for (int i = 0; i < N; i = i + SRN)
            fillBox(i, i);
    }

    // Заполнение матрицы 3x3
    private void fillBox(int row, int col) {
        int num;
        for (int i = 0; i < SRN; i++)
            for (int j = 0; j < SRN; j++) {
                do num = randomGenerator(N);
                while (!unUsedInBox(row, col, num));
                mat[row + i][col + j] = num;
            }
    }

    // Рандом генератор
    private int randomGenerator(int num) {
        return (int) Math.floor((Math.random() * num + 1));
    }

    // Проверка, безопасно ли помещать элемент в клетку
    private boolean CheckIfSafe(int i, int j, int num) {
        return (unUsedInRow(i, num) &&
                unUsedInCol(j, num) &&
                unUsedInBox(i - i % SRN, j - j % SRN, num));
    }

    // Проверить отсутствие элемента в блоке 3x3
    private boolean unUsedInBox(int rowStart, int colStart, int num) {
        for (int i = 0; i < SRN; i++)
            for (int j = 0; j < SRN; j++)
                if (mat[rowStart + i][colStart + j] == num)
                    return false;
        return true;
    }

    // Проверить отсутствие элемента в столбце
    private boolean unUsedInRow(int i, int num) {
        for (int j = 0; j < N; j++)
            if (mat[i][j] == num)
                return false;
        return true;
    }

    // Проверить отсутствие элемента в строке
    private boolean unUsedInCol(int j, int num) {
        for (int i = 0; i < N; i++)
            if (mat[i][j] == num)
                return false;
        return true;
    }

    // Рекурсивная функция для заполнения оставшейся части матрицы
    private boolean fillRemaining(int i, int j) {
        if (j >= N && i < N - 1) {
            i = i + 1;
            j = 0;
        }
        if (i >= N && j >= N)
            return true;
        if (i < SRN) {
            if (j < SRN)
                j = SRN;
        } else if (i < N - SRN) {
            if (j == (i / SRN) * SRN)
                j = j + SRN;
        } else {
            if (j == N - SRN) {
                i = i + 1;
                j = 0;
                if (i >= N)
                    return true;
            }
        }
        for (int num = 1; num <= N; num++)
            if (CheckIfSafe(i, j, num)) {
                mat[i][j] = num;
                if (fillRemaining(i, j + 1))
                    return true;
                mat[i][j] = 0;
            }
        return false;
    }

    // Очистка всех клеток, кроме подсказок
    private void removeKDigits() {
        int count = k;
        while (count != 0) {
            int cellId = randomGenerator(N * N);
            int i = (cellId / N);
            int j = cellId % 9;
            if (i == 9) i = 0;
            if (mat[i][j] != 0) {
                count--;
                mat[i][j] = 0;
            }
        }
    }
}