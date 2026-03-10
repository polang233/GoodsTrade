package cc.sbsj.polang.goodstrade.commands.main;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.TYPE)
public @interface SubCommandAnnotation {
    String name();
}
