package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Empresa;
import com.contable.model.Suplidor;
import com.contable.model.Usuario;
import com.contable.repository.SuplidoresRepository;
import com.contable.service.ISuplidoresService;

@Service
public class SuplidoresServiceJpa implements ISuplidoresService{
	
	@Autowired
	private SuplidoresRepository repo;

	@Override
	public Suplidor buscarPorId(Integer id) {
		Optional<Suplidor> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Suplidor> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}

	@Override
	public List<Suplidor> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario) {
		return repo.findByEmpresaAndUsuario(empresa, usuario);
	}

	@Override
	public void guardar(Suplidor suplidor) {
		repo.save(suplidor);
	}

	@Override
	public void eliminar(Suplidor suplidor) {
		repo.delete(suplidor);
	}

}
