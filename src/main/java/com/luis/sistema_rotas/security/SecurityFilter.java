package com.luis.sistema_rotas.security;

import com.luis.sistema_rotas.domain.usuario.entity.Usuario;
import com.luis.sistema_rotas.domain.usuario.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    protected  void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null){
            var login = tokenService.validateToken(token);
            Usuario usuario = usuarioRepository.findByEmail(login).orElse(null);
            if (usuario != null){
                UserDetails user = new User(usuario.getId(), usuario.getEmail(), usuario.getSenha());
                var authentication = new UsernamePasswordAuthenticationToken(user, null , user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var cookies = request.getCookies();

        if (cookies == null){
            return null;
        }

        for (var cookie : cookies){
            if (cookie.getName().equals("token")){
                return cookie.getValue();
            }
        }

        return null;
    }
}
