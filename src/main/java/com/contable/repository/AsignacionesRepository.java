package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Asignacion;
import com.contable.model.Empleado;

public interface AsignacionesRepository extends JpaRepository<Asignacion, Integer> {
	List<Asignacion> findByEstado(Integer estado);
	List<Asignacion> findByEmpleado(Empleado empleado);
	List<Asignacion> findByEmpleadoAndEstado(Empleado empleado, Integer estado);
}
