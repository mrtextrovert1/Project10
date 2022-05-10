package controls;

import controls.MultiLineLabel;
import io.github.humbleui.jwm.Window;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.RRect;
import io.github.humbleui.types.IRect;
import misc.CoordinateSystem2i;
import misc.Vector2i;

import static app.Colors.BUTTON_COLOR;

/**
 * Класс кнопки
 */
public class Button extends MultiLineLabel {
    /**
     * Событие по нажатию
     */
    private Runnable onClick;
    /**
     * находится ли сейчас курсор над этой кнопкой
     */
    public boolean selected;

    /**
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
    public Button(
            Window window, boolean drawBG, int backgroundColor, int padding,
            int gridWidth, int gridHeight, int gridX, int gridY, int colspan,
            int rowspan, String text, boolean centered, boolean vcentered) {
        super(
                window, drawBG, backgroundColor, padding, gridWidth, gridHeight,
                gridX, gridY, colspan, rowspan, text, centered, vcentered
        );
        selected = false;
    }

    /**
     * Метод под рисование в конкретной реализации
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    @Override
    public void paintImpl(Canvas canvas, CoordinateSystem2i windowCS) {
        // сохраняем канвасы
        canvas.save();
        if (selected) {
            if (centered)
                canvas.translate((windowCS.getSize().x - lastTextWidth) / 2.0f + padding, 0);
            if (vcentered)
                canvas.translate(0, (windowCS.getSize().y - lastTextHeight) / 2.0f);

            try (Paint bg = new Paint().setColor(BUTTON_COLOR)) {
                var bounds = IRect.makeXYWH(0, 0, lastTextWidth, lastTextHeight);
                canvas.drawRRect(RRect.makeLTRB(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom(), 4), bg);
            }
        }
        // восстанавливаем канвасы
        canvas.restore();
        super.paintImpl(canvas, windowCS);
    }

    /**
     * Обработчик клика по кнопке
     *
     * @param pos положение курсора мыши
     */
    public void click(Vector2i pos) {
        if (onClick != null && contains(pos)) onClick.run();
    }

    /**
     * Задать обработчик нажатия
     *
     * @param onClick обработчик
     */
    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    /**
     * Проверить, что мышь над кнопкой
     *
     * @param pos положение курсора мыши
     */
    public void checkOver(Vector2i pos) {
        selected = contains(pos);
    }
}
