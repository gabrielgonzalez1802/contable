package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.Suplidor;
import com.contable.model.SuplidorCuentaContable;
import com.contable.model.Usuario;

public interface SuplidoresCuentasContablesRepository extends JpaRepository<SuplidorCuentaContable, Integer> {
	List<SuplidorCuentaContable> findByEmpresaAndSuplidor(Empresa empresa, Suplidor suplidor);
	List<SuplidorCuentaContable> findByEmpresaAndUsuario(Empresa empresa, Usuario usuario);
	List<SuplidorCuentaContable> findByEmpresaAndUsuarioAndCuentaContable(Empresa empresa, Usuario usuario, CuentaContable cuentaContable);
	List<SuplidorCuentaContable> findByEmpresaAndCuentaContable(Empresa empresa, CuentaContable cuentaContable);
}
