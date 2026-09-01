package com.luis.sistema_rotas.domain.usuario.resource;

import com.luis.sistema_rotas.domain.usuario.dto.UsuarioDTO;
import com.luis.sistema_rotas.domain.usuario.entity.Usuario;
import com.luis.sistema_rotas.domain.usuario.service.UsuarioService;
import com.luis.sistema_rotas.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "usuarios")
public class UsuarioResource {

    @Autowired
    private UsuarioService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable UUID id){
        Usuario obj = service.findById(id);
        return ResponseEntity.ok()
                .body(new UsuarioDTO(obj));
    }

    @GetMapping()
    public ResponseEntity<UsuarioDTO> findById(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        Usuario obj = service.findById(user.getId());
        return ResponseEntity.ok().body(new UsuarioDTO(obj));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> create(@RequestBody UsuarioDTO objDTO){
        Usuario obj = service.create(objDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getId())
                .toUri();
        return ResponseEntity.created(uri)
                .build();
    }


    @PutMapping(value = "/{id}")
    public ResponseEntity<UsuarioDTO> update(@PathVariable UUID id, @RequestBody UsuarioDTO objDTO){
        Usuario obj = service.update(id, objDTO);
        return ResponseEntity.ok()
                .body(new UsuarioDTO(obj));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        service.delete(id);
        return ResponseEntity.noContent()
                .build();
    }
}
