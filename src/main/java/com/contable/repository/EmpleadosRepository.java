package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Empleado;

public interface EmpleadosRepository extends JpaRepository<Empleado, Integer> {
	List<Empleado> findByCedula(String cedula);
	List<Empleado> findByEstado(Integer estado);
}
