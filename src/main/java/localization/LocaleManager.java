package localization;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Синглтон-класс для управления локализацией приложения.
 * Позволяет менять текущую локаль, загружать строковые ресурсы,
 * уведомлять подписчиков об изменении языка, а также локализовать стандартные
 * компоненты Swing, такие как кнопки в диалогах JOptionPane.
 */
public class LocaleManager {
    private static LocaleManager instance;
    private ResourceBundle bundle;
    private Locale currentLocale;
    private final List<LocaleChangeListener> listeners = new ArrayList<>();
    private final LocaleStorage localeStorage = new LocaleStorage();

    /**
     * Приватный конструктор с инициализацией текущей локали
     * из сохраненного значения и загрузкой соответствующего ресурса.
     */
    private LocaleManager() {
        String savedLanguage = localeStorage.load();
        currentLocale = new Locale(savedLanguage);
        bundle = ResourceBundle.getBundle("localization.messages", currentLocale);
    }

    /**
     * Возвращает единственный экземпляр менеджера локали (паттерн Singleton).
     * @return экземпляр LocaleManager
     */
    public static synchronized LocaleManager getInstance() {
        if (instance == null) {
            instance = new LocaleManager();
        }
        return instance;
    }

    /**
     * Регистрирует слушателя для получения уведомлений об изменении локали.
     * Если слушатель уже зарегистрирован, не добавляет его повторно.
     * @param listener слушатель изменений локали
     */
    public void addListener(LocaleChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Удаляет регистрацию слушателя изменений локали.
     * @param listener слушатель, который должен быть удален
     */
    public void removeListener(LocaleChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Устанавливает текущую локаль по языковому коду,
     * обновляет ресурсный бандл, сохраняет настройку,
     * локализует стандартные кнопки JOptionPane
     * и уведомляет всех зарегистрированных слушателей.
     * @param language языковой код локали (например, "ru", "en")
     */
    public void setLocale(String language) {
        currentLocale = new Locale(language);
        bundle = ResourceBundle.getBundle("localization.messages", currentLocale);
        localeStorage.save(language);
        localizeOptionPaneButtons();
        notifyListeners();
    }

    /**
     * Локализует стандартные кнопки диалогов JOptionPane,
     * используя текущий ресурсный бандл.
     */
    private void localizeOptionPaneButtons() {
        UIManager.put("OptionPane.yesButtonText", getString("dialog.yes"));
        UIManager.put("OptionPane.noButtonText", getString("dialog.no"));
        UIManager.put("OptionPane.cancelButtonText", getString("dialog.cancel"));
    }

    /**
     * Принудительно обновляет UI компонента и всех его потомков
     * с учетом текущей локали.
     * @param component компонент, у которого нужно обновить UI
     */
    public void updateUI(Component component) {
        SwingUtilities.updateComponentTreeUI(component);
    }

    /**
     * Сохраняет текущую локаль в постоянное хранилище.
     */
    public void saveCurrentLocale() {
        localeStorage.save(currentLocale.getLanguage());
    }

    /**
     * Получает локализованную строку по ключу из текущего ресурсного бандла.
     * @param key ключ строки в ресурсах
     * @return локализованная строка
     */
    public String getString(String key) {
        return bundle.getString(key);
    }

    /**
     * Статический аналог метода getString для быстрого получения строки
     * через синглтон-экземпляр LocaleManager.
     * @param key ключ строки в ресурсах
     * @return локализованная строка
     */
    public static String getStringStatic(String key) {
        return getInstance().getString(key);
    }

    /**
     * Возвращает текущую локаль.
     * @return текущий объект Locale
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Уведомляет всех зарегистрированных слушателей
     * о том, что локаль была изменена.
     */
    private void notifyListeners() {
        for (LocaleChangeListener listener : new ArrayList<>(listeners)) {
            listener.onLocaleChanged();
        }
    }
}
