package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.SuplidorCuentaContableTemp;
import com.contable.model.Usuario;

public interface SuplidoresCuentasContablesTempRepository extends JpaRepository<SuplidorCuentaContableTemp, Integer>{
	List<SuplidorCuentaContableTemp> findByEmpresaAndUsuario(Empresa empresa, Usuario usuario);
	List<SuplidorCuentaContableTemp> findByEmpresaAndUsuarioAndCuentaContable(Empresa empresa, Usuario usuario, CuentaContable cuentaContable);
	List<SuplidorCuentaContableTemp> findByEmpresaAndCuentaContable(Empresa empresa, CuentaContable cuentaContable);
}
