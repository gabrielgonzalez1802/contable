package com.contable.service;

import java.util.List;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.SuplidorCuentaContableTemp;
import com.contable.model.Usuario;

public interface ISuplidoresCuentasContablesTempService {
	SuplidorCuentaContableTemp buscarPorId(Integer id);
	List<SuplidorCuentaContableTemp> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario);
	List<SuplidorCuentaContableTemp> buscarPorEmpresaUsuarioCuentaContable(Empresa empresa, Usuario usuario, CuentaContable cuentaContable);
	List<SuplidorCuentaContableTemp> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	void guardar(SuplidorCuentaContableTemp suplidorCuentaContableTemp);
	void eliminar(SuplidorCuentaContableTemp suplidorCuentaContableTemp);
	void eliminar(List<SuplidorCuentaContableTemp> suplidoresCuentasContablesTemp);
}
