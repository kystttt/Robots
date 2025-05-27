package log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Источник сообщений для окна лога с поддержкой подписки на изменения.
 * Хранит сообщения в кольцевом буфере фиксированного размера и уведомляет
 * зарегистрированных слушателей о новых сообщениях.
 */
public class LogWindowSource {
    private final CircularBuffer<LogEntry> m_messages;
    private final List<WeakReference<LogChangeListener>> m_listeners;

    /**
     * Создает новый источник лога с указанной емкостью.
     */
    public LogWindowSource(int queueLength) {
        m_messages = new CircularBuffer<>(queueLength);
        m_listeners = new ArrayList<>();
    }

    /**
     * Добавляет новое сообщение в лог.
     */
    public void append(LogEntry entry) {
        synchronized (this) {
            m_messages.add(entry);
        }
        notifyListeners();
    }

    /**
     * Добавляет сообщение с заданным уровнем в лог.
     * Уведомляет всех зарегистрированных слушателей об изменении лога.
     * @param level   уровень логирования
     * @param message текст сообщения
     */
    public void append(LogLevel level, String message) {
        LogEntry entry = new LogEntry(level, message);
        append(entry);
    }

    /**
     * Регистрирует слушателя изменений лога.
     */
    public void registerListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            for (WeakReference<LogChangeListener> ref : m_listeners) {
                LogChangeListener l = ref.get();
                if (l == listener) {
                    return;
                }
            }
            m_listeners.add(new WeakReference<>(listener));
        }
    }

    /**
     * Удаляет регистрацию слушателя
     */
    public void unregisterListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            Iterator<WeakReference<LogChangeListener>> it = m_listeners.iterator();
            while (it.hasNext()) {
                LogChangeListener l = it.next().get();
                if (l == null || l == listener) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Возвращает текущее количество сообщений в логе.
     */
    public int size() {
        synchronized (this) {
            return m_messages.size();
        }
    }

    /**
     * Возвращает диапазон сообщений из лога.
     */
    public Iterable<LogEntry> range(int startFrom, int count) {
        synchronized (this) {
            return m_messages.range(startFrom, count);
        }
    }

    /**
     * Возвращает все сообщения в логе.
     */
    public Iterable<LogEntry> all() {
        synchronized (this) {
            return m_messages;
        }
    }

    /**
     * Уведомляет всех активных слушателей об изменении лога.
     */
    private void notifyListeners() {
        synchronized (m_listeners) {
            Iterator<WeakReference<LogChangeListener>> it = m_listeners.iterator();
            while (it.hasNext()) {
                LogChangeListener listener = it.next().get();
                if (listener != null) {
                    listener.onLogChanged();
                } else {
                    it.remove();
                }
            }
        }
    }
}