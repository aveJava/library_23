package library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
* Настройки локализации. По какому запросу переключать локаль, где брать локализованные сообщения и пр.
*
*
* Внимание! Работа данного класса приводит к ошибке в случае, если в приложении одновременно присутствуют
* аннотации @EnableWebMvc и @SpringBootApplication (в любом месте). Поэтому, поскольку данное приложение
* является SpringBoot приложением, аннотация @EnableWebMvc в нем не используется.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
@Configuration
public class LocalizationConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    // позволяет перехватить из http-запроса параметр, определяющий локаль, и переключить локаль (например, при параметре lang запрос ?lang=fr переключит локаль на fr)
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");   // параметр http-запроса, значение которого будет определять локаль, на которую нужно переключиться
        return lci;
    }

    // SessionLocaleResolver позволяет хранить локаль в сессии. то есть, при переключении языка, пользователю выдается куки JSESSEIONID
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        return resolver;
    }

    // настройка источника локализованных сообщений (откуда их брать)
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("locales/library");     // приложение будет искать ResourceBundles в resources/locales
        source.setDefaultEncoding("UTF-8");         // кодировка ResourceBundles
        source.setUseCodeAsDefaultMessage(true);    // при отсутствии файла нужной локали в значении true будет использовано значение из дефолтного файла
        return source;                                                                 // в значении false будет вызвано исключение NoSuchMessageException
    }

    /**
     * Настройка кодировки .property файлов в Idea:
     *
     *      В Idea кодировка для .properties файлов задается по особому и делается это на уровне проекта. Стандартное
     * Java API спроектировано для использования ISO 8859-1 для properties файлов. Чтобы использовать другие кодировки,
     * можно использовать управляющие последовательности и Unicode (виде \ uXXXX, например \ u0410 = русская А).
     * Альтернатива - определить кодировку по умолчанию для файлов .properties на уровне проекта и использовать другое
     * API, которое может читать файлы настроек в заданной кодировке.
     *
     *      Кодировка для файла настроек задается следующим образом. Открыть диалоговое окно Settings и выбрать
     * File Encodings (можно найти через поиск). Сделать одно из следующего:
     *
     *  1.  Чтобы включить специальный режим, когда символы сохраняются в файле как управляющие последовательности,
     *      но отображаются как нормальные буквы, выберите Transparent native-to-ascii conversion. Эта опция полезна
     *      когда файлы настройки закодированы в ISO 8859-1. Рекомендуется использовать этот способ, если у вас нет
     *      особых причин менять кодировку.
     *
     *  2.  В поле Default encoding for properties files, выберите кодировку, которую вы хотите использовать для
     *      всех файлов настройки в проекте.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
}
