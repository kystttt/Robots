package log;

import java.util.Collections;
import java.util.Set;
import java.util.ArrayList;
import java.util.WeakHashMap;

/**
 * Источник сообщений для окна логов, хранит сообщения в кольцевом буфере фиксированного размера
 * Уведомляет зарегестрированных пользователей о новых сообщениях
 */
public class LogWindowSource
{
    private final CircularBuffer<LogEntry> m_messages;
    private final Set<LogChangeListener> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;

    /**
     * Создает новый источник лога с указанной емкостью
     * @param iQueueLength
     */
    public LogWindowSource(int iQueueLength) 
    {
        m_messages = new CircularBuffer<>(iQueueLength);
        m_listeners = Collections.newSetFromMap(new WeakHashMap<>());
    }

    /**
     * Регистрирует слушателя
     */
    public void registerListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.add(listener);
            m_activeListeners = null;
        }
    }

    /**
     * Отменяет регистрацию слушателя
     */
    public void unregisterListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.remove(listener);
            m_activeListeners = null;
        }
    }

    /**
     * Добавляет сообщение в лог и уведомляет слушателей
     * @param logLevel
     * @param strMessage
     */
    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        m_messages.add(entry);
        LogChangeListener [] activeListeners = m_activeListeners;
        if (activeListeners == null)
        {
            synchronized (m_listeners)
            {
                if (m_activeListeners == null)
                {
                    activeListeners = m_listeners.toArray(new LogChangeListener [0]);
                    m_activeListeners = activeListeners;
                }
            }
        }
        for (LogChangeListener listener : activeListeners)
        {
            listener.onLogChanged();
        }
    }

    /**
     * Показывает размер лога
     * @return
     */
    public int size()
    {
        return m_messages.size();
    }

    public Iterable<LogEntry> range(int startFrom, int count)
    {
        if (startFrom < 0 || startFrom >= m_messages.size())
        {
            return Collections.emptyList();
        }
        int indexTo = Math.min(startFrom + count, m_messages.size());
        return m_messages.getRange(startFrom, indexTo);
    }

    /**
     * Показывает все сообщения в логе
     */
    public Iterable<LogEntry> all()
    {
        return m_messages;
    }
}