package me.elia.lpdeck.gui;

import lombok.Setter;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import javax.swing.*;
import java.io.Serializable;

@Setter
@Plugin(
        name = "ConsoleAppender",
        category = Node.CATEGORY,
        elementType = Appender.ELEMENT_TYPE
)
public class ConsoleAppender extends AbstractAppender {
    private JTextArea console;

    public ConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties, JTextArea console) {
        super(name, filter, layout, ignoreExceptions, properties);
        this.console = console;
    }

    @PluginFactory
    public static ConsoleAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter
    ) {
        if (name == null) {
            LOGGER.error("No name provided for ConsoleAppender");
            return null;
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new ConsoleAppender(name, filter, layout, true, new Property[0], null);
    }

    @Override
    public void append(LogEvent event) {
        if (this.console != null) {
            this.console.append(new String(this.getLayout().toByteArray(event)));
        }
    }
}
