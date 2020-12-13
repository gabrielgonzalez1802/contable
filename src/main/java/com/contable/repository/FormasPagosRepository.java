package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.FormaPago;

public interface FormasPagosRepository extends JpaRepository<FormaPago, Integer> {
	List<FormaPago> findByEmpresa(Empresa empresa);
	List<FormaPago> findByEmpresaAndIdentificador(Empresa empresa, String identificador);
	List<FormaPago> findByCuentaContable(CuentaContable cuentaContable);
	List<FormaPago> findByEmpresaAndCuentaContableAndIdentificador(Empresa empresa, CuentaContable cuentaContable, String identificador);
	List<FormaPago> findByEmpresaAndCuentaContable(Empresa empresa, CuentaContable cuentaContable);
}
