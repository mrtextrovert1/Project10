package panels;

import controls.Label;
import io.github.humbleui.jwm.Event;
import io.github.humbleui.jwm.Window;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.FontMetrics;
import io.github.humbleui.skija.Paint;
import misc.CoordinateSystem2i;
import misc.Misc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static app.Application.PANEL_PADDING;
import static app.Colors.PANEL_BACKGROUND_COLOR;
import static app.Fonts.FONT12;

/**
 * Панель управления
 */
public class PanelLog extends GridPanel {
    /**
     * Кол-во строчек лога
     */
    private static final int LOG_LINES_CNT = 15;
    /**
     * Максимальная длина строки лога
     */
    private static final int MAX_LOG_LINE_LENGTH = 80;
    /**
     * Список записей лога
     */
    private static final List<Record> logs = new ArrayList<>();

    /**
     * Тип записи
     */
    enum RecordType {
        /**
         * Информация
         */
        INFO,
        /**
         * Предупреждение
         */
        WARNING,
        /**
         * Ошибка
         */
        ERROR,
        /**
         * Успех
         */
        SUCCESS
    }

    /**
     * Запись
     */
    record Record(RecordType recordType, String text, Date date) {
        /**
         * Строковое представление объекта
         *
         * @return строковое представление объекта
         */
        @Override
        public String toString() {
            return new SimpleDateFormat("  HH:mm:ss").format(date) + ": " + text;
        }
    }


    /**
     * Панель управления
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
    public PanelLog(
            Window window, boolean drawBG, int color, int padding, int gridWidth, int gridHeight,
            int gridX, int gridY, int colspan, int rowspan
    ) {
        super(window, drawBG, color, padding, gridWidth, gridHeight, gridX, gridY, colspan, rowspan);

    }

    /**
     * Получить цвет строки лога
     *
     * @param recordType тип записи
     * @return цвет строки лога
     */
    public static int getColor(RecordType recordType) {
        return switch (recordType) {
            case INFO -> Misc.getColor(144, 255, 255, 255);
            case WARNING -> Misc.getColor(144, 255, 255, 0);
            case ERROR -> Misc.getColor(144, 255, 0, 0);
            case SUCCESS -> Misc.getColor(144, 0, 255, 0);
        };
    }


    /**
     * Добавить в лога
     *
     * @param recordType тип записи
     * @param text       текст записи
     */
    public static void addToLog(RecordType recordType, String text) {
        for (String line : text.split("\n")) {
            for (String limitedLine : Misc.limit(line, MAX_LOG_LINE_LENGTH)) {
                while (logs.size() > LOG_LINES_CNT)
                    logs.remove(0);
                logs.add(new Record(recordType, limitedLine, Calendar.getInstance().getTime()));
            }
        }
    }

    /**
     * Добавить info запись
     *
     * @param text текст записи
     */
    public static void success(String text) {
        addToLog(RecordType.SUCCESS, text);
    }

    /**
     * Добавить info запись
     *
     * @param text текст записи
     */
    public static void info(String text) {
        addToLog(RecordType.INFO, text);
    }

    /**
     * Добавить warning запись
     *
     * @param text текст записи
     */
    public static void warning(String text) {
        addToLog(RecordType.WARNING, text);
    }

    /**
     * Добавить error запись
     *
     * @param text текст записи
     */
    public static void error(String text) {
        addToLog(RecordType.ERROR, text);
    }

    /**
     * Метод под рисование в конкретной реализации
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    @Override
    public void paintImpl(Canvas canvas, CoordinateSystem2i windowCS) {
        // создаём перо
        try (Paint paint = new Paint()) {
            // получаем метрики шрифта
            FontMetrics metrics = FONT12.getMetrics();
            // сохраняем область рисования
            canvas.save();
            // смещаем область рисования
            canvas.translate(padding, windowCS.getSize().y - padding - metrics.getDescent());
            // перебираем записи лога
            for (int i = logs.size() - 1; i >= 0; --i) {
                // получаем запись лога
                Record log = logs.get(i);
                // задаём цвет лога
                paint.setColor(getColor(log.recordType));
                // выводим строку на экран
                canvas.drawString(log.toString(), 0, 0, FONT12, paint);
                // смещаем область к следующей линии
                canvas.translate(0, -metrics.getCapHeight() - 8);
            }
            // восстанавливаем область рисования
            canvas.restore();
        }
    }
}