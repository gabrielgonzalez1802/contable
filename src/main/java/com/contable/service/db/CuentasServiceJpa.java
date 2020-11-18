package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Carpeta;
import com.contable.model.Cuenta;
import com.contable.model.Empresa;
import com.contable.repository.CuentasRepository;
import com.contable.service.ICuentasService;

@Service
public class CuentasServiceJpa implements ICuentasService{
	
	@Autowired
	private CuentasRepository repo;

	@Override
	public Cuenta buscarPorId(Integer id) {
		Optional<Cuenta> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Cuenta> buscarPorCarpeta(Carpeta carpeta) {
		return repo.findByCarpeta(carpeta);
	}

	@Override
	public void guardar(Cuenta cuenta) {
		repo.save(cuenta);
	}

	@Override
	public void eliminar(Cuenta cuenta) {
		repo.delete(cuenta);
	}

	@Override
	public List<Cuenta> buscarPorCarpetaEmpresa(Carpeta carpeta, Empresa empresa) {
		return repo.findByCarpetaAndEmpresa(carpeta, empresa);
	}

}
