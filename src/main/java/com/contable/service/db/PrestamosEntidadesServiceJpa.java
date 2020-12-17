package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Empresa;
import com.contable.model.Inversionista;
import com.contable.model.PrestamoEntidad;
import com.contable.repository.PrestamosEntidadesRepository;
import com.contable.service.IPrestamosEntidadesService;

@Service
public class PrestamosEntidadesServiceJpa implements IPrestamosEntidadesService{

	@Autowired
	private PrestamosEntidadesRepository repo;
	
	@Override
	public PrestamoEntidad buscarPorId(Integer id) {
		Optional<PrestamoEntidad> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<PrestamoEntidad> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}

	@Override
	public List<PrestamoEntidad> buscarPorEmpresaInversionista(Empresa empresa, Inversionista inversionista) {
		return repo.findByEmpresaAndInversionista(empresa, inversionista);
	}

	@Override
	public void guardar(PrestamoEntidad prestamoEntidad) {
		repo.save(prestamoEntidad);
	}

	@Override
	public void eliminar(PrestamoEntidad prestamoEntidad) {
		repo.delete(prestamoEntidad);
	}

}
