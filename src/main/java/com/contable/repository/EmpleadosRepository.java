package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Empleado;
import com.contable.model.Empresa;

public interface EmpleadosRepository extends JpaRepository<Empleado, Integer> {
	List<Empleado> findByEmpresa(Empresa empresa);
	List<Empleado> findByCedula(String cedula);
	List<Empleado> findByEstado(Integer estado);
}
