package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Empresa;
import com.contable.repository.EmpresasRepository;
import com.contable.service.IEmpresasService;

@Service
public class EmpresasServiceJpa implements IEmpresasService{
	
	@Autowired
	private EmpresasRepository repo;

	@Override
	public Empresa buscarPorId(Integer id) {
		Optional<Empresa> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Empresa> buscarTodas() {
		return repo.findAll();
	}

	@Override
	public void guardar(Empresa empresa) {
		repo.save(empresa);
	}

	@Override
	public void eliminar(Empresa empresa) {
		repo.delete(empresa);
	}

}
