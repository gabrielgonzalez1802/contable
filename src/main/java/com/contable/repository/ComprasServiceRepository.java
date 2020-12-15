package com.contable.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Compra;
import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.Suplidor;

public interface ComprasServiceRepository extends JpaRepository<Compra, Integer> {
	List<Compra> findByEmpresa(Empresa empresa);
	List<Compra> findByEmpresaAndBalanceGreaterThan(Empresa empresa, double valor);
	List<Compra> findByEmpresaAndCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	List<Compra> findByEmpresaAndCuentaContableAndSuplidor(Empresa empresa, CuentaContable cuentaContable, Suplidor suplidor);
	List<Compra> findByEmpresaAndFechaBetween(Empresa empresa, Date desde, Date hasta);
	List<Compra> findByEmpresaAndCuentaContableAndFechaBetween(Empresa empresa, CuentaContable cuentaContable, Date desde, Date hasta);
}
