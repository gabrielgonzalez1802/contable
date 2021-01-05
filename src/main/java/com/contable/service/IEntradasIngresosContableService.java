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
	List<EntradaIngresoContable> buscarPorEmpresaFecha(Empresa empresa, Date date);
	List<EntradaIngresoContable> buscarPorEmpresaFechaBetween(Empresa empresa, Date desde, Date hasta);
	List<EntradaIngresoContable> buscarPorEmpresaFechaCurrent(Empresa empresa);
	List<EntradaIngresoContable> buscarPorEmpresaCompra(Empresa empresa, Compra compra);
	List<EntradaIngresoContable> buscarPorEmpresaUsuarioNULLCuentaContableFechaCurrent(Empresa empresa, CuentaContable cuentaContable);
	List<EntradaIngresoContable> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	List<EntradaIngresoContable> buscarPorEmpresaCuentaContableFechas(Empresa empresa, CuentaContable cuentaContable, Date desde, Date hasta);
	List<EntradaIngresoContable> buscarPorEmpresaCuentasContables(Empresa empresa, List<CuentaContable> cuentasContables);
	List<EntradaIngresoContable> buscarPorEmpresaCompraCuentaContable(Empresa empresa, Compra compra, CuentaContable cuentaContable);
	List<EntradaIngresoContable> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario);
	List<EntradaIngresoContable> buscarPorEmpresaUsuarioCuentaContable(Empresa empresa, Usuario usuario, CuentaContable cuentaContable);
	List<EntradaIngresoContable> buscarPorEmpresaUsuarioFechas(Empresa empresa, Usuario usuario, Date desde, Date hasta);
	List<EntradaIngresoContable> buscarPorEmpresaBalanceContableNullASC(Empresa empresa);
	List<EntradaIngresoContable> buscarPorEmpresaBalanceContableNullDESC(Empresa empresa);
	List<EntradaIngresoContable> buscarPorEmpresaBalanceContableNotNull(Empresa empresa);
	List<EntradaIngresoContable> buscarPorEmpresaCuentaContableBalanceContableNotNull(Empresa empresa, CuentaContable cuentaContable);
	List<EntradaIngresoContable> buscarPorEmpresaCuentaContableBalanceContableNotNullMenorQueID(Empresa empresa, CuentaContable cuentaContable, Integer id);
	void guardar(EntradaIngresoContable entradaIngresoContable);
	void eliminar(EntradaIngresoContable entradaIngresoContable);
	void eliminar(List<EntradaIngresoContable> entradasIngresosContables);
}
