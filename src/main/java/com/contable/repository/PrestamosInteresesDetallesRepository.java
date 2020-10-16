package com.contable.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Prestamo;
import com.contable.model.PrestamoInteresDetalle;

public interface PrestamosInteresesDetallesRepository extends JpaRepository<PrestamoInteresDetalle, Integer> {
	List<PrestamoInteresDetalle> findByPrestamo(Prestamo prestamo);
	List<PrestamoInteresDetalle> findByPrestamoAndFecha(Prestamo prestamo, Date fecha);
}
