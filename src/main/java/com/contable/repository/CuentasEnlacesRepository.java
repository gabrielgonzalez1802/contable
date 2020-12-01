package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.CuentaContable;
import com.contable.model.CuentaEnlace;
import com.contable.model.Empresa;

public interface CuentasEnlacesRepository extends JpaRepository<CuentaEnlace, Integer> {
	List<CuentaEnlace> findByEmpresa(Empresa empresa);
	List<CuentaEnlace> findByEmpresaAndCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	CuentaEnlace findByEmpresaAndTipoAndSeccionAndReferencia(Empresa empresa, String tipo, String seccion, String referencia);
}
