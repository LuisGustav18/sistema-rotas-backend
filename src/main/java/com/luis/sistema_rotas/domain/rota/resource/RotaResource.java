package com.luis.sistema_rotas.domain.rota.resource;

import com.luis.sistema_rotas.domain.rota.dto.RotaDTO;
import com.luis.sistema_rotas.domain.rota.entity.Rota;
import com.luis.sistema_rotas.domain.rota.service.RotaService;
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
@RequestMapping(value = "rotas")
public class RotaResource {

    @Autowired
    private RotaService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<RotaDTO> findById(@PathVariable UUID id){
        Rota obj = service.findById(id);
        return ResponseEntity.ok()
                .body(new RotaDTO(obj));
    }

    @GetMapping(value = "projeto/{id}")
    public ResponseEntity<List<RotaDTO>> findRotaByProjetoId(@PathVariable UUID id){
        List<Rota> list = service.findRotaByProjetoId(id);
        List<RotaDTO>  listDTO = list.stream().map(RotaDTO::new).toList();
        return ResponseEntity.ok()
                .body(listDTO);
    }

    @PostMapping
    public ResponseEntity<RotaDTO> create(@RequestBody RotaDTO objDTO, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        Rota obj = service.create(objDTO, user.getId());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getId())
                .toUri();
        return ResponseEntity.created(uri)
                .build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<RotaDTO> update(@PathVariable UUID id, @RequestBody RotaDTO objDTO, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        Rota obj = service.update(id, objDTO, user.getId());
        return ResponseEntity.ok()
                .body(new RotaDTO(obj));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        service.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
