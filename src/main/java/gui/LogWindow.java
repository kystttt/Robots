package gui;

import localization.LocaleManager;
import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import state.WindowAction;

import javax.swing.*;
import java.awt.*;
import java.awt.EventQueue;

public class LogWindow extends BaseWindow implements LogChangeListener, WindowAction{
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;

    public LogWindow(LogWindowSource logSource) {
        super(LocaleManager.getInstance().getString("log.title"), 300, 800, 10, 10);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }


    @Override
    public void updateLocale() {
        setTitle(LocaleManager.getInstance().getString("log.title"));
        updateLogContent();
        invalidate();
        validate();
        repaint();
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public String getNameOfWindow() {
        return "LogWindow";
    }
}
