package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Carpeta;
import com.contable.model.Cuenta;

public interface CuentasRepository extends JpaRepository<Cuenta, Integer> {
	List<Cuenta> findByCarpeta(Carpeta carpeta);
}
