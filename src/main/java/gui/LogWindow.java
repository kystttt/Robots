package gui;

import localization.LocaleManager;
import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import state.WindowAction;

import javax.swing.*;
import java.awt.*;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LogWindow extends BaseWindow implements LogChangeListener, WindowAction, PropertyChangeListener {
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;

    public LogWindow(LogWindowSource logSource) {
        super(LocaleManager.getInstance().getString("log.window.title"), 300, 800, 10, 10);
        addPropertyChangeListener(this);
        LocaleManager.getInstance().addPropertyChangeListener(this);
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("locale".equals(evt.getPropertyName())) {
            setTitle(LocaleManager.getInstance().getString("log.window.title"));
        }
    }
}
