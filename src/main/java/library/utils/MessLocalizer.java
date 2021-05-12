package library.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessLocalizer {

    private static MessageSource messageSource;

    @Autowired
    public MessLocalizer(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public static String get(String mess) {
        Locale locale = LocaleContextHolder.getLocale();
        String localizMess = messageSource.getMessage(mess, null, locale);
        return localizMess;
    }

}
