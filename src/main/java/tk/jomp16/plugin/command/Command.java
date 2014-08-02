package tk.jomp16.plugin.command;

import tk.jomp16.plugin.level.Level;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    String value();

    String[] optCommands() default "";

    String[] args() default "placeholder";

    Level level() default Level.NORMAL;
}
