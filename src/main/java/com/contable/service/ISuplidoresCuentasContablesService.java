package com.contable.service;

import java.util.List;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.Suplidor;
import com.contable.model.SuplidorCuentaContable;
import com.contable.model.Usuario;

public interface ISuplidoresCuentasContablesService {
	SuplidorCuentaContable buscarPorId(Integer id);
	List<SuplidorCuentaContable> buscarPorEmpresaSuplidor(Empresa empresa, Suplidor suplidor);
	List<SuplidorCuentaContable> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario);
	List<SuplidorCuentaContable> buscarPorEmpresaUsuarioCuentaContable(Empresa empresa, Usuario usuario, CuentaContable cuentaContable);
	List<SuplidorCuentaContable> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	void guardar(SuplidorCuentaContable suplidorCuentaContable);
	void eliminar(SuplidorCuentaContable suplidorCuentaContable);
}
