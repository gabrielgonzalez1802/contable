package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.PagoTemp;
import com.contable.model.Prestamo;

public interface PagosTempRepository extends JpaRepository<PagoTemp, Integer> {
	List<PagoTemp> findByPrestamo(Prestamo prestamo);
}
