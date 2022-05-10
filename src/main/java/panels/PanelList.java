package panels;

import controls.Button;
import io.github.humbleui.jwm.*;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.RRect;
import misc.CoordinateSystem2i;
import misc.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static app.Colors.*;


/**
 * Панель списка
 */
public class PanelList extends GridPanel {
    /**
     * Отступ между элементами списка
     */
    public static final int LIST_PADDING = 5;
    /**
     * Отступ скроллера
     */
    public static final int SCROLLER_PADDING = 20;
    /**
     * Ширина скроллера
     */
    public static final int SCROLLER_WIDTH = 7;
    /**
     * поставщик списков строк
     */
    private final Supplier<List<String>> lines;
    /**
     * кол-во отображаемых строк
     */
    private final int renderLineCnt;
    /**
     * список кнопок для отображения элементов
     */
    private final List<Button> buttons;
    /**
     * индекс первого отображаемого элемента
     */
    private int start;
    /**
     * id выбранной кнопки
     */
    int selectedButtonId;

    /**
     * Панель на сетке
     *
     * @param window          окно
     * @param drawBG          флаг, нужно ли рисовать подложку
     * @param backgroundColor цвет подложки
     * @param padding         отступы
     * @param gridWidth       кол-во ячеек сетки по ширине
     * @param gridHeight      кол-во ячеек сетки по высоте
     * @param gridX           координата в сетке x
     * @param gridY           координата в сетке y
     * @param colspan         кол-во колонок, занимаемых панелью
     * @param rowspan         кол-во строк, занимаемых панелью
     * @param lines           поставщик списков строк
     * @param lineConsumer    обработчик выбранного элемента списка
     * @param renderLineCnt   кол-во отображаемых линий
     */
    public PanelList(
            Window window, boolean drawBG, int backgroundColor, int padding, int gridWidth, int gridHeight,
            int gridX, int gridY, int colspan, int rowspan, Supplier<List<String>> lines,
            Consumer<String> lineConsumer, int renderLineCnt
    ) {
        super(window, drawBG, backgroundColor, padding, gridWidth, gridHeight, gridX, gridY, colspan, rowspan);
        this.lines = lines;
        this.renderLineCnt = renderLineCnt;

        buttons = new ArrayList<>();
        // создаём кнопки для отображения элементов списка
        for (int i = 0; i < renderLineCnt; i++) {
            // создаём кнопку
            Button elem = new Button(
                    window, false, BUTTON_COLOR, LIST_PADDING,
                    1, renderLineCnt, 0, i, 1, 1, "",
                    false, true);
            // по клику кнопки в обработчик возвращаем её текст
            elem.setOnClick(() -> lineConsumer.accept(elem.text));
            buttons.add(elem);
        }
    }

    /**
     * Обработчик событий
     * при перегрузке обязателен вызов реализации предка
     *
     * @param e событие
     */
    @Override
    public void accept(Event e) {
        super.accept(e);
        // событие движения мыши
        if (e instanceof EventMouseMove ee) {
            for (int i = 0; i < buttons.size(); i++) {
                if (i == selectedButtonId) {
                    buttons.get(i).selected = true;
                } else
                    buttons.get(i).checkOver(lastWindowCS.getRelativePos(new Vector2i(ee)));

            }
            // событие нажатия мыши
        } else if (e instanceof EventMouseButton) {
            if (!lastInside)
                return;

            Vector2i relPos = lastWindowCS.getRelativePos(lastMove);

            // пробуем кликнуть по всем кнопкам
            for (int i = 0; i < buttons.size(); i++) {
                buttons.get(i).click(relPos);
                if (buttons.get(i).contains(relPos))
                    selectedButtonId = i;
            }

            // перерисовываем окно
            window.requestFrame();
            // обработчик ввода текста
        } else if (e instanceof EventMouseScroll ee) {
            if (lastMove != null && lastInside) {
                // если строк для вывода меньше чем строк отображения
                if (lines.get().size() <= renderLineCnt)
                    return;

                start -= (int) ee.getDeltaY() / 100;
                if (start >= lines.get().size() - renderLineCnt)
                    start = lines.get().size() - renderLineCnt - 1;
                else if (start < 0)
                    start = 0;

            }
            window.requestFrame();
        }
    }

    /**
     * Метод под рисование в конкретной реализации
     *
     * @param canvas область рисования
     * @param windowCS СК окна
     */
    @Override
    public void paintImpl(Canvas canvas, CoordinateSystem2i windowCS) {
        // рисуем список кнопками
        for (int i = start; i < Math.min(start + renderLineCnt, lines.get().size()); i++) {
            buttons.get(i - start).text = lines.get().get(i);
            buttons.get(i - start).paint(canvas, windowCS);
        }
        // если строк для вывода больше чем строк отображения
        // рисуем скроллер
        if (lines.get().size() >= renderLineCnt) {
            // рисуем полосу прокрутки
            canvas.save();
            // создаём кисть
            try (var paint = new Paint()) {
                // сохраняем область рисования
                canvas.save();
                // задаём цвет подложки
                paint.setColor(SCROLLER_BACKGROUND_COLOR);
                // получаем высоту скроллера с учётом отступов
                int realHeight = (windowCS.getSize().y - 2 * SCROLLER_PADDING);
                // рисуем скруглённую подложку
                canvas.drawRRect(RRect.makeXYWH(windowCS.getSize().x - SCROLLER_WIDTH, SCROLLER_PADDING, SCROLLER_WIDTH,
                        realHeight, 4), paint);
                // задаём цвет ползунка
                paint.setColor(SCROLLER_COLOR);
                // рисуем его
                canvas.drawRRect(RRect.makeXYWH(
                        windowCS.getSize().x - SCROLLER_WIDTH,
                        SCROLLER_PADDING + (float) realHeight * start / lines.get().size(), SCROLLER_WIDTH,
                        (float) realHeight * (renderLineCnt + 1) / lines.get().size(),
                        4), paint);
            }
            canvas.restore();
        }
    }
}
