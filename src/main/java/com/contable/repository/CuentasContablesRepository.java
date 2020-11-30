package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;

public interface CuentasContablesRepository extends JpaRepository<CuentaContable, Integer> {
	List<CuentaContable> findByEmpresaAndNombreCuenta(Empresa empresa, String nombreCuenta);
	List<CuentaContable> findByEmpresaAndCodigo(Empresa empresa, String codigo);
	List<CuentaContable> findByEmpresa(Empresa empresa);
	List<CuentaContable> findByEmpresaAndTipo(Empresa empresa, String tipo);
	List<CuentaContable> findByEmpresaOrderByCodigoAsc(Empresa empresa);
	List<CuentaContable> findByEmpresaOrderByCodigoDesc(Empresa empresa);
	List<CuentaContable> findByEmpresaAndTipoOrderByCodigoAsc(Empresa empresa, String tipo);
	List<CuentaContable> findByEmpresaAndTipoAndEstado(Empresa empresa, String tipo, Integer estado);
	List<CuentaContable> findByEmpresaAndTipoAndCuentaControl(Empresa empresa, String tipo, String cuentaControl);
	List<CuentaContable> findByEmpresaAndCuentaControl(Empresa empresa, String cuentaControl);
	List<CuentaContable> findByEmpresaAndIdCuentaControl(Empresa empresa, Integer idCuentaControl);
	List<CuentaContable> findByEmpresaAndTipoAndEstadoAndCodigoContaining(Empresa empresa, String tipo, Integer estado, String codigo);
}
