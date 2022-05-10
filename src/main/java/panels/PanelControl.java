package panels;

import controls.Button;
import app.Point;
import app.Task;
import controls.Input;
import controls.InputFactory;
import controls.Label;
import controls.MultiLineLabel;
import dialogs.PanelInfo;
import io.github.humbleui.jwm.*;
import io.github.humbleui.skija.Canvas;
import misc.CoordinateSystem2i;
import misc.Vector2d;
import misc.Vector2i;

import java.util.ArrayList;
import java.util.List;

import static app.Application.PANEL_PADDING;
import static app.Colors.*;

/**
 * Панель управления
 */
public class PanelControl extends GridPanel {
    /**
     * Текст задания
     */
    MultiLineLabel task;
    /**
     * Заголовки
     */
    public List<Label> labels;
    /**
     * Поля ввода
     */
    public List<Input> inputs;
    /**
     * Кнопки
     */
    public List<Button> buttons;
    /**
     * кнопка решения
     */
    private final Button solve;

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
    public PanelControl(
            Window window, boolean drawBG, int color, int padding, int gridWidth, int gridHeight,
            int gridX, int gridY, int colspan, int rowspan
    ) {
        super(window, drawBG, color, padding, gridWidth, gridHeight, gridX, gridY, colspan, rowspan);

        // создаём списки
        inputs = new ArrayList<>();
        labels = new ArrayList<>();
        buttons = new ArrayList<>();

        // задание
        task = new MultiLineLabel(
                window, false, backgroundColor, PANEL_PADDING,
                6, 7, 0, 0, 6, 2, Task.TASK_TEXT,
                false, true);
        // добавление вручную
        Label xLabel = new Label(window, false, backgroundColor, PANEL_PADDING,
                6, 7, 0, 2, 1, 1, "X", true, true);
        labels.add(xLabel);
        Input xField = InputFactory.getInput(window, false, FIELD_BACKGROUND_COLOR, PANEL_PADDING,
                6, 7, 1, 2, 2, 1, "0.0", true,
                FIELD_TEXT_COLOR, true);
        inputs.add(xField);
        Label yLabel = new Label(window, false, backgroundColor, PANEL_PADDING,
                6, 7, 3, 2, 1, 1, "Y", true, true);
        labels.add(yLabel);
        Input yField = InputFactory.getInput(window, false, FIELD_BACKGROUND_COLOR, PANEL_PADDING,
                6, 7, 4, 2, 2, 1, "0.0", true,
                FIELD_TEXT_COLOR, true);
        inputs.add(yField);

        Button addToFirstSet = new Button(
                window, false, backgroundColor, PANEL_PADDING,
                6, 7, 0, 3, 3, 1, "Добавить в первое\nмножество",
                true, true);
        addToFirstSet.setOnClick(() -> {
            // если числа введены верно
            if (!xField.hasValidDoubleValue()) {
                PanelLog.warning("X координата введена неверно");
            } else if (!yField.hasValidDoubleValue())
                PanelLog.warning("Y координата введена неверно");
            else
                PanelRendering.task.addPoint(
                        new Vector2d(xField.doubleValue(), yField.doubleValue()), Point.PointSet.FIRST_SET
                );
        });
        buttons.add(addToFirstSet);

        Button addToSecondSet = new Button(
                window, false, backgroundColor, PANEL_PADDING,
                6, 7, 3, 3, 3, 1, "Добавить во второе\nмножество",
                true, true);
        addToSecondSet.setOnClick(() -> {
            // если числа введены верно
            if (!xField.hasValidDoubleValue()) {
                PanelLog.warning("X координата введена неверно");
            } else if (!yField.hasValidDoubleValue())
                PanelLog.warning("Y координата введена неверно");
            else {
                PanelRendering.task.addPoint(
                        new Vector2d(xField.doubleValue(), yField.doubleValue()), Point.PointSet.SECOND_SET
                );
            }
        });
        buttons.add(addToSecondSet);

        // случайное добавление
        Label cntLabel = new Label(window, false, backgroundColor, PANEL_PADDING,
                6, 7, 0, 4, 1, 1, "Кол-во", true, true);
        labels.add(cntLabel);

        Input cntField = InputFactory.getInput(window, false, FIELD_BACKGROUND_COLOR, PANEL_PADDING,
                6, 7, 1, 4, 2, 1, "5", true,
                FIELD_TEXT_COLOR, true);
        inputs.add(cntField);

        Button addPoints = new Button(
                window, false, backgroundColor, PANEL_PADDING,
                6, 7, 3, 4, 3, 1, "Добавить\nслучайные точки",
                true, true);
        addPoints.setOnClick(() -> {
            // если числа введены верно
            if (!cntField.hasValidIntValue()) {
                PanelLog.warning("кол-во точек указано неверно");
            } else
                PanelRendering.task.addRandomPoints(cntField.intValue());
        });
        buttons.add(addPoints);

        Button clear = new Button(
                window, false, backgroundColor, PANEL_PADDING,
                6, 7, 0, 6, 3, 1, "Очистить",
                true, true);
        clear.setOnClick(() -> PanelRendering.task.clear());
        buttons.add(clear);

        solve = new Button(
                window, false, backgroundColor, PANEL_PADDING,
                6, 7, 3, 6, 3, 1, "Решить",
                true, true);
        solve.setOnClick(() -> {
            if (!PanelRendering.task.isSolved()) {
                PanelRendering.task.solve();
                String s = "Задача решена\n";

                PanelInfo.show(s + "\n\nНажмите Esc, чтобы вернуться");
                PanelLog.success(s);
                solve.text = "Сбросить";
            } else {
                cancelTask();
            }
            window.requestFrame();
        });
        buttons.add(solve);
    }

