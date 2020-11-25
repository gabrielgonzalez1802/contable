package com.contable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.MotivoPrestamoAdicional;

public interface MotivosPrestamosAdicionalesRepository extends JpaRepository<MotivoPrestamoAdicional, Integer> {
	MotivoPrestamoAdicional findByMotivo(String motivo);
}
