package panels;

import io.github.humbleui.jwm.*;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import misc.CoordinateSystem2i;
import misc.Vector2i;

import java.util.function.Consumer;

import static app.Application.C_RAD_IN_PX;


/**
 * Класс панели
 */
public abstract class Panel implements Consumer<Event> {
    /**
     * отступ в пикселях
     */
    protected int padding;
    /**
     * переменная окна
     */
    protected final Window window;
    /**
     * флаг, нужно ли рисовать подложку
     */
    private final boolean drawBG;
    /**
     * цвет подложки
     */
    protected final int backgroundColor;
    /**
     * последнее движение мыши
     */
    protected Vector2i lastMove = new Vector2i(0, 0);
    /**
     * было ли оно внутри панели
     */
    protected boolean lastInside = false;
    /**
     * последняя СК окна
     */
    protected CoordinateSystem2i lastWindowCS;

    /**
     * Конструктор панели
     *
     * @param window          окно
     * @param drawBG          нужно ли рисовать подложку
     * @param backgroundColor цвет фона
     * @param padding         отступы
     */
    public Panel(Window window, boolean drawBG, int backgroundColor, int padding) {
        this.window = window;
        this.drawBG = drawBG;
        this.backgroundColor = backgroundColor;
        this.padding = padding;
    }

    /**
     * Функция рисования, вызывающаяся извне
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        // сохраняем область рисования
        canvas.save();
        // определяем область рисования
        canvas.clipRect(windowCS.getRect());
        // рисуем подложку, если выставлен флаг
        if (drawBG) {
            try (var paint = new Paint()) {
                // задаём цвет рисования
                paint.setColor(backgroundColor);
                // рисуем скруглённый прямоугольник как подложку
                canvas.drawRRect(windowCS.getRRect(C_RAD_IN_PX), paint);
            }
        }
        canvas.translate(windowCS.getMin().x, windowCS.getMin().y);
        // пользовательская реализация рисования
        paintImpl(canvas, windowCS);
        // восстанавливаем область рисования
        canvas.restore();
        // сохраняем СК окна
        lastWindowCS = windowCS;
    }

    /**
     * Метод под рисование в конкретной реализации
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public abstract void paintImpl(Canvas canvas, CoordinateSystem2i windowCS);

    /**
     * Проверка, содержит ли панель координаты
     *
     * @param pos положение
     * @return флаг, содержит или нет
     */
    public boolean contains(Vector2i pos) {
        if (lastWindowCS != null)
            return lastWindowCS.checkCoords(pos);
        return false;
    }

    /**
     * Обработчик событий
     * при перегрузке обязателен вызов реализации предка
     *
     * @param e событие
     */
    @Override
    public void accept(Event e) {
        if (e instanceof EventMouseMove ee) {
            // сохраняем последнее положение мыши
            lastMove = new Vector2i(ee);
            // сохраняем флаг, был ли курсор внутри панели
            lastInside = contains(lastMove);
        }
    }
}