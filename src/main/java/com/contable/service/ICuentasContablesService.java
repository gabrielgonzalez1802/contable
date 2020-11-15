package com.contable.service;

import java.util.List;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;

public interface ICuentasContablesService {
	CuentaContable buscarPorId(Integer id);
	List<CuentaContable> buscarPorEmpresaCodigo(Empresa empresa, String codigo);
	List<CuentaContable> buscarPorEmpresaOrderByCodigo(Empresa empresa);
	List<CuentaContable> buscarPorEmpresaNombreCuenta(Empresa empresa, String nombreCuenta);
	void guardar(CuentaContable cuentaContable);
	void eliminar(CuentaContable cuentaContable);
}
