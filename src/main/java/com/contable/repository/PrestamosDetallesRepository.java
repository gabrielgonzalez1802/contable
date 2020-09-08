package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Prestamo;
import com.contable.model.PrestamoDetalle;

public interface PrestamosDetallesRepository extends JpaRepository<PrestamoDetalle, Integer>{
	List<PrestamoDetalle> findByPrestamo(Prestamo prestamo);
}