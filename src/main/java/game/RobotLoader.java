package game;

import model.ExternalRobotGui;
import model.ExternalModelRobot;
import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Класс для загрузки роботов из JARников
 */
public class RobotLoader {

    /**
     * Загружает класс робота из Jar файла
     * @param jarFile Jarник с реализацией робота
     * @param className полное имя класса с пакетом для загрузки
     * @return экземпляр класса, реализующего ExternalRobot
     * @throws Exception выбрасывает если произошли ошибка
     */
    public ExternalModelRobot loadRobotFromJarModel(File jarFile, String className) throws Exception {
        URL jarUrl = jarFile.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl});
        Class<?> robotClass = classLoader.loadClass(className);
        return (ExternalModelRobot) robotClass.getDeclaredConstructor().newInstance();
    }

    /**
     * Загружает класс робота из Jar файла
     * @param jarFile Jarник с реализацией робота
     * @param className полное имя класса с пакетом для загрузки
     * @return экземпляр класса, реализующего ExternalRobot
     * @throws Exception выбрасывает если произошли ошибка
     */
    public ExternalRobotGui loadRobotFromJarGui(File jarFile, String className) throws Exception {
        URL jarUrl = jarFile.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl});
        Class<?> robotClass = classLoader.loadClass(className);
        return (ExternalRobotGui) robotClass.getDeclaredConstructor().newInstance();
    }

    /**
     * Создает и настраивает JFileChooser для выбора JAR-файлов
     * @param title заголовок диалогового окна
     * @return настроенный JFileChooser
     */
    public JFileChooser createJarFileChooser(String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".jar");
            }

            @Override
            public String getDescription() {
                return "JAR files (*.jar)";
            }
        });
        return fileChooser;
    }
}