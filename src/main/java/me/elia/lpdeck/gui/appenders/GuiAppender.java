package me.elia.lpdeck.gui.appenders;

import lombok.Setter;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import javax.swing.*;

@Setter
@Plugin(
        name = "GuiAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE
)
public class GuiAppender extends AbstractAppender {
    private JTextArea consoleArea;

    public GuiAppender(String name, Filter filter) {
        super(
                name,
                filter,
                PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss}] [%c{1}/%level]: %msg%n").build(),
                true,
                Property.EMPTY_ARRAY
        );
    }

    @PluginFactory
    public static GuiAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter
    ) {
        if (name == null) {
            LOGGER.error("No name provided for ConsoleAppender");
            return null;
        }
        return new GuiAppender(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        if (this.consoleArea != null) {
            this.consoleArea.append(new String(this.getLayout().toByteArray(event)));
            SwingUtilities.invokeLater(() -> this.consoleArea.setCaretPosition(this.consoleArea.getDocument().getLength()));
        }
    }
}
