package panels;

import java.util.*;

import io.github.humbleui.jwm.*;
import io.github.humbleui.skija.*;
import io.github.humbleui.skija.RRect;
import misc.CoordinateSystem2i;

import static app.Colors.HELP_TEXT;
import static app.Colors.HELP_TEXT_BACKGROUND;
import static app.Fonts.FONT12;

/**
 * Панель поддержки
 */
public class PanelHelp extends GridPanel {
    /**
     * Отступ в списке
     */
    float HELP_PADDING = 8;

    /**
     * Управляющие сочетания клавиш
     */
    record Shortcut(String command, boolean ctrl, String text) {
    }

    /**
     * список управляющих сочетаний клавиш
     */
    public List<Shortcut> shortcuts = new ArrayList<>();

    /**
     * Панель поддержки
     *
     * @param window     окно
     * @param drawBG     флаг, нужно ли рисовать подложку
     * @param color      цвет подложки
     * @param padding    отступы
     * @param gridWidth  кол-во ячеек сетки по ширине
     * @param gridHeight кол-во ячеек сетки по высоте
     * @param gridX      координата в сетке x
     * @param gridY      координата в сетке y
     * @param colspan    кол-во колонок, занимаемых панелью
     * @param rowspan    кол-во строк, занимаемых панелью
     */
    public PanelHelp(
            Window window, boolean drawBG, int color, int padding, int gridWidth, int gridHeight,
            int gridX, int gridY, int colspan, int rowspan
    ) {
        super(window, drawBG, color, padding, gridWidth, gridHeight, gridX, gridY, colspan, rowspan);
        shortcuts.add(new Shortcut("O", true, "Открыть"));
        shortcuts.add(new Shortcut("S", true, "Сохранить"));
        shortcuts.add(new Shortcut("H", true, "Свернуть"));
        shortcuts.add(new Shortcut("1", true, "Во весь экран/Обычный размер"));
        shortcuts.add(new Shortcut("2", true, "Полупрозрачное окно/обычное"));
        shortcuts.add(new Shortcut("Esc", false, "Закрыть окно"));
        shortcuts.add(new Shortcut("ЛКМ", false, "Добавить в первое множество"));
        shortcuts.add(new Shortcut("ПКМ", false, "Добавить во второе множество"));
    }

    /**
     * Метод под рисование в конкретной реализации
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    @Override
    public void paintImpl(Canvas canvas, CoordinateSystem2i windowCS) {
        // получаем модификатор в зависимости от операционной системы
        // 8984 - код символа cmd у Mac
        String modifier = Platform.CURRENT == Platform.MACOS ? ((char) 8984 + " ") : "Ctrl ";

        // Получаем кисти
        try (Paint bg = new Paint().setColor(HELP_TEXT_BACKGROUND);
             Paint fg = new Paint().setColor(HELP_TEXT)) {
            // метрика фона
            FontMetrics metrics = FONT12.getMetrics();
            // высота букв
            float capHeight = metrics.getCapHeight();
            // ширина команды прибавления
            float bgWidth = 0;
            // получаем строку с модификатором
            try (TextLine line = TextLine.make(modifier + "W", FONT12)) {
                bgWidth = line.getWidth() + 4 * HELP_PADDING;
            }
            // получаем высоту
            float bgHeight = capHeight + HELP_PADDING * 2;

            // положение первой строки
            float x = HELP_PADDING;
            float y = HELP_PADDING;

            // перебираем комбинации
            for (Shortcut shortcut : shortcuts) {
                // получаем полный текст команды
                String shortcutCommand = shortcut.ctrl ? modifier + shortcut.command : shortcut.command;
                //  формируем строку команды
                try (TextLine line = TextLine.make(shortcutCommand, FONT12)) {
                    canvas.drawRRect(RRect.makeXYWH(x, y, bgWidth, bgHeight, 4), bg);
                    canvas.drawTextLine(line, x + (bgWidth - line.getWidth()) / 2, y + HELP_PADDING + capHeight, fg);
                }
                // формируем строку с описанием
                try (TextLine line = TextLine.make(shortcut.text, FONT12);) {
                    canvas.drawTextLine(line, x + bgWidth + HELP_PADDING, y + HELP_PADDING + capHeight, fg);
                }
                // смещаемся вниз на
                y += HELP_PADDING + capHeight * 2 + 2;
            }

        }
    }

}