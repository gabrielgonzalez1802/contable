package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Deduccion;
import com.contable.model.Empleado;

public interface DeduccionesRepository extends JpaRepository<Deduccion, Integer> {
	List<Deduccion> findByEstado(Integer estado);
	List<Deduccion> findByEmpleado(Empleado empleado);
	List<Deduccion> findByEmpleadoAndEstado(Empleado empleado, Integer estado);
}
