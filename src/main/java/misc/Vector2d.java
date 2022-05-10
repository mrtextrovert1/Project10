package misc;


import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс двумерного вектора double
 */
public class Vector2d {
    /**
     * x - координата вектора
     */
    public double x;
    /**
     * y - координата вектора
     */
    public double y;

    /**
     * Конструктор вектора
     *
     * @param x координата X вектора
     * @param y координата Y вектора
     */
    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Конструктор вектора создаёт нулевой вектор
     */
    public Vector2d() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Сложить два вектора
     *
     * @param a первый вектор
     * @param b второй вектор
     * @return сумма двух векторов
     */

    public static Vector2d sum(Vector2d a, Vector2d b) {
        return new Vector2d(a.x + b.x, a.y + b.y);
    }


    /**
     * Добавить вектор к текущему вектору
     *
     * @param v вектор, который нужно добавить
     */
    public void add(Vector2d v) {
        this.x = this.x + v.x;
        this.y = this.y + v.y;
    }

    /**
     * Вычесть второй вектор из первого
     *
     * @param a первый вектор
     * @param b второй вектор
     * @return разность двух векторов
     */
    public static Vector2d subtract(Vector2d a, Vector2d b) {
        return new Vector2d(a.x - b.x, a.y - b.y);
    }

    /**
     * Вычесть вектор из текущего
     *
     * @param v вектор, который нужно вычесть
     */
    public void subtract(Vector2d v) {
        this.x = this.x - v.x;
        this.y = this.y - v.y;
    }

    /**
     * Умножение вектора на число
     *
     * @param v вектор
     * @param a число
     * @return результат умножения вектора на число
     */

    public static Vector2d mul(Vector2d v, double a) {
        return new Vector2d(v.x * a, v.y * a);
    }

    /**
     * Получить случайное значение в заданном диапазоне [min,max)
     *
     * @param min нижняя граница
     * @param max верхняя граница
     * @return случайное значение в заданном диапазоне [min,max)
     */

    public static Vector2d rand(Vector2d min, Vector2d max) {
        return new Vector2d(
                ThreadLocalRandom.current().nextDouble(min.x, max.x),
                ThreadLocalRandom.current().nextDouble(min.y, max.y)
        );
    }

    /**
     * Получить длину вектора
     *
     * @return длина вектора
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Строковое представление объекта
     *
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return "(" + String.format("%.2f", x).replace(",", ".") +
                ", " + String.format("%.2f", y).replace(",", ".") +
                ')';
    }

    /**
     * Проверка двух объектов на равенство
     *
     * @param o объект, с которым сравниваем текущий
     * @return флаг, равны ли два объекта
     */
    @Override
    public boolean equals(Object o) {
        // если объект сравнивается сам с собой, тогда объекты равны
        if (this == o) return true;
        // если в аргументе передан null или классы не совпадают, тогда объекты не равны
        if (o == null || getClass() != o.getClass()) return false;

        // приводим переданный в параметрах объект к текущему классу
        Vector2d vector2d = (Vector2d) o;

        // если не совпадают x координаты
        if (Double.compare(vector2d.x, x) != 0) return false;
        // объекты совпадают тогда и только тогда, когда совпадают их координаты
        return Double.compare(vector2d.y, y) == 0;
    }

    /**
     * Получить хэш-код объекта
     *
     * @return хэш-код объекта
     */
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}