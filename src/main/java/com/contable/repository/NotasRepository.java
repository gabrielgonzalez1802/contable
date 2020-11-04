package com.contable.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Nota;
import com.contable.model.Prestamo;
import com.contable.model.PrestamoDetalle;

public interface NotasRepository extends JpaRepository<Nota, Integer> {
	List<Nota> findByPrestamo(Prestamo prestamo);
	List<Nota> findByPrestamoDetalle(PrestamoDetalle prestamoDetalle);
	List<Nota> findByTipo(Integer tipo);
	List<Nota> findByPrestamoAndFecha(Prestamo prestamo, Date fecha);
	
}
