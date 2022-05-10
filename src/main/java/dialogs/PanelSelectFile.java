package dialogs;

import app.Application;
import controls.Button;
import controls.Input;
import controls.Label;
import controls.MultiLineLabel;
import io.github.humbleui.jwm.*;
import io.github.humbleui.skija.Canvas;
import misc.CoordinateSystem2i;
import misc.Vector2i;
import panels.Panel;
import panels.PanelList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static app.Colors.*;

/**
 * Панель управления
 */
public class PanelSelectFile extends Panel {
    /**
     * Отступы в панели управления
     */
    private static final int CONTROL_PADDING = 5;
    /**
     * Строка для обозначения родительской папки
     */
    private static final String PARENT_FOLDER_STR = "..";
    /**
     * Кнопка принять
     */
    private final Button accept;
    /**
     * Кнопка отмена
     */
    private final Button cancel;
    /**
     * заголовок
     */
    private final MultiLineLabel infoLabel;
    /**
     * заголовок пути
     */
    private final Input pathInput;
    /**
     * текст заголовка делаем статическим, чтобы можно было менять его
     * из любого места, каждый экземпляр панели информации будет обращаться
     * к этому полю при рисовании, но логика работы программы
     * не предполагает создания нескольких экземпляров, так что всё ок
     */
    private static String labelText;
    /**
     * Путь к файлу
     */
    static String folderPath = "src/main/resources";
    /**
     * Панель списка
     */
    private final PanelList listPanel;
    /**
     * путь к тексту
     */
    private static String pathText = "";
    /**
     * Обработчик выбранного файла
     */
    private static Consumer<String> processFile;
    /**
     * Заголовок поля ввода пути файла
     */
    private static Label pathLabel;

    /**
     * Панель управления
     *
     * @param window  окно
     * @param drawBG  флаг, нужно ли рисовать подложку
     * @param color   цвет подложки
     * @param padding отступы
     */
    public PanelSelectFile(Window window, boolean drawBG, int color, int padding) {
        super(window, drawBG, color, padding);

        // добавление вручную
        infoLabel = new MultiLineLabel(window, false, backgroundColor, CONTROL_PADDING,
                6, 5, 2, 0, 2, 1, "",
                true, true);


        listPanel = new PanelList(window, false, APP_BACKGROUND_COLOR, CONTROL_PADDING,
                6, 5, 2, 1, 2, 2, PanelSelectFile::getFileList,
                s -> {
                    processSelectedFile(s);
                    window.requestFrame();
                }, 10);


        pathInput = new Input(window, false, backgroundColor, CONTROL_PADDING,
                6, 5, 2, 4, 2, 1, "",
                true, MULTILINE_TEXT_COLOR);
        pathLabel = new Label(window, false, backgroundColor, CONTROL_PADDING,
                6, 5, 1, 4, 1, 1, "Путь к файлу:",
                true, true);

        accept = new Button(
                window, false, BUTTON_COLOR, CONTROL_PADDING,
                6, 5, 2, 3, 1, 1, "ОК",
                true, true);

        accept.setOnClick(PanelSelectFile::accept);

        cancel = new Button(
                window, false, BUTTON_COLOR, CONTROL_PADDING,
                6, 5, 3, 3, 1, 1, "Отмена",
                true, true);

        cancel.setOnClick(() -> Application.currentMode = Application.Mode.WORK);

    }

    /**
     * Выбрать текущий файл
     */
    private static void accept() {
        // переводим режим обратно в основной
        Application.currentMode = Application.Mode.WORK;
        // обрабатываем полученный файл
        processFile.accept(pathText);
    }

    /**
     * Вывести информацию
     *
     * @param caption             текст заголовка
     * @param processFileConsumer обработчик выбранного файла
     */
    public static void show(String caption, Consumer<String> processFileConsumer) {
        // задаём новый текст
        labelText = caption;
        // переключаем вывод приложения на режим информации
        Application.currentMode = Application.Mode.FILE;
        // сохраняем обработчик выбранного файла
        processFile = processFileConsumer;

    }

    /**
     * ОБработчик выбранного файла
     *
     * @param fileName название файла
     */
    public static void processSelectedFile(String fileName) {
        if (fileName.equals(PARENT_FOLDER_STR)) {
            int lastIndex = folderPath.lastIndexOf("/");
            if (lastIndex > 0)
                folderPath = folderPath.substring(0, lastIndex);
        } else if (new File(folderPath + "/" + fileName).isDirectory())
            folderPath += "/" + fileName;
        else
            pathText = folderPath + "/" + fileName;

    }

    /**
     * Получить список файлов
     *
     * @return список текстовых представлений файлов
     */
    public static List<String> getFileList() {
        ArrayList<String> lst = new ArrayList<>();
        lst.add(PARENT_FOLDER_STR);
        for (File file : Objects.requireNonNull(new File(folderPath).listFiles())) {
            if (file.isDirectory() || file.getName().matches("[a-zA-Z-_0-9]*.json"))
                lst.add(file.getName());
        }

        return lst;
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
            cancel.checkOver(lastWindowCS.getRelativePos(new Vector2i(ee)));
            listPanel.accept(e);
            pathInput.accept(e);
            // событие нажатия мыши
        } else if (e instanceof EventMouseButton) {
            if (!lastInside)
                return;


            Vector2i relPos = lastWindowCS.getRelativePos(lastMove);

            accept.click(relPos);
            cancel.click(relPos);
            listPanel.accept(e);
            pathInput.accept(e);
            // перерисовываем окно
            window.requestFrame();
            // обработчик ввода текста
        } else if (e instanceof EventMouseScroll) {
            listPanel.accept(e);
        } else if (e instanceof EventKey ee) {
            if (ee.isPressed()) {
                // получаем код клавиши
                Key key = ee.getKey();
                // перебираем варианты
                switch (key) {
                    // если esc
                    case ESCAPE -> Application.currentMode = Application.Mode.WORK;
                    // если enter
                    case ENTER -> accept();
                }
            }
            pathInput.accept(e);
            pathText = pathInput.getText();
        } else if (e instanceof EventTextInput) {
            pathInput.accept(e);
            pathText = pathInput.getText();
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
        pathInput.setText(pathText);
        accept.paint(canvas, windowCS);
        cancel.paint(canvas, windowCS);
        infoLabel.paint(canvas, windowCS);
        pathLabel.paint(canvas, windowCS);
        pathInput.paint(canvas, windowCS);
        listPanel.paint(canvas, windowCS);
    }
}
