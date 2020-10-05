package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Carpeta;
import com.contable.repository.CarpetasRepository;
import com.contable.service.ICarpetasService;

@Service
public class CarpetasServiceJpa implements ICarpetasService{
	
	@Autowired
	private CarpetasRepository repo;

	@Override
	public Carpeta buscarPorId(Integer id) {
		Optional<Carpeta> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public Carpeta buscarPorNombre(String nombre) {
		return repo.findByNombre(nombre);
	}

	@Override
	public List<Carpeta> buscarTipoCarpeta(Integer id) {
		return repo.findByPrincipal(id);
	}

	@Override
	public void guardar(Carpeta carpeta) {
		repo.save(carpeta);
	}

	@Override
	public void eliminar(Carpeta carpeta) {
		repo.delete(carpeta);
	}

}
