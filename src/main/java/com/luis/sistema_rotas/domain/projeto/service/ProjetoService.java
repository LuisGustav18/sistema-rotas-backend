package com.luis.sistema_rotas.domain.projeto.service;

import com.luis.sistema_rotas.domain.projeto.dto.ProjetoDTO;
import com.luis.sistema_rotas.domain.projeto.entity.Projeto;
import com.luis.sistema_rotas.domain.projeto.repository.ProjetoRepository;
import com.luis.sistema_rotas.domain.usuario.entity.Usuario;
import com.luis.sistema_rotas.domain.usuario.service.UsuarioService;
import com.luis.sistema_rotas.exceptions.DataIntegrityViolationException;
import com.luis.sistema_rotas.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository repository;

    @Autowired
    private UsuarioService usuarioService;

    public Projeto findById(UUID id){
        Optional<Projeto> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("Projeto não encontrado"));
    }

    public List<Projeto> findProjetoByUsuario(UUID id){
        return repository.findProjetoByUsuarioId(id);
    }

    public Projeto create(ProjetoDTO objDTO, UUID usuarioId){
        Projeto obj = new Projeto(objDTO);
        obj.setUsuario(findUsuario(usuarioId));
        return repository.save(obj);
    }

    public Projeto update(UUID id, ProjetoDTO objDTO){
        Projeto obj = findById(id);
        validationUserAccess(obj.getUsuario().getId(), objDTO.usuario());
        obj.setTitulo(objDTO.titulo());
        obj.setData(objDTO.data());
        return repository.save(obj);
    }

    public void delete(UUID id, UUID usuarioId){
        Projeto obj = findById(id);

        validationUserAccess(id, usuarioId);

        repository.delete(obj);
    }

    private void validationUserAccess(UUID id, UUID usuarioId){
        if (!id.equals(usuarioId)){
            throw new DataIntegrityViolationException("Sem autorização sobre projeto");
        }
    }

    private Usuario findUsuario(UUID id){
        return usuarioService.findById(id);
    }
}

