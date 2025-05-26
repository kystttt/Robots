package localization;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LocaleManager {
    private static LocaleManager instance;
    private ResourceBundle bundle;
    private Locale currentLocale;
    private final List<LocaleChangeListener> listeners = new ArrayList<>();
    private final LocaleStorage localeStorage = new LocaleStorage();

    private LocaleManager() {
        String savedLanguage = localeStorage.load();
        currentLocale = new Locale(savedLanguage);
        bundle = ResourceBundle.getBundle("localization.messages", currentLocale);
    }

    public static synchronized LocaleManager getInstance() {
        if (instance == null) {
            instance = new LocaleManager();
        }
        return instance;
    }

    public void addListener(LocaleChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(LocaleChangeListener listener) {
        listeners.remove(listener);
    }

    public void setLocale(String language) {
        currentLocale = new Locale(language);
        bundle = ResourceBundle.getBundle("localization.messages", currentLocale);
        localeStorage.save(language);
        localizeOptionPaneButtons();
        notifyListeners();
    }


    private void localizeOptionPaneButtons() {
        UIManager.put("OptionPane.yesButtonText", getString("dialog.yes"));
        UIManager.put("OptionPane.noButtonText", getString("dialog.no"));
        UIManager.put("OptionPane.cancelButtonText", getString("dialog.cancel"));
    }


    public void updateUI(Component component) {
        SwingUtilities.updateComponentTreeUI(component);
    }

    public void saveCurrentLocale() {
        localeStorage.save(currentLocale.getLanguage());
    }

    public String getString(String key) {
        return bundle.getString(key);
    }

    public static String getStringStatic(String key) {
        return getInstance().getString(key);
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    private void notifyListeners() {
        for (LocaleChangeListener listener : new ArrayList<>(listeners)) {
            listener.onLocaleChanged();
        }
    }
}
