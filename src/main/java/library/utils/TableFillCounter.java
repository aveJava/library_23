package library.utils;

/**
 * Вспомогательный класс, использующийся для закрашивания таблиц зеброй
 * (четные строки одного цвета, нечетные - другого)
 * */
public class TableFillCounter {
    private int value = 0;

    public boolean isNextLineFilled() {
        value++;
        if (value % 2 == 0) return true;
        else return false;
    }
}
