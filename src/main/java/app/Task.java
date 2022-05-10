package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.humbleui.jwm.MouseButton;
import io.github.humbleui.skija.*;
import misc.CoordinateSystem2d;
import misc.CoordinateSystem2i;
import misc.Vector2d;
import misc.Vector2i;
import panels.PanelLog;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static app.Colors.*;

/**
 * Класс задачи
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class Task {
    /**
     * Текст задачи
     */
    public static final String TASK_TEXT = """
            ПОСТАНОВКА ЗАДАЧИ:
            Заданы множества точек и треугольник. Требуется найти
            2 точки, задающие прямую, не пересекающую треугольник
            и имеющую наиболее близкое к нему расстояние""";


    /**
     *  коэффициент колёсика мыши
     */
    private static final float WHEEL_SENSITIVE = 0.001f;

    /**
     * Вещественная система координат задачи
     */
    private final CoordinateSystem2d ownCS;
    /**
     * Список точек
     */
    private final ArrayList<Point> points;
    /**
     * Размер точки
     */
    private static final int POINT_SIZE = 3;
    /**
     * Последняя СК окна
     */
    private CoordinateSystem2i lastWindowCS;
    /**
     * Флаг, решена ли задача
     */
    private boolean solved;
    /**
     * Точки для ответа
     */
    private final ArrayList<Point> answer;
    /**
     * Список точек в треугольнике
     */
    private final ArrayList<Point> triangle;
    /**
     * Порядок разделителя сетки, т.е. раз в сколько отсечек
     * будет нарисована увеличенная
     */
    private static final int DELIMITER_ORDER = 10;

    /**
     * Задача
     *
     * @param ownCS  СК задачи
     * @param points массив точек
     */
    @JsonCreator
    public Task(@JsonProperty("ownCS") CoordinateSystem2d ownCS, @JsonProperty("points") ArrayList<Point> points) {
        this.ownCS = ownCS;
        this.points = points;
        this.answer = new ArrayList<>();
        this.triangle = new ArrayList<>();
    }

    /**
     * Рисование
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        // Сохраняем последнюю СК
        lastWindowCS = windowCS;
        // рисуем координатную сетку
        renderGrid(canvas, lastWindowCS);
        // рисуем задачу
        renderTask(canvas, windowCS);
    }

    /**
     * Рисование задачи
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    private void renderTask(Canvas canvas, CoordinateSystem2i windowCS) {
        canvas.save();
        // создаём перо
        try (var paint = new Paint()) {
            printPoints(canvas, windowCS, paint, points);
            printPoints(canvas, windowCS, paint, triangle);
            if (triangle.size() >= 2) {
                paint.setColor(triangle.get(0).getColor());
                for (int i = 0; i < triangle.size(); i++) {
                    for (int j = i + 1; j < triangle.size(); j++) {
                        printLine(canvas, windowCS, paint, triangle.get(i).pos, triangle.get(j).pos);
                    }
                }
            }
            if (solved && answer.size() != 0) {
                paint.setColor(ANSWER_COLOR);
                for (Point p: answer) {
                    Vector2i windowPos = windowCS.getCoords(p.pos.x, p.pos.y, ownCS);
                    // рисуем точку
                    canvas.drawRect(Rect.makeXYWH(windowPos.x - POINT_SIZE, windowPos.y - POINT_SIZE, POINT_SIZE * 2, POINT_SIZE * 2), paint);
                }
                if (answer.get(0).getPos().x == answer.get(1).getPos().x) {
                    printLine(canvas, windowCS, paint, new Vector2d(answer.get(0).getPos().x, answer.get(2).getPos().y), answer.get(2).getPos());
                } else {
                    double k = (answer.get(1).getPos().y - answer.get(0).getPos().y) / (answer.get(1).getPos().x - answer.get(0).getPos().x);
                    double p = answer.get(1).getPos().y - k * answer.get(1).getPos().x;
                    double kP = -1 / k;
                    double pP = answer.get(2).getPos().y - kP * answer.get(2).getPos().x;
                    double xCross = (pP - p) / (k - kP);
                    double yCross = k * xCross + p;
                    printLine(canvas, windowCS, paint, new Vector2d(xCross, yCross), answer.get(2).getPos());
                    if (k >= 1) {
                        double yMin = ownCS.getMin().y;
                        double yMax = ownCS.getMax().y;
                        printLine(canvas, windowCS, paint, new Vector2d((yMin - p) / k, yMin),
                                new Vector2d((yMax - p) / k, yMax));
                    } else {
                        double xMin = ownCS.getMin().x;
                        double xMax = ownCS.getMax().x;
                        printLine(canvas, windowCS, paint, new Vector2d(xMin, xMin * k + p),
                                new Vector2d(xMax, xMax * k + p));
                    }
                }
            }
        }
        canvas.restore();
    }

    private void printLine(Canvas canvas, CoordinateSystem2i windowCS, Paint paint, Vector2d p1, Vector2d p2) {
        Vector2i windowPos1 = windowCS.getCoords(p1.x, p1.y, ownCS);
        Vector2i windowPos2 = windowCS.getCoords(p2.x, p2.y, ownCS);
        canvas.drawLine(windowPos1.x, windowPos1.y, windowPos2.x, windowPos2.y, paint);
    }

    private void printPoints(Canvas canvas, CoordinateSystem2i windowCS, Paint paint, ArrayList<Point> pointsArr) {
        for (Point p : pointsArr) {
            paint.setColor(p.getColor());
            // y-координату разворачиваем, потому что у СК окна ось y направлена вниз,
            // а в классическом представлении - вверх
            Vector2i windowPos = windowCS.getCoords(p.pos.x, p.pos.y, ownCS);
            // рисуем точку
            canvas.drawRect(Rect.makeXYWH(windowPos.x - POINT_SIZE, windowPos.y - POINT_SIZE, POINT_SIZE * 2, POINT_SIZE * 2), paint);
        }
    }

    /**
     * Добавить точку
     *
     * @param pos      положение
     * @param pointSet множество
     */
    public void addPoint(Vector2d pos, Point.PointSet pointSet) {
        solved = false;
        Point newPoint = new Point(pos, pointSet);
        if (pointSet.equals(Point.PointSet.FIRST_SET)) {
            if (triangle.size() < 3) {
                triangle.add(newPoint);
            }
        } else {
            points.add(newPoint);
        }
        PanelLog.info("точка " + newPoint + " добавлена в " + newPoint.getSetName());
    }


    /**
     * Клик мыши по пространству задачи
     *
     * @param pos         положение мыши
     * @param mouseButton кнопка мыши
     */
    public void click(Vector2i pos, MouseButton mouseButton) {
        if (lastWindowCS == null) return;
        // получаем положение на экране
        Vector2d taskPos = ownCS.getCoords(pos, lastWindowCS);
        // если левая кнопка мыши, добавляем в первое множество
        if (mouseButton.equals(MouseButton.PRIMARY)) {
            addPoint(taskPos, Point.PointSet.FIRST_SET);
            // если правая, то во второе
        } else if (mouseButton.equals(MouseButton.SECONDARY)) {
            addPoint(taskPos, Point.PointSet.SECOND_SET);
        }
    }


    /**
     * Добавить случайные точки
     *
     * @param cnt кол-во случайных точек
     */
    public void addRandomPoints(int cnt) {
        CoordinateSystem2i addGrid = new CoordinateSystem2i(30, 30);

        for (int i = 0; i < cnt; i++) {
            Vector2i gridPos = addGrid.getRandomCoords();
            Vector2d pos = ownCS.getCoords(gridPos, addGrid);
            // сработает примерно в половине случаев
            if (ThreadLocalRandom.current().nextBoolean())
                addPoint(pos, Point.PointSet.FIRST_SET);
            else
                addPoint(pos, Point.PointSet.SECOND_SET);
        }
    }


    /**
     * Рисование сетки
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void renderGrid(Canvas canvas, CoordinateSystem2i windowCS) {
        // сохраняем область рисования
        canvas.save();
        // получаем ширину штриха(т.е. по факту толщину линии)
        float strokeWidth = 0.03f / (float) ownCS.getSimilarity(windowCS).y + 0.5f;
        // создаём перо соответствующей толщины
        try (var paint = new Paint().setMode(PaintMode.STROKE).setStrokeWidth(strokeWidth).setColor(TASK_GRID_COLOR)) {
            // перебираем все целочисленные отсчёты нашей СК по оси X
            for (int i = (int) (ownCS.getMin().x); i <= (int) (ownCS.getMax().x); i++) {
                // находим положение этих штрихов на экране
                Vector2i windowPos = windowCS.getCoords(i, 0, ownCS);
                // каждый 10 штрих увеличенного размера
                float strokeHeight = i % DELIMITER_ORDER == 0 ? 5 : 2;
                // рисуем вертикальный штрих
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x, windowPos.y + strokeHeight, paint);
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x, windowPos.y - strokeHeight, paint);
            }
            // перебираем все целочисленные отсчёты нашей СК по оси Y
            for (int i = (int) (ownCS.getMin().y); i <= (int) (ownCS.getMax().y); i++) {
                // находим положение этих штрихов на экране
                Vector2i windowPos = windowCS.getCoords(0, i, ownCS);
                // каждый 10 штрих увеличенного размера
                float strokeHeight = i % 10 == 0 ? 5 : 2;
                // рисуем горизонтальный штрих
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x + strokeHeight, windowPos.y, paint);
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x - strokeHeight, windowPos.y, paint);
            }
        }
        // восстанавливаем область рисования
        canvas.restore();
    }


    /**
     * Очистить задачу
     */
    public void clear() {
        points.clear();
        triangle.clear();
        solved = false;
    }
    private double findCos(Vector2d vec1, Vector2d vec2, Vector2d depend) {
        vec1 = new Vector2d(vec1.x - depend.x, vec1.y - depend.y);
        vec2 = new Vector2d(vec2.x - depend.x, vec2.y - depend.y);
        return (vec1.x * vec2.x + vec1.y * vec2.y) /
                Math.sqrt(vec1.x * vec1.x + vec1.y * vec1.y) /
                Math.sqrt(vec2.x * vec2.x + vec2.y * vec2.y);
    }

    private boolean isCrossed(Vector2d i, Vector2d j, Vector2d a, Vector2d b) {
        if (i.x== j.x) {
            if (a.x == b.x) {
                return a.x == i.x;
            }
            return (j.x >= a.x && j.x <= b.x) || (j.x >= b.x && j.x <= a.x);
        } else if (a.x == b.x) {
            double k = (j.y - i.y) / (j.x - i.x);
            double p = j.y - k * j.x;
            double yCross = k * a.x + p;
            return (yCross >= a.y && yCross <= b.y) || (yCross >= b.y && yCross <= a.y);
        } else{
            double k1 = (j.y - i.y) / (j.x - i.x);
            double k2 = (b.y - a.y) / (b.x - a.x);
            if (k1 == k2) {
                return false;
            }
            double p1 = j.y - k1 * j.x;
            double p2 = b.y - k2 * b.x;
            double xCross = (p2 - p1) / (k1 - k2);
            return (xCross >= a.x && xCross <= b.x) || (xCross >= b.x && xCross <= a.x);
        }
    }

    /**
     * Решить задачу
     */
    public void solve() {
        // очищаем списки
        answer.clear();

        // перебираем пары точек
        double shortest = 0;
        boolean isFirst = true;
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                boolean isCross = false;

                for (int n = 0; n < triangle.size(); n++) {
                    for (int m = n + 1; m < triangle.size(); m++) {
                        if (isCrossed(points.get(i).getPos(), points.get(j).getPos(), triangle.get(n).getPos(), triangle.get(m).getPos())) {
                            isCross = true;
                        }
                    }
                }

                if (isCross) continue;

                double a = points.get(j).getPos().y - points.get(i).getPos().y;
                double b = points.get(i).getPos().x - points.get(j).getPos().x;
                double c = points.get(i).getPos().y * (points.get(j).getPos().x - points.get(i).getPos().x) -
                        points.get(i).getPos().x * (points.get(j).getPos().y - points.get(i).getPos().y);

                double sqrt = Math.sqrt(a * a + b * b);

                if (isFirst) {
                    answer.add(points.get(i));
                    answer.add(points.get(j));
                    answer.add(triangle.get(0));
                    shortest = Math.abs(a * triangle.get(0).getPos().x + b * triangle.get(0).getPos().y + c) / sqrt;
                    isFirst = false;
                }

                for (Point point : triangle) {
                    if ((Math.abs(a * point.getPos().x + b * point.getPos().y + c) / sqrt) < shortest) {
                        answer.set(0, points.get(i));
                        answer.set(1, points.get(j));
                        answer.set(2, point);
                    }
                }
            }
        }

        // задача решена
        solved = true;
    }

    /**
     * Отмена решения задачи
     */
    public void cancel() {
        solved = false;
    }

    /**
     * проверка, решена ли задача
     *
     * @return флаг
     */
    public boolean isSolved() {
        return solved;
    }

    /**
     * Масштабирование области просмотра задачи
     *
     * @param delta  прокрутка колеса
     * @param center центр масштабирования
     */
    public void scale(float delta, Vector2i center) {
        if (lastWindowCS == null) return;
        // получаем координаты центра масштабирования в СК задачи
        Vector2d realCenter = ownCS.getCoords(center, lastWindowCS);
        // выполняем масштабирование
        ownCS.scale(1 + delta * WHEEL_SENSITIVE, realCenter);
    }

    /**
     * Получить положение курсора мыши в СК задачи
     *
     * @param x        координата X курсора
     * @param y        координата Y курсора
     * @param windowCS СК окна
     * @return вещественный вектор положения в СК задачи
     */
    @JsonIgnore
    public Vector2d getRealPos(int x, int y, CoordinateSystem2i windowCS) {
        return ownCS.getCoords(x, y, windowCS);
    }


    /**
     * Рисование курсора мыши
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     * @param font     шрифт
     * @param pos      положение курсора мыши
     */
    public void paintMouse(Canvas canvas, CoordinateSystem2i windowCS, Font font, Vector2i pos) {
        // создаём перо
        try (var paint = new Paint().setColor(TASK_GRID_COLOR)) {
            // сохраняем область рисования
            canvas.save();
            // рисуем перекрестие
            canvas.drawRect(Rect.makeXYWH(0, pos.y - 1, windowCS.getSize().x, 2), paint);
            canvas.drawRect(Rect.makeXYWH(pos.x - 1, 0, 2, windowCS.getSize().y), paint);
            // смещаемся немного для красивого вывода текста
            canvas.translate(pos.x + 3, pos.y - 5);
            // положение курсора в пространстве задачи
            Vector2d realPos = getRealPos(pos.x, pos.y, lastWindowCS);
            // выводим координаты
            canvas.drawString(realPos.toString(), 0, 0, font, paint);
            // восстанавливаем область рисования
            canvas.restore();
        }
    }

}
