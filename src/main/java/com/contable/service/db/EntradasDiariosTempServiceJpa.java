package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiarioTemp;
import com.contable.model.Usuario;
import com.contable.repository.EntradasDiariosTempRepository;
import com.contable.service.IEntradasDiariosTempService;

@Service
public class EntradasDiariosTempServiceJpa implements IEntradasDiariosTempService{

	@Autowired
	private EntradasDiariosTempRepository repo;
	
	@Override
	public EntradaDiarioTemp buscarPorId(Integer id) {
		Optional<EntradaDiarioTemp> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<EntradaDiarioTemp> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable) {
		return repo.findByEmpresaAndCuentaContable(empresa, cuentaContable);
	}

	@Override
	public List<EntradaDiarioTemp> buscarPorCuentaContable(CuentaContable cuentaContable) {
		return repo.findByCuentaContable(cuentaContable);
	}

	@Override
	public List<EntradaDiarioTemp> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario) {
		return repo.findByEmpresaAndUsuario(empresa, usuario);
	}

	@Override
	public void guardar(EntradaDiarioTemp entradaDiarioTemp) {
		repo.save(entradaDiarioTemp);
	}

	@Override
	public void eliminar(EntradaDiarioTemp entradaDiarioTemp) {
		repo.delete(entradaDiarioTemp);
	}

	@Override
	public void eliminar(List<EntradaDiarioTemp> entradasDiariosTemp) {
		repo.deleteAll(entradasDiariosTemp);
	}

}
