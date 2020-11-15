package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;

public interface CuentasContablesRepository extends JpaRepository<CuentaContable, Integer> {
	List<CuentaContable> findByEmpresaAndNombreCuenta(Empresa empresa, String nombreCuenta);
	List<CuentaContable> findByEmpresaAndCodigo(Empresa empresa, String codigo);
	List<CuentaContable> findByEmpresa(Empresa empresa);
	List<CuentaContable> findByEmpresaOrderByCodigoAsc(Empresa empresa);
}
