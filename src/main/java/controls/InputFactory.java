package controls;

import io.github.humbleui.jwm.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Фабрика полей ввода
 */
public class InputFactory {
    /**
     * Поля ввода
     */
    private static final List<Input> inputs = new ArrayList<>();
    /**
     * Таймер
     */
    private static final Timer timer = new Timer(true);
    /**
     * флаг, нужно ли рисовать курсоа
     */
    private static boolean cursorDraw = true;
    /**
     * группа индексов для переключения по tab
     */
    private static final List<Integer> tabGroup = new ArrayList<>();
    /**
     * положение в tab группе
     */
    private static int tabPos = 0;



    static {
        // запускаем таймер, срабатывающий каждые 500 мс
        // он попеременно включает и выключает рисование курсора
        // для имитации мигания
        timer.schedule(new TimerTask() {
            public void run() {
                cursorDraw = !cursorDraw;
            }
        }, 0, 500);
    }

    /**
     * Получить новое поле ввода
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
     * @param text            начальный текст
     * @param vcentered       флаг, нужно ли выравнивать текст по центру по вертикали
     * @param textColor       цвет текста
     * @param addToTabGroup   флаг, нужно ли добавить это поле в tab группу
     * @return Новое поле ввода
     */
    public static Input getInput(
            Window window, boolean drawBG, int backgroundColor, int padding,
            int gridWidth, int gridHeight, int gridX, int gridY, int colspan,
            int rowspan, String text, boolean vcentered, int textColor,
            boolean addToTabGroup
    ) {
        Input input = new Input(
                window, drawBG, backgroundColor, padding, gridWidth, gridHeight,
                gridX, gridY, colspan, rowspan, text, vcentered, textColor);
        inputs.add(input);
        if (addToTabGroup) {
            tabGroup.add(inputs.size() - 1);
        }

        // изначально ничего не выбрано, по первому tab
        // положение станет равным нулю, и мы получим первый
        // элемент tab группы
        tabPos = -1;

        return input;
    }

    /**
     * Снять фокус со всех полей ввода
     */
    public static void defocusAll() {
        // снимаем фокусы
        for (Input input : inputs)
            input.focused = false;
    }

    /**
     * Нужно ли рисовать курсор сейчас
     *
     * @return нужно ли рисовать курсор
     */
    public static boolean cursorDraw() {
        return cursorDraw;
    }

    /**
     * Следующий элемент
     */
    public static void nextTab() {
        if (tabGroup.isEmpty())
            return;
        tabPos++;
        if (tabPos > tabGroup.size() - 1)
            tabPos = 0;
        inputs.get(tabGroup.get(tabPos)).setFocus();
    }

    /**
     * Запрещаем вызов конструктора
     */
    private InputFactory() {
        throw new AssertionError("Вызов этого конструктора запрещён!");
    }

}