    /**
     * Обработчик событий
     *
     * @param e событие
     */
    @Override
    public void accept(Event e) {
        // вызываем обработчик предка
        super.accept(e);
        // событие движения мыши
        if (e instanceof EventMouseMove ee) {
            for (Input input : inputs)
                input.accept(ee);

            for (Button button : buttons) {
                if (lastWindowCS != null)
                    button.checkOver(lastWindowCS.getRelativePos(new Vector2i(ee)));
            }
            // событие нажатия мыши
        } else if (e instanceof EventMouseButton) {
            if (!lastInside)
                return;

            Vector2i relPos = lastWindowCS.getRelativePos(lastMove);

            // пробуем кликнуть по всем кнопкам
            for (Button button : buttons) {
                button.click(relPos);
            }

            // перебираем поля ввода
            for (Input input : inputs) {
                // если клик внутри этого поля
                if (input.contains(relPos)) {
                    // переводим фокус на это поле ввода
                    input.setFocus();
                }
            }
            // перерисовываем окно
            window.requestFrame();
            // обработчик ввода текста
        } else if (e instanceof EventTextInput ee) {
            for (Input input : inputs) {
                if (input.isFocused()) {
                    input.accept(ee);
                }
            }
            // перерисовываем окно
            window.requestFrame();
            // обработчик ввода клавиш
        } else if (e instanceof EventKey ee) {
            for (Input input : inputs) {
                if (input.isFocused()) {
                    input.accept(ee);
                }
            }
            // перерисовываем окно
            window.requestFrame();
        }
    }

    /**
     * Метод под рисование в конкретной реализации
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    @Override
    public void paintImpl(Canvas canvas, CoordinateSystem2i windowCS) {
        // выводим текст задачи
        task.paint(canvas, windowCS);

        // выводим кнопки
        for (Button button : buttons) {
            button.paint(canvas, windowCS);
        }
        // выводим поля ввода
        for (Input input : inputs) {
            input.paint(canvas, windowCS);
        }
        // выводим поля ввода
        for (Label label : labels) {
            label.paint(canvas, windowCS);
        }
    }

    /**
     * Сброс решения задачи
     */
    private void cancelTask() {
        PanelRendering.task.cancel();
        // Задаём новый текст кнопке решения
        solve.text = "Решить";
    }
}
