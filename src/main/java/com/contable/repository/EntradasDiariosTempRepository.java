package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiarioTemp;
import com.contable.model.Usuario;

public interface EntradasDiariosTempRepository extends JpaRepository<EntradaDiarioTemp, Integer> {
	List<EntradaDiarioTemp> findByEmpresaAndUsuario(Empresa empresa, Usuario usuario);
	List<EntradaDiarioTemp> findByEmpresaAndCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	List<EntradaDiarioTemp> findByCuentaContable(CuentaContable cuentaContable);
}
