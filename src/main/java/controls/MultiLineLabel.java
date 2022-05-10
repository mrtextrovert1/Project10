package controls;

import io.github.humbleui.jwm.Window;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.TextLine;
import misc.CoordinateSystem2i;
import panels.GridPanel;

import static app.Colors.MULTILINE_TEXT_COLOR;
import static app.Fonts.FONT12;

/**
 * Многострочный заголовок
 */
public class MultiLineLabel extends GridPanel {
    /**
     * Текст
     */
    public String text;
    /**
     * Последняя высота текста
     */
    protected int lastTextHeight;
    /**
     * Последняя ширина текста
     */
    protected int lastTextWidth;
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
    public MultiLineLabel(
            Window window, boolean drawBG, int backgroundColor, int padding, int gridWidth,
            int gridHeight, int gridX, int gridY, int colspan, int rowspan, String text,
            boolean centered, boolean vcentered) {
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
        // сохраняем области рисования
        canvas.save();
        // высота текста
        int capHeight = (int) FONT12.getMetrics().getCapHeight();
        // говорим, что первая y координата - это высота текста
        int y = capHeight;
        // начальное значение для последней сохранённой высоты
        lastTextHeight = y;
        // начальное значение для последней сохранённой ширины
        lastTextWidth = 0;


        // перебираем строки текста
        for (String lineText : text.split("\n")) {
            // создаём линию как объект рисования
            try (TextLine line = TextLine.make(lineText, FONT12)) {
                // последняя сохранённая ширина будет равна максимальной ширине строки
                lastTextWidth = Math.max((int) line.getWidth() + 2 * padding, lastTextWidth);
            }
            // последняя сохранённая высота равна y-координате
            lastTextHeight += 2 * capHeight;
        }
        // увеличиваем последнюю сохранённую высоту на высоту текста
        lastTextHeight += capHeight;

        // если нужно центрировать по горизонтали
        if (centered)
            canvas.translate((windowCS.getSize().x - lastTextWidth) / 2.0f, 0);
        if (vcentered)
            canvas.translate(0, (windowCS.getSize().y - lastTextHeight) / 2.0f);


        try (Paint fg = new Paint().setColor(MULTILINE_TEXT_COLOR)) {
            // перебираем строки текста
            for (String lineText : text.split("\n")) {
                // создаём линию как объект рисования
                try (TextLine line = TextLine.make(lineText, FONT12)) {
                    // рисуем линию
                    canvas.save();

                    // если нужно центрировать по горизонтали
                    if (centered)
                        canvas.translate((lastTextWidth - line.getWidth()) / 2, 0);

                    canvas.drawTextLine(line, padding, y + padding + capHeight, fg);
                    // увеличиваем y координату на двойную высоту текста
                    y += 2 * capHeight;
                    canvas.restore();
                }
            }
        }

        // восстанавливаем области рисования
        canvas.restore();
    }

}
