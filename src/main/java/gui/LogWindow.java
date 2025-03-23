package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

import static java.awt.Frame.ICONIFIED;
import static java.awt.Frame.NORMAL;

public class LogWindow extends JInternalFrame implements LogChangeListener, WindowAction {
    private LogWindowSource m_logSource;
    private TextArea m_logContent;

    public LogWindow(LogWindowSource logSource) {
        super("Протокол работы", true, true, true, true);
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
    public Map<String, Integer> saveWindowState() {
        Map<String, Integer> state = new HashMap<>();
        state.put("x", getLocation().x);
        state.put("y", getLocation().y);
        state.put("width", getWidth());
        state.put("height", getHeight());
        if (isIcon()) {
            state.put("state", ICONIFIED);
        } else {
            state.put("state", NORMAL);
        }
        return state;
    }

    @Override
    public void loadWindowState(Map<String, Integer> params) {
        if (params != null) {
            setLocation(params.getOrDefault("x", 50), params.getOrDefault("y", 50));
            setSize(params.getOrDefault("width", 300), params.getOrDefault("height", 800));
            if (params.getOrDefault("state", NORMAL) == ICONIFIED) {
                try {
                    setIcon(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getNameOfWindow() {
        return "LogWindow";
    }
}
