package library.config;

import library.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserEntityService userService;              // предоставляет данные о пользователе по его имени


    // настройка аутентификации (выбор менеджера и провайдеров аутентификации)
    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth

        /* Настройка некоторых опций аутентификации */

             // .authenticationEventPublisher(eventPublisher)       // устанавливает стратегию публикации событий аутентификации
             // .eraseCredentials(true)                             // нужно ли удалять предоставленные пользователем Credentials из объекта Authentication после аутентификации
             // .parentAuthenticationManager(authenticationManager) // позволяет указать другой AuthenticationManager, на случай, если основной не сможет попытаться аутентифицировать

        /* Выбор способа аутентификации */

             // .getDefaultUserDetailsService()         // option 1 - аутентификация на основе UserDetailsService, используемого по умолчанию

             // .inMemoryAuthentication()               // option 2 - аутентификация на основе данных о пользователях прописанных в памяти приложения
             //     .withUser("user")
             //     .password("password")
             //     .roles("USER")
             // .and()
             //     .withUser("admin")
             //     .password("password")
             //     .roles("ADMIN","USER");

             // .jdbcAuthentication()                   // option 3 - аутентификация на основе JDBC-соединения
             // .ldapAuthentication()                   // option 4 - аутентификация на основе LDAP-хранилица данных о пользователях
             // .userDetailsService(userService)        // option 5 - аутентификация на основе UserDetailsService
                .authenticationProvider(getAuthProvider());  // option 6 - аутентификация на основе AuthenticationProvider
    }

    // настройка авторизации (определение прав (ролей), необходимых для доступа к тем или иным url)
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // настройка защиты csrf (в данном случае - отключение)
                .csrf()
                    .disable()

                // настройка прав доступа к ресурсам (такие-то url будут доступны для таких-то ролей)
                .authorizeRequests()
                    // адреса, доступные только админам и суперадминам
                    .antMatchers("/books/**", "/admin", "/red").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")
                    // просмотр содержимого книг и голосование доступны только аутентифицированным пользователям
                    .antMatchers("/books/content", "/MainPage/Rating").authenticated()// возвращает конфигуратор ограничений доступа
                    .anyRequest().permitAll()           // ко всем остальным страницам разрешить доступ всем
                    .and()

                // обработка ошибок доступа
                .exceptionHandling()
                    .accessDeniedPage("/login")         // куда перенаправлять в случае отказа в доступе
                    .and()

                // настройка формы аутентификации
                .formLogin()                // выбрана аутентификация через заполнение html-формы
                    .loginPage("/login")                // адрес страницы входа в систему
                    .usernameParameter("username")      // имя поля для ввода имени пользователя
                    .passwordParameter("password")      // имя поля для ввода пароля
                    .defaultSuccessUrl("/")             // куда перенаправить в случае успешного входа (если пользователь никуда не пытался попасть)
                    .failureUrl("/login?error=true")    // куда перенаправить в случае неудачи
                    .permitAll()                        // разрешить всем доступ к formLogin
                    .and()

                // настройка функции rememberMe
                .rememberMe()
                    .key("superSecretKey$15976")        // секретный ключ (используется где-то в cookie)
                    .userDetailsService(userService)    // функция rememberMe требует реализации UserDetailsService
                    .tokenValiditySeconds(86400)        // время валидности ключа в секундах (1 день)
                    .rememberMeParameter("rememberMe")  // имя checkbox в html-форме, отвечающего за rememberMe
                    .and()

                // настройка выхода пользователя из системы
                .logout()
                    .logoutUrl("/logout")           // url, запускающий выход из системы
                    .logoutSuccessUrl("/")          // страница, на которую будет перенаправлен пользоваетль после выхода
                    .deleteCookies("JSESSIONID", "SPRING_SECURITY_REMEMBER_MY_COOKIE")  // удалить cookie после выхода
                    .invalidateHttpSession(true)    // удалить сессию после выхода
                    .permitAll();                   // разрешить всем доступ к странице logout
    }

    // позволяет сконфигурировать глобальные параметры безопасности
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()                 // позволяет отпределить адреса и ресурсы, которые будут выведены из под защиты Spring Security
                .antMatchers("/resources/**",    // все содержимое папки resources (html-страцицы, css-стили, картинки и пр.)
                        "/books/img",       // обложки книг из БД
                        "/errors");         // страница ошибок
    }

    // настройка своего провайдера аутентификации (хэширует пароль, предоставленный пользователем и сравнивает с хэшем, полученным из UserService)
    @Bean
    protected DaoAuthenticationProvider getAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(getPasswordEncoder());      // чем хэшировать аутентифицируемый пароль
        provider.setUserDetailsService(userService);            // как получить хэш пароля из хранилища пользователей
        return provider;
    }

    // Энкодер, используемый для шифрования (хэширования) паролей
    @Bean
    protected BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}






