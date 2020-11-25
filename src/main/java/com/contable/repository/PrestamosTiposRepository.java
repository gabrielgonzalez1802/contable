package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.PrestamoTipo;

public interface PrestamosTiposRepository extends JpaRepository<PrestamoTipo, Integer> {
	PrestamoTipo findByTipo(String tipo);
}
