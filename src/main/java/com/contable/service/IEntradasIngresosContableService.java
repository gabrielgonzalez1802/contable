package com.contable.service;

import java.util.Date;
import java.util.List;

import com.contable.model.Compra;
import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.EntradaIngresoContable;
import com.contable.model.Usuario;

public interface IEntradasIngresosContableService {
	EntradaIngresoContable buscarPorId(Integer id);
	List<EntradaIngresoContable> buscarPorEmpresa(Empresa empresa);
	List<EntradaIngresoContable> buscarPorEmpresaCompra(Empresa empresa, Compra compra);
	List<EntradaIngresoContable> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	List<EntradaIngresoContable> buscarPorEmpresaCuentasContables(Empresa empresa, List<CuentaContable> cuentasContables);
	List<EntradaIngresoContable> buscarPorEmpresaCompraCuentaContable(Empresa empresa, Compra compra, CuentaContable cuentaContable);
	List<EntradaIngresoContable> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario);
	List<EntradaIngresoContable> buscarPorEmpresaUsuarioCuentaContable(Empresa empresa, Usuario usuario, CuentaContable cuentaContable);
	List<EntradaIngresoContable> buscarPorEmpresaUsuarioFechas(Empresa empresa, Usuario usuario, Date desde, Date hasta);
	void guardar(EntradaIngresoContable entradaIngresoContable);
	void eliminar(EntradaIngresoContable entradaIngresoContable);
	void eliminar(List<EntradaIngresoContable> entradasIngresosContables);
}
