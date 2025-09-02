package localization;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Синглтон-класс для изменения локали, после изменеия оповещает слушателей об произошедшем изменении
 */
public class LocaleManager {
    private static final LocaleManager instance = new LocaleManager();
    private Locale currentLocale = Locale.of("ru");
    private ResourceBundle bundle = ResourceBundle.getBundle("messages", currentLocale);
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Для реализации шаблона синглтон делаем приватный конструктор, чтобы
     * был гаранитированно один экземпляр класса
     */
    private LocaleManager(){}

    /**
     * Возвращает тот самый один экземпляр
     */
    public static LocaleManager getInstance(){
        return instance;
    }

    /**
     * Возвращает текущую локаль
     * @return
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Меняет текущую локаль на другую
     * @param newLocale изменяемая локаль
     */
    public void setCurrentLocale(Locale newLocale) {
        Locale oldLocalization = currentLocale;
        currentLocale = newLocale;
        bundle = ResourceBundle.getBundle("messages", currentLocale);
        support.firePropertyChange("locale", oldLocalization, newLocale);
    }

    /**
     * Добавляет слушателя изменений локали
     *
     * @param listener объект, реализующий {@code PropertyChangeListener}
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Возвращает локализованную строку
     */
    public String getString(String key) {
        return bundle.getString(key);
    }
}
