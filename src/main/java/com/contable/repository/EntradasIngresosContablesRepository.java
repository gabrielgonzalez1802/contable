package com.contable.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Compra;
import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.EntradaIngresoContable;
import com.contable.model.Usuario;

public interface EntradasIngresosContablesRepository extends JpaRepository<EntradaIngresoContable, Integer> {
	List<EntradaIngresoContable> findByEmpresa(Empresa empresa);
	List<EntradaIngresoContable> findByEmpresaAndCompra(Empresa empresa,Compra compra);
	List<EntradaIngresoContable> findByEmpresaAndCompraAndCuentaContable(Empresa empresa, Compra compra,
			CuentaContable cuentaContable);
	List<EntradaIngresoContable> findByEmpresaAndCuentaContableIn(Empresa empresa, List<CuentaContable> cuentasContables);
	List<EntradaIngresoContable> findByEmpresaAndCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	List<EntradaIngresoContable> findByEmpresaAndUsuario(Empresa empresa, Usuario usuario);
	List<EntradaIngresoContable> findByEmpresaAndUsuarioAndCuentaContable(Empresa empresa, Usuario usuario, CuentaContable cuentaContable);
	List<EntradaIngresoContable> findByEmpresaAndUsuarioAndFechaBetween(Empresa empresa, Usuario usuario, Date desde, Date hasta);
	List<EntradaIngresoContable> findByEmpresaAndBalanceContableIsNotNullOrderByIdDesc(Empresa empresa);
	List<EntradaIngresoContable> findByEmpresaAndCuentaContableAndBalanceContableIsNotNullOrderByIdDesc(Empresa empresa, CuentaContable cuentaContable);
	List<EntradaIngresoContable> findByEmpresaAndBalanceContableIsNullOrderByIdAsc(Empresa empresa);
	List<EntradaIngresoContable> findByEmpresaAndBalanceContableIsNullOrderByIdDesc(Empresa empresa);
	List<EntradaIngresoContable> findByEmpresaAndCuentaContableAndBalanceContableIsNotNullAndIdLessThanOrderByIdDesc(Empresa empresa,
			CuentaContable cuentaContable, Integer id);
}
