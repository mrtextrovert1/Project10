package dialogs;

import app.Application;
import controls.Button;
import controls.MultiLineLabel;
import io.github.humbleui.jwm.*;
import io.github.humbleui.skija.Canvas;
import misc.CoordinateSystem2i;
import misc.Vector2i;
import panels.Panel;

import static app.Colors.BUTTON_COLOR;


/**
 * Панель управления
 */
public class PanelInfo extends Panel {
    /**
     * Отступы в панели управления
     */
    private static final int CONTROL_PADDING = 5;

    /**
     * Кнопка принять
     */
    private final Button accept;
    /**
     * заголовок
     */
    private final MultiLineLabel infoLabel;
    /**
     * текст заголовка делаем статическим, чтобы можно было менять его
     * из любого места, каждый экземпляр панели информации будет обращаться
     * к этому полю при рисовании, но логика работы программы
     * не предполагает создания нескольких экземпляров, так что всё ок
     */
    private static String labelText;


    /**
     * Панель управления
     *
     * @param window  окно
     * @param drawBG  флаг, нужно ли рисовать подложку
     * @param color   цвет подложки
     * @param padding отступы
     */
    public PanelInfo(Window window, boolean drawBG, int color, int padding) {
        super(window, drawBG, color, padding);

        // добавление вручную
        infoLabel = new MultiLineLabel(window, false, backgroundColor, CONTROL_PADDING,
                1, 2, 0, 0, 1, 1, "",
                true, true);


        accept = new Button(
                window, false, BUTTON_COLOR, CONTROL_PADDING,
                1, 2, 0, 1, 1, 1, "ОК",
                true, true);
        accept.setOnClick(() -> Application.currentMode = Application.Mode.WORK);

    }


    /**
     * Вывести информацию
     *
     * @param text текст
     */
    public static void show(String text) {
        // задаём новый текст
        labelText = text;
        // переключаем вывод приложения на режим информации
        Application.currentMode = Application.Mode.INFO;
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
            accept.checkOver(lastWindowCS.getRelativePos(new Vector2i(ee)));
            // событие нажатия мыши
        } else if (e instanceof EventMouseButton) {
            if (!lastInside)
                return;

            Vector2i relPos = lastWindowCS.getRelativePos(lastMove);
            accept.click(relPos);
            // перерисовываем окно
            window.requestFrame();
            // обработчик ввода текста
        } else if (e instanceof EventKey ee) {
            if (ee.isPressed()) {
                // получаем код клавиши
                Key key = ee.getKey();
                // перебираем варианты
                switch (key) {
                    // если esc
                    case ESCAPE -> Application.currentMode = Application.Mode.WORK;
                    // если enter
                    case ENTER -> Application.currentMode = Application.Mode.WORK;
                }
            }
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
        infoLabel.text = labelText;
        accept.paint(canvas, windowCS);
        infoLabel.paint(canvas, windowCS);
    }
}
