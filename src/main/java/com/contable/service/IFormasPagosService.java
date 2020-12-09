package com.contable.service;

import java.util.List;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.FormaPago;

public interface IFormasPagosService {
	FormaPago buscarPorId(Integer id);
	List<FormaPago> buscarPorEmpresa(Empresa empresa);
	List<FormaPago> buscarPorCuentaContable(CuentaContable cuentaContable);
	List<FormaPago> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	void guardar(FormaPago formaPago);
	void eliminar(FormaPago formaPago);
}