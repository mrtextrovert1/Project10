package misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Вспомогательная библиотека
 */
public class Misc {

    /**
     * Получить цвет по компонентам
     *
     * @param a прозрачность
     * @param r красная компонента
     * @param g зелёная компонента
     * @param b синяя компонента
     * @return целове число с цветом
     */
    public static int getColor(int a, int r, int g, int b) {
        return ((a * 256 + r) * 256 + g) * 256 + b;
    }

    /**
     * Сформировать набор строк не превышающих заданную длину
     *
     * @param line      исходная длинная строка
     * @param maxLength максимальная длина строки
     * @return список строк
     */
    public static List<String> limit(String line, int maxLength) {
        // создаём список
        List<String> lst = new ArrayList<>();
        // если вся строка умещается в огланичение
        if (line.length() < maxLength) {
            // добавляем её к списку
            lst.add(line);
            // возвращаем его
            return lst;
        }
        // теперь будем извлекать из строки максимально длинную последовательность слов,
        // начиная с первого, такую, чтобы длина полученной строки умещалась
        // в заданный диапазон
        int spacePos;
        do {
            // ищем последний индекс пробела в подстроке заданной длины
            spacePos = line.substring(0, maxLength).lastIndexOf(' ');
            // если индекс пробела найден
            if (spacePos > 0) {
                // добавляем в список подстроку до этого пробела
                lst.add(line.substring(0, spacePos));
                // а саму строку обрезаем начиная со следующего после пробела символа
                line = line.substring(spacePos + 1);
            }
            // повторяем пока есть пробел в
        } while (spacePos > 0 && line.length() > maxLength);
        // добавляем оставшуюся строку к списку
        lst.add(line);
        // возвращаем его
        return lst;
    }

    /**
     * Запрещаем вызов конструктора
     */
    private Misc() {
        throw new AssertionError("Вызов этого конструктора запрещён");
    }

}
