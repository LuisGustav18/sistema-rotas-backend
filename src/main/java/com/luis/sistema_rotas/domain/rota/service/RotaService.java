package com.luis.sistema_rotas.domain.rota.service;

import com.luis.sistema_rotas.domain.projeto.entity.Projeto;
import com.luis.sistema_rotas.domain.projeto.service.ProjetoService;
import com.luis.sistema_rotas.domain.rota.dto.RotaDTO;
import com.luis.sistema_rotas.domain.rota.entity.Rota;
import com.luis.sistema_rotas.domain.rota.repository.RotaRepository;
import com.luis.sistema_rotas.domain.usuario.entity.Usuario;
import com.luis.sistema_rotas.exceptions.DataIntegrityViolationException;
import com.luis.sistema_rotas.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RotaService {

    @Autowired
    private RotaRepository repository;

    @Autowired
    private ProjetoService projetoService;

    public Rota findById(UUID id){
        Optional<Rota> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException("Ponto não encontrado"));
    }

    public List<Rota> findRotaByProjetoId(UUID id){
        List<Rota> list = repository.findRotaByProjetoId(id);

        if (list.isEmpty()){
            throw new ObjectNotFoundException("Pontos não marcados");
        }

        return list;
    }

    public Rota create(RotaDTO objDTO, UUID usuarioId){

        validationDuplicationSameLocation(objDTO.latitude(), objDTO.longitude());

        Rota obj = new Rota(objDTO);
        obj.setProjeto(findProjeto(objDTO.projeto()));

        validationUserIntegrityCheck(obj.getProjeto().getUsuario(), usuarioId);

        return repository.save(obj);
    }

    public Rota update(UUID id, RotaDTO objDTO, UUID usuarioId){
        Rota obj = findById(id);

        validationUserIntegrityCheck(obj.getProjeto().getUsuario(), usuarioId);

         if (!obj.getLongitude().equals(objDTO.longitude()) || !obj.getLatitude().equals(objDTO.latitude())) {
            validationDuplicationSameLocation(objDTO.latitude(), objDTO.longitude());
        }

        if (!obj.getProjeto().getId().equals(objDTO.projeto())){
            throw new DataIntegrityViolationException("Erro na atualização de rota");
        }

        obj = new Rota(objDTO);
        return repository.save(obj);
    }

    public void delete(UUID id, UUID usuarioId){
        Rota obj = findById(id);
        validationUserIntegrityCheck(obj.getProjeto().getUsuario(), usuarioId);
        repository.delete(obj);
    }

    private void validationUserIntegrityCheck(Usuario usuario, UUID usuarioId){
        if (!usuario.getId().equals(usuarioId)){
            throw new DataIntegrityViolationException("Não contem permissão nesse projeto");
        }
    }

    private void validationDuplicationSameLocation(Double latitude, Double longitude){
        if (repository.findRotaByLatitudeAndLongitude(latitude, longitude) != null){
            throw new DataIntegrityViolationException("Rota já marcada no local");
        }
    }

    private Projeto findProjeto(UUID id){
         Projeto projeto = projetoService.findById(id);
        return projeto;
    }
}
