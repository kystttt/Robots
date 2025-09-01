package log;

/**
 * Класс для работы с окном логов
 */
public final class Logger
{
    /**
     * Иниициализирует источник окна логов с максимальной длиной записи 5 сообщенийф
     */
    private static final LogWindowSource defaultLogSource;
    static {
        defaultLogSource = new LogWindowSource(5);
    }

    /**
     * Констур для предотвращения создания экземпляров этого класса, тк методы все статические
     */
    private Logger()
    {
    }

    /**
     * Добавляет сообщения Debug в логи
     */
    public static void debug(String strMessage)
    {
        defaultLogSource.append(LogLevel.Debug, strMessage);
    }

    /**
     * Добавляет сообщения Error в логи
     */
    public static void error(String strMessage)
    {
        defaultLogSource.append(LogLevel.Error, strMessage);
    }

    /**
     * Возвращает источник логов по умолчанию
     */
    public static LogWindowSource getDefaultLogSource()
    {
        return defaultLogSource;
    }
}
