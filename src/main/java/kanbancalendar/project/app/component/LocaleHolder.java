package kanbancalendar.project.app.component;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Locale;

@Component
@SessionScope
@Data
public class LocaleHolder {

    private Locale locale;

}
