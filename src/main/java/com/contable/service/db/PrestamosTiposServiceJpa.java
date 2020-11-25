package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.contable.model.PrestamoTipo;
import com.contable.repository.PrestamosTiposRepository;
import com.contable.service.IPrestamosTiposService;

@Service
public class PrestamosTiposServiceJpa implements IPrestamosTiposService{

	@Autowired
	private PrestamosTiposRepository repo;
	
	@Override
	public PrestamoTipo buscarPorId(Integer id) {
		Optional<PrestamoTipo> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public PrestamoTipo buscarPorTipo(String tipo) {
		return repo.findByTipo(tipo);
	}

	@Override
	public List<PrestamoTipo> buscarTodos() {
		Sort sort = Sort.by("id");
		return repo.findAll(sort);
	}

	@Override
	public void guardar(PrestamoTipo prestamoTipo) {
		repo.save(prestamoTipo);
	}

	@Override
	public void eliminar(PrestamoTipo prestamoTipo) {
		repo.delete(null);
	}

}
