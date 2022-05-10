package controls;

import io.github.humbleui.jwm.Event;
import io.github.humbleui.jwm.Window;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.TextLine;
import misc.CoordinateSystem2i;
import panels.GridPanel;

import static app.Colors.LABEL_TEXT_COLOR;
import static app.Fonts.FONT12;

/**
 * Заголовок
 */
public class Label extends GridPanel {
    /**
     * Текст заголовка
     */
    public String text;
    /**
     * Флаг, нужно ли выравнивать текст по центру по горизонтали
     */
    protected boolean centered;
    /**
     * Флаг, нужно ли выравнивать текст по центру по вертикали
     */
    protected boolean vcentered;

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
     * @param text            текст
     * @param centered        флаг, нужно ли выравнивать текст по центру по горизонтали
     * @param vcentered       флаг, нужно ли выравнивать текст по центру по вертикали
     */
    public Label(Window window, boolean drawBG, int backgroundColor, int padding,
                 int gridWidth, int gridHeight, int gridX, int gridY, int colspan, int rowspan,
                 String text, boolean centered, boolean vcentered) {
        super(window, drawBG, backgroundColor, padding, gridWidth, gridHeight,
                gridX, gridY, colspan, rowspan);
        this.text = text;
        this.centered = centered;
        this.vcentered = vcentered;
    }

    /**
     * Метод под рисование в конкретной реализации
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    @Override
    public void paintImpl(Canvas canvas, CoordinateSystem2i windowCS) {
        // сохраняем область рисования
        canvas.save();
        // создаём линию
        try (TextLine line = TextLine.make(text, FONT12)) {
            // получаем высоту текста
            int capHeight = (int) FONT12.getMetrics().getCapHeight();
            // если нужно центрировать по горизонтали
            if (centered)
                canvas.translate((windowCS.getSize().x - line.getWidth()) / 2.0f, 0);
            if (vcentered)
                canvas.translate(0, (windowCS.getSize().y - capHeight) / 2.0f);

            // рисуем текст
            try (Paint fg = new Paint().setColor(LABEL_TEXT_COLOR)) {
                canvas.drawTextLine(line, 0, capHeight, fg);
            }
        }
        // восстанавливаем области рисования
        canvas.restore();
    }

    /**
     * Обработчик событий
     *
     * @param e событие
     */
    @Override
    public void accept(Event e) {

    }
}
