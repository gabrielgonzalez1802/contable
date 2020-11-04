package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Perfil;
import com.contable.repository.PerfilesRepository;
import com.contable.service.IPerfilesService;

@Service
public class PerfilesServiceJpa implements IPerfilesService{
	
	@Autowired
	private PerfilesRepository repo;

	@Override
	public Perfil buscarPorId(Integer id) {
		Optional<Perfil> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Perfil buscarPorPerfil(String perfil) {
		return repo.findByPerfil(perfil);
	}

	@Override
	public void guardar(Perfil perfil) {
		repo.save(perfil);
	}

	@Override
	public void elminar(Integer id) {
		Optional<Perfil> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public List<Perfil> buscarTodos() {
		return repo.findAll();
	}

}
