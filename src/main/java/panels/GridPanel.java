package panels;

import io.github.humbleui.jwm.Window;
import io.github.humbleui.skija.Canvas;
import misc.CoordinateSystem2i;

public abstract class GridPanel extends Panel {
    /**
     * кол-во ячеек сетки по ширине
     */
    protected final int gridWidth;
    /**
     * кол-во ячеек сетки по высоте
     */
    protected final int gridHeight;
    /**
     * x координата в сетке
     */
    protected final int gridX;
    /**
     * y координата в сетке
     */
    protected final int gridY;
    /**
     * кол-во колонок, занимаемых панелью
     */
    protected final int colspan;
    /**
     * кол-во строк, занимаемых панелью
     */
    protected final int rowspan;

    /**
     * Конструктор панели
     *
     * @param window          окно
     * @param drawBG          нужно ли рисовать подложку
     * @param backgroundColor цвет фона
     * @param padding         отступы
     * @param gridWidth       кол-во ячеек сетки по ширине
     * @param gridHeight      кол-во ячеек сетки по высоте
     * @param gridX           координата в сетке x
     * @param gridY           координата в сетке y
     * @param colspan         кол-во колонок, занимаемых панелью
     * @param rowspan         кол-во строк, занимаемых панелью
     */
    public GridPanel(Window window, boolean drawBG, int backgroundColor, int padding, int gridWidth, int gridHeight,
                     int gridX, int gridY, int colspan, int rowspan) {
        super(window, drawBG, backgroundColor, padding);
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.gridX = gridX;
        this.gridY = gridY;
        this.colspan = colspan;
        this.rowspan = rowspan;
    }

    /**
     * Рисование панели
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    @Override
    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        // рассчитываем размер ячейки таблицы
        int cellWidth = (windowCS.getSize().x - (gridWidth + 1) * padding) / gridWidth;
        int cellHeight = (windowCS.getSize().y - (gridHeight + 1) * padding) / gridHeight;

        // если неправильно рассчитаны
        if (cellWidth <= 0 || cellHeight <= 0)
            return;

        CoordinateSystem2i gridCS = new CoordinateSystem2i(
                padding + (cellWidth + padding) * gridX,
                padding + (cellHeight + padding) * gridY,
                cellWidth * colspan + padding * (colspan - 1),
                cellHeight * rowspan + padding * (rowspan - 1)
        );

        // рисуем ячейку вместо всей панели
        super.paint(canvas, gridCS);

    }

}
