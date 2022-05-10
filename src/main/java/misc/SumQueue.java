package misc;

/**
 * Класс суммирующей очереди
 */
public class SumQueue {
    /**
     * Предел суммирования
     */
    int SUM_LIMIT = 1000;
    /**
     * Начальная длина очереди
     */
    int QUEUE_INIT_LENGTH = 180;
    /**
     * Сколько записей мы уже получили
     */
    int dataLength;
    /**
     * положение курсора
     */
    int cursor;
    /**
     * Значения
     */
    public float[] values = new float[QUEUE_INIT_LENGTH];

    /**
     * Добавить элемент
     *
     * @param a значение элемента
     */
    public void add(float a) {
        // задаём новое значение
        values[cursor] = a;
        // сдвигаем курсор по кругу
        cursor = (cursor + 1) % values.length;
        // если длина данных меньше длины массива элементов
        if (dataLength < values.length)
            // увеличиваем её на 1
            dataLength++;
    }

    /**
     * Получить среднее значение очереди
     *
     * @return среднее значение очереди
     */
    public double getMean() {
        double res = 0;
        int cnt = 0;
        // перебираем все элементы массива
        for (int i = 0; i < Math.min(dataLength, values.length); i++) {
            // но со сдвигом
            var realPos = (cursor - i + values.length) % values.length;
            // если dt на
            if (values[realPos] > 0) {
                res += values[realPos];
                cnt++;
            }
            // если результат больше предела
            if (res > SUM_LIMIT)
                // прерываем цикл
                break;
        }
        // выводим среднее
        return res / cnt;
    }

    /**
     * Получить длину очереди
     *
     * @return длина очереди
     */
    public int getLength() {
        return values.length;
    }

    /**
     * Задать длину очереди
     *
     * @param len новая длина
     */
    public void setLength(int len) {
        // курсор в начало
        cursor = 0;
        // пересоздаём массив
        values = new float[len];
        // данных нет
        dataLength = 0;
    }

    /**
     * Получить элемент очереди ппо индексу
     *
     * @param i индекс
     * @return элемент очереди
     */
    public float get(int i) {
        // получаем положение с учётом зацикливания
        var idx = (cursor + i) % values.length;
        return values[idx];
    }
}
