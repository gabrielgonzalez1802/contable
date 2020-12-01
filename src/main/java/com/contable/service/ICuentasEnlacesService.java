package com.contable.service;

import java.util.List;

import com.contable.model.CuentaContable;
import com.contable.model.CuentaEnlace;
import com.contable.model.Empresa;

public interface ICuentasEnlacesService {
	CuentaEnlace buscarPorId(Integer id);
	List<CuentaEnlace> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	List<CuentaEnlace> buscarPorEmpresa(Empresa empresa);
	CuentaEnlace buscarPorEmpresaTipoSeccionReferencia(Empresa empresa, String tipo, String seccion, String referencia);
	void guardar(CuentaEnlace cuentaEnlace);
	void eliminar(CuentaEnlace cuentaEnlace);
}
