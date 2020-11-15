package com.contable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.GrupoCuenta;

public interface GruposCuentasRepository extends JpaRepository<GrupoCuenta, Integer> {
	GrupoCuenta findByTipo(String tipo);
	GrupoCuenta findByCodigo(Integer id);
}
