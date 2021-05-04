package library.controllers.security;

import library.domain.UserEntity;
import library.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegController {
    @Autowired
    private UserEntityService userService;

    @GetMapping("/registration")
    public String getRegistrationForm(Model model) {
        UserEntity user = new UserEntity();
        user.setFormStage("username");   // стадия выбора имени
        model.addAttribute("user", user);

        return "login/registration";
    }

    @PostMapping("/registration")
    public String register(@ModelAttribute("user") @Valid UserEntity user,
                           BindingResult bindingResult, Model model) {

        String stage = user.getFormStage();         // текущая стадия заполнения формы
        boolean hasError = false;
        String errorMsg = null;

        if (stage.equals("username")) {             // стадия выбора имени
            // проверка имени на ошибки
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                hasError = true;
                errorMsg = "Введите имя пользователя";
            } else
            if (userService.isPresentEnableUsersWithUsername(user.getUsername())){
                hasError = true;
                errorMsg = "Это имя уже используется";
            }

            // если имя подходит, переходить к выбору пароля
            if (!hasError) {
                user.setFormStage("password");
                return "login/registration";
            }
        }

        if (stage.equals("password")) {             // стадия выбора пароля

            String password = user.getPassword();
            String passConfirm = user.getPasswordConfirm();
            String regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$";
            boolean isInvalidPassword = password == null || !password.matches(regexp);

            // проверка пароля на ошибки
            if (password == null  || password.isEmpty()) {
                hasError = true;
                errorMsg = "Введите пароль";
            } else
            if (isInvalidPassword) {
                hasError = true;
                errorMsg = "Пароль должен содержать от 8 до 20 символов (цифры и латинские буквы), включая как минимум одну строчную букву, одну заглавную букву и одну цифру";
            } else
            if (passConfirm == null || passConfirm.isEmpty()) {
                hasError = true;
                errorMsg = "Заполните поле подтверждения пароля";
                model.addAttribute("displayPasswordValue", true);
            } else
            if (!password.equals(passConfirm)) {
                hasError = true;
                errorMsg = "Пароли не совпадают, попробуйте еще раз";
            }

            // если пароль подходит, сохранить нового пользователя в БД
            if (!hasError) {
                userService.save(user);

                // выход из используемой учетной записи (если пользователь уже аутентифицирован)
                SecurityContextHolder.getContext().setAuthentication(null);
                // аутентификация созданной учетной записи
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // если была обнаружена ошибка, перенаправить снова на форму регистрации и отобразить сообщение об ошибке
        if (hasError) {
            model.addAttribute("user", user);
            model.addAttribute("hasErrors", true);
            model.addAttribute("errorMsg", errorMsg);

            return "login/registration";
        }

        return "redirect:/";
    }
}
