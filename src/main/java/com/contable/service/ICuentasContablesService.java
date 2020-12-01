package com.contable.service;

import java.util.List;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;

public interface ICuentasContablesService {
	CuentaContable buscarPorId(Integer id);
	List<CuentaContable> buscarPorEmpresaCodigo(Empresa empresa, String codigo);
	List<CuentaContable> buscarPorEmpresaTipo(Empresa empresa, String tipo);
	List<CuentaContable> buscarPorEmpresaTipoOrderByCodigo(Empresa empresa, String tipo);
	List<CuentaContable> buscarPorEmpresaOrderByCodigoDesc(Empresa empresa);
	List<CuentaContable> buscarPorEmpresaOrderByCodigoAsc(Empresa empresa);
	List<CuentaContable> buscarPorEmpresaIdCuentaControl(Empresa empresa, Integer idCuentaControl);
	List<CuentaContable> buscarPorEmpresaNombreCuenta(Empresa empresa, String nombreCuenta);
	List<CuentaContable> buscarPorEmpresaTipoEstado(Empresa empresa, String tipo, Integer estado);
	List<CuentaContable> buscarPorEmpresaTipoAndContieneCodigo(Empresa attribute, String tipo, String valor);
	List<CuentaContable> buscarPorEmpresaTipoEstadoAndContieneCodigo(Empresa empresa, String tipo, Integer estado, String codigo);
	List<CuentaContable> buscarPorEmpresaCuentaControl(Empresa empresa, String cuentaControl);
	List<CuentaContable> buscarPorEmpresaTipoCuentaControl(Empresa empresa, String tipo, String cuentaControl);
	void guardar(CuentaContable cuentaContable);
	void eliminar(CuentaContable cuentaContable);
}
