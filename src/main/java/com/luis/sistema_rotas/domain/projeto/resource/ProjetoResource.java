package com.luis.sistema_rotas.domain.projeto.resource;

import com.luis.sistema_rotas.domain.projeto.dto.ProjetoDTO;
import com.luis.sistema_rotas.domain.projeto.entity.Projeto;
import com.luis.sistema_rotas.domain.projeto.service.ProjetoService;
import com.luis.sistema_rotas.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "projetos")
public class ProjetoResource {

    @Autowired
    private ProjetoService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProjetoDTO> findById(@PathVariable UUID id){
        Projeto obj = service.findById(id);
        return ResponseEntity.ok().body(new ProjetoDTO(obj));
    }

    @GetMapping(value = "usuario/{id}")
    public ResponseEntity<List<ProjetoDTO>> findProjetoByUsuario(@PathVariable UUID id){
        List<Projeto> list = service.findProjetoByUsuario(id);
        List<ProjetoDTO> listDTO = list.stream().map(ProjetoDTO::new).toList();
        return ResponseEntity.ok()
                .body(listDTO);
    }

    @GetMapping(value = "usuario")
    public ResponseEntity<List<ProjetoDTO>> findProjetoByUsuario(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        List<Projeto> list = service.findProjetoByUsuario(user.getId());
        List<ProjetoDTO> listDTO = list.stream().map(ProjetoDTO::new).toList();
        return ResponseEntity.ok()
                .body(listDTO);
    }

    @PostMapping
    public ResponseEntity<ProjetoDTO> create(@RequestBody ProjetoDTO objDTO){
        Projeto obj = service.create(objDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProjetoDTO> update(@PathVariable UUID id, @RequestBody ProjetoDTO objDTO){
        Projeto obj = service.update(id, objDTO);
        return ResponseEntity.ok().body(new ProjetoDTO(obj));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
