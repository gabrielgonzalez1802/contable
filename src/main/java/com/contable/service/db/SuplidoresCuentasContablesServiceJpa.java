package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.Suplidor;
import com.contable.model.SuplidorCuentaContable;
import com.contable.model.Usuario;
import com.contable.repository.SuplidoresCuentasContablesRepository;
import com.contable.service.ISuplidoresCuentasContablesService;

@Service
public class SuplidoresCuentasContablesServiceJpa implements ISuplidoresCuentasContablesService {

	@Autowired
	private SuplidoresCuentasContablesRepository repo;
	
	@Override
	public SuplidorCuentaContable buscarPorId(Integer id) {
		Optional<SuplidorCuentaContable> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<SuplidorCuentaContable> buscarPorEmpresaSuplidor(Empresa empresa, Suplidor suplidor) {
		return repo.findByEmpresaAndSuplidor(empresa, suplidor);
	}

	@Override
	public List<SuplidorCuentaContable> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario) {
		return repo.findByEmpresaAndUsuario(empresa, usuario);
	}

	@Override
	public List<SuplidorCuentaContable> buscarPorEmpresaUsuarioCuentaContable(Empresa empresa, Usuario usuario,
			CuentaContable cuentaContable) {
		return repo.findByEmpresaAndUsuarioAndCuentaContable(empresa, usuario, cuentaContable);
	}

	@Override
	public List<SuplidorCuentaContable> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable) {
		return repo.findByEmpresaAndCuentaContable(empresa, cuentaContable);
	}

	@Override
	public void guardar(SuplidorCuentaContable suplidorCuentaContable) {
		repo.save(suplidorCuentaContable);
	}

	@Override
	public void eliminar(SuplidorCuentaContable suplidorCuentaContable) {
		repo.delete(suplidorCuentaContable);
	}

	
}
