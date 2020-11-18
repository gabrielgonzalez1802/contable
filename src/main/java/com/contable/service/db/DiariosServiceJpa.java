package com.contable.service.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Diario;
import com.contable.repository.DiariosRepository;
import com.contable.service.IDiariosService;

@Service
public class DiariosServiceJpa implements IDiariosService{
	
	@Autowired
	private DiariosRepository repo;

	@Override
	public Diario buscarPorId(Integer id) {
		Optional<Diario> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Diario> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public List<Diario> buscarPorFechaBetween(Date desde, Date hasta) {
		return repo.findByFechaBetween(desde, hasta);
	}

	@Override
	public void guardar(Diario diario) {
		repo.save(diario);
	}

	@Override
	public void eliminar(Diario diario) {
		repo.delete(diario);
	}

}
