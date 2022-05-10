package misc;

import io.github.humbleui.skija.*;

import static app.Colors.*;

/**
 * Cтатистика
 */
public class Stats {

    /**
     * Время старта
     */
    public long prevTime = System.nanoTime();
    /**
     * Очередь временных меток
     */
    private final SumQueue deltaTimes = new SumQueue();


    /**
     * Рисование
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     * @param font     шрифт
     * @param padding  отступ
     */
    public void paint(Canvas canvas, CoordinateSystem2i windowCS, Font font, int padding) {
        // создаём кисть
        try (var paint = new Paint()) {
            // сохраняем область рисования
            canvas.save();
            // смещаем
            canvas.translate(padding, windowCS.getSize().y - padding - 32);
            // задаём цвет подложки
            paint.setColor(STATS_BACKGROUND_COLOR);
            // X, Y, ширина, высота,
            // радиусы скругления для каждого угла
            canvas.drawRRect(RRect.makeXYWH(
                    0, 0, windowCS.getSize().x - padding * 2, 32,
                    4, 4, 0, 0), paint);
            paint.setColor(STATS_COLOR);

            // рисуем сам график
            for (int i = 0; i < deltaTimes.getLength(); i++) {
                float currentDelta = deltaTimes.get(i);
                canvas.drawRect(Rect.makeXYWH(i, Math.min(windowCS.getSize().y, 32 - currentDelta), 1, currentDelta), paint);
            }

            // рассчитываем длину очереди
            int len = windowCS.getSize().x - padding * 2;
            // если она получилась положительной и новая длина отличается от старой
            if (len > 0 && deltaTimes.getLength() != len) {
                // задаём новую длину
                deltaTimes.setLength(len);
            }

            // получаем текущее время в мс.
            long now = System.nanoTime();
            // переводим его в секунды
            deltaTimes.add((now - prevTime) / 1000000.0f);
            // сохраняем новое время
            prevTime = now;

            // восстанавливаем область рисования
            canvas.restore();

            // сохраняем область рисования
            canvas.save();
            // задаём цвет
            paint.setColor(STATS_TEXT_COLOR);
            // перемещаемся в правый верхний край
            canvas.translate(windowCS.getSize().x - padding - 50, padding + 15);
            // формируем строку с текущим fps
            String fps = String.format("%.01f", (1 / deltaTimes.getMean() * 1000));
            // выводим её
            canvas.drawString("FPS: " + fps, 0, 0, font, paint);
            // восстанавливаем область рисования
            canvas.restore();
        }
    }

}
