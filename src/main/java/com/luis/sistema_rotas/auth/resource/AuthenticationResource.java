package com.luis.sistema_rotas.auth.resource;

import com.luis.sistema_rotas.auth.dto.AuthenticationDTO;
import com.luis.sistema_rotas.auth.dto.MeDTO;
import com.luis.sistema_rotas.security.TokenService;
import com.luis.sistema_rotas.security.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping(value = "auth")
public class AuthenticationResource {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping(value = "/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){

        var usernamePassword = new UsernamePasswordAuthenticationToken(
                data.email(),
                data.senha()
        );

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        ResponseCookie cookie = ResponseCookie
                .from("token", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<MeDTO> me(Authentication authentication){
        User user = (User) authentication.getPrincipal();

        MeDTO me = new MeDTO(
                user.getId(),
                user.getPassword()
        );

        return ResponseEntity.ok(me);
    }
}
