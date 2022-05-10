package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import misc.Misc;
import misc.Vector2d;

import java.util.Objects;

/**
 * Класс точки
 */
public class Point {
    /**
     * Множества
     */
    public enum PointSet {
        /**
         * Первое
         */
        FIRST_SET,
        /**
         * Второе
         */
        SECOND_SET
    }

    /**
     * Множество, которому принадлежит точка
     */
    protected final PointSet pointSet;
    /**
     * Координаты точки
     */
    public final Vector2d pos;

    /**
     * Конструктор точки
     *
     * @param pos     положение точки
     * @param setType множество, которому она принадлежит
     */
    @JsonCreator
    public Point(@JsonProperty("pos") Vector2d pos, @JsonProperty("setType") PointSet setType) {
        this.pos = pos;
        this.pointSet = setType;
    }


    /**
     * Получить цвет точки по её множеству
     *
     * @return цвет точки
     */
    @JsonIgnore
    public int getColor() {
        return switch (pointSet) {
            case FIRST_SET -> Misc.getColor(0xCC, 0x00, 0x00, 0xFF);
            case SECOND_SET -> Misc.getColor(0xCC, 0x00, 0xFF, 0x0);
        };
    }

    /**
     * Получить положение
     * (нужен для json)
     *
     * @return положение
     */
    public Vector2d getPos() {
        return pos;
    }

    /**
     * Получить множество
     *
     * @return множество
     */
    public PointSet getSetType() {
        return pointSet;
    }


    /**
     * Получить название множества
     *
     * @return название множества
     */
    @JsonIgnore
    public String getSetName() {
        return switch (pointSet) {
            case FIRST_SET -> "Первое множество";
            case SECOND_SET -> "Второе множество";
        };
    }

    /**
     * Строковое представление объекта
     *
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return "Point{" +
                "pointSetType=" + pointSet +
                ", pos=" + pos +
                '}';
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
        Point point = (Point) o;
        return pointSet.equals(point.pointSet) && Objects.equals(pos, point.pos);
    }

    /**
     * Получить хэш-код объекта
     *
     * @return хэш-код объекта
     */
    @Override
    public int hashCode() {
        return Objects.hash(pointSet, pos);
    }
}
