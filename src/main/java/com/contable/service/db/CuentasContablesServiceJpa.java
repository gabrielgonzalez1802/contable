package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.repository.CuentasContablesRepository;
import com.contable.service.ICuentasContablesService;

@Service
public class CuentasContablesServiceJpa implements ICuentasContablesService{
	
	@Autowired
	private CuentasContablesRepository repo;

	@Override
	public CuentaContable buscarPorId(Integer id) {
		Optional<CuentaContable> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<CuentaContable> buscarPorEmpresaOrderByCodigo(Empresa empresa) {
		return repo.findByEmpresaOrderByCodigoAsc(empresa);
	}

	@Override
	public void guardar(CuentaContable cuentaContable) {
		repo.save(cuentaContable);
	}

	@Override
	public void eliminar(CuentaContable cuentaContable) {
		repo.delete(cuentaContable);
	}

	@Override
	public List<CuentaContable> buscarPorEmpresaCodigo(Empresa empresa, String codigo) {
		return repo.findByEmpresaAndCodigo(empresa, codigo);
	}

	@Override
	public List<CuentaContable> buscarPorEmpresaNombreCuenta(Empresa empresa, String nombreCuenta) {
		return repo.findByEmpresaAndNombreCuenta(empresa, nombreCuenta);
	}

}
