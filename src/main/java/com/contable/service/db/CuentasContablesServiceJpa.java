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
	public List<CuentaContable> buscarPorEmpresaOrderByCodigoDesc(Empresa empresa) {
		return repo.findByEmpresaOrderByCodigoDesc(empresa);
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

	@Override
	public List<CuentaContable> buscarPorEmpresaTipoEstado(Empresa empresa, String tipo, Integer estado) {
		return repo.findByEmpresaAndTipoAndEstado(empresa, tipo, estado);
	}

	@Override
	public List<CuentaContable> buscarPorEmpresaTipoCuentaControl(Empresa empresa, String tipo, String cuentaControl) {
		return repo.findByEmpresaAndTipoAndCuentaControl(empresa, tipo, cuentaControl);
	}

	@Override
	public List<CuentaContable> buscarPorEmpresaTipo(Empresa empresa, String tipo) {
		return repo.findByEmpresaAndTipo(empresa, tipo);
	}

	@Override
	public List<CuentaContable> buscarPorEmpresaTipoOrderByCodigo(Empresa empresa, String tipo) {
		return repo.findByEmpresaAndTipoOrderByCodigoAsc(empresa, tipo);
	}

	@Override
	public List<CuentaContable> buscarPorEmpresaCuentaControl(Empresa empresa, String cuentaControl) {
		return repo.findByEmpresaAndCuentaControl(empresa, cuentaControl);
	}

	@Override
	public List<CuentaContable> buscarPorEmpresaIdCuentaControl(Empresa empresa, Integer idCuentaControl) {
		return repo.findByEmpresaAndIdCuentaControl(empresa, idCuentaControl);
	}

	@Override
	public List<CuentaContable> buscarPorEmpresaOrderByCodigoAsc(Empresa empresa) {
		return repo.findByEmpresaOrderByCodigoAsc(empresa);
	}

}
