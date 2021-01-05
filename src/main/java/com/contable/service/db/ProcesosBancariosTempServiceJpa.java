package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Empresa;
import com.contable.model.ProcesoBancarioTemp;
import com.contable.model.Usuario;
import com.contable.repository.ProcesosBancariosTempRepository;
import com.contable.service.IProcesosBancariosTempService;

@Service
public class ProcesosBancariosTempServiceJpa implements IProcesosBancariosTempService {

	@Autowired
	private ProcesosBancariosTempRepository repo;
	
	@Override
	public ProcesoBancarioTemp buscarPorId(Integer id) {
		Optional<ProcesoBancarioTemp> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	@Override
	public List<ProcesoBancarioTemp> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario) {
		return repo.findByEmpresaAndUsuario(empresa, usuario);
	}

	@Override
	public void guardar(ProcesoBancarioTemp procesoBancarioTemp) {
		repo.save(procesoBancarioTemp);
	}

	@Override
	public void eliminar(List<ProcesoBancarioTemp> procesosBancariosTemp) {
		repo.deleteAll(procesosBancariosTemp);
	}

	@Override
	public void eliminar(ProcesoBancarioTemp procesoBancarioTemp) {
		repo.delete(procesoBancarioTemp);
	}
}
