package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.CuentaContable;
import com.contable.model.Diario;
import com.contable.model.EntradaDiario;

public interface EntradasDiariosRepository extends JpaRepository<EntradaDiario, Integer> {
	List<EntradaDiario> findByCuentaContable(CuentaContable cuentaContable);
	List<EntradaDiario> findByDiario(Diario diario);
	List<EntradaDiario> findByCuentaContableAndDiario(CuentaContable cuentaContable, Diario diario);
}
