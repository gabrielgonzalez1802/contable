package com.contable.service.db;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.GrupoCuenta;
import com.contable.repository.GruposCuentasRepository;
import com.contable.service.IGruposCuentasService;

@Service
public class GruposCuentasServiceJpa implements IGruposCuentasService {

	@Autowired
	private GruposCuentasRepository repo;
	
	@Override
	public GrupoCuenta buscarPorId(Integer id) {
		Optional<GrupoCuenta> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public GrupoCuenta buscarPorTipo(String tipo) {
		return repo.findByTipo(tipo);
	}

	@Override
	public GrupoCuenta buscarPorCodigo(Integer id) {
		return repo.findByCodigo(id);
	}

	@Override
	public void guardar(GrupoCuenta grupoCuenta) {
		repo.save(grupoCuenta);
	}

	@Override
	public void eliminar(GrupoCuenta grupoCuenta) {
		repo.delete(grupoCuenta);
	}

}
