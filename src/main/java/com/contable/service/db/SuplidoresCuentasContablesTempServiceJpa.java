package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.SuplidorCuentaContableTemp;
import com.contable.model.Usuario;
import com.contable.repository.SuplidoresCuentasContablesTempRepository;
import com.contable.service.ISuplidoresCuentasContablesTempService;

@Service
public class SuplidoresCuentasContablesTempServiceJpa implements ISuplidoresCuentasContablesTempService {

	@Autowired
	private SuplidoresCuentasContablesTempRepository repo;
	
	@Override
	public SuplidorCuentaContableTemp buscarPorId(Integer id) {
		Optional<SuplidorCuentaContableTemp> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<SuplidorCuentaContableTemp> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario) {
		return repo.findByEmpresaAndUsuario(empresa, usuario);
	}

	@Override
	public List<SuplidorCuentaContableTemp> buscarPorEmpresaUsuarioCuentaContable(Empresa empresa, Usuario usuario,
			CuentaContable cuentaContable) {
		return repo.findByEmpresaAndUsuarioAndCuentaContable(empresa, usuario, cuentaContable);
	}

	@Override
	public List<SuplidorCuentaContableTemp> buscarPorEmpresaCuentaContable(Empresa empresa,
			CuentaContable cuentaContable) {
		return repo.findByEmpresaAndCuentaContable(empresa, cuentaContable);
	}

	@Override
	public void guardar(SuplidorCuentaContableTemp suplidorCuentaContableTemp) {
		repo.save(suplidorCuentaContableTemp);
	}

	@Override
	public void eliminar(SuplidorCuentaContableTemp suplidorCuentaContableTemp) {
		repo.delete(suplidorCuentaContableTemp);
	}

	@Override
	public void eliminar(List<SuplidorCuentaContableTemp> suplidoresCuentasContablesTemp) {
		repo.deleteAll(suplidoresCuentasContablesTemp);
	}
	
}
