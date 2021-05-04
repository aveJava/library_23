package library.controllers.security;

import library.domain.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    private String username;
    private String password;

    // получить аутентифицированного пользователя текущей сессии
    public UserEntity getCurrentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = principal instanceof UserEntity ? (UserEntity) principal : null;
        return user;
    }

    // имеется ли у аутентифицированного пользователя текущей сессии указанная роль (без префикса ROLE_)
    public boolean hasRole(String role){
        return getCurrentUser().getAuthorities()
                .stream()
                .map(x -> x.getAuthority())
                .anyMatch(x -> x.equals("ROLE_" + role));

    }

}
