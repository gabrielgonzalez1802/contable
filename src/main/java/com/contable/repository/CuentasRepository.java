package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Carpeta;
import com.contable.model.Cuenta;
import com.contable.model.Empresa;

public interface CuentasRepository extends JpaRepository<Cuenta, Integer> {
	List<Cuenta> findByCarpeta(Carpeta carpeta);
	List<Cuenta> findByCarpetaAndEmpresa(Carpeta carpeta, Empresa empresa);
	List<Cuenta> findByCarpetaAndEmpresaAndMoneda(Carpeta carpeta, Empresa empresa, String moneda);
}
