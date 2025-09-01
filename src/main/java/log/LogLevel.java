package log;

/**
 * LogLevel представляет набор предопределенных уровней важности для записей журнала.
 * Каждый уровень имеет числовое значение,
 * которое определяет его приоритет
 */
public enum LogLevel
{
    Trace(0),
    Debug(1),
    Info(2),
    Warning(3),
    Error(4),
    Fatal(5);
    
    private int m_iLevel;

    /**
     * Конструктор LogLevel. Инициализирует уровень с заданным числовым значением
     */
    private LogLevel(int iLevel)
    {
        m_iLevel = iLevel;
    }
    
    public int level()
    {
        return m_iLevel;
    }
}

