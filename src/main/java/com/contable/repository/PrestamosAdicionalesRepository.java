package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Prestamo;
import com.contable.model.PrestamoAdicional;
import com.contable.model.PrestamoDetalle;

public interface PrestamosAdicionalesRepository extends JpaRepository<PrestamoAdicional, Integer> {
	List<PrestamoAdicional> findByPrestamo(Prestamo prestamo);
	List<PrestamoAdicional> findByPrestamoDetalle(PrestamoDetalle prestamoDetalle);
	List<PrestamoAdicional> findByPrestamoAndEstado(Prestamo prestamo, Integer estado);
}
