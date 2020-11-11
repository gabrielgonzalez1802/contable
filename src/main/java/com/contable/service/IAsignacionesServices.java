package com.contable.service;

import java.util.List;

import com.contable.model.Asignacion;
import com.contable.model.Empleado;

public interface IAsignacionesServices {
	Asignacion buscarPorId(Integer id);
	List<Asignacion> buscarPorEstado(Integer estado);
	List<Asignacion> buscarTodos();
	List<Asignacion> buscarPorEmpleado(Empleado empleado);
	List<Asignacion> buscarPorEmpleadoEstado(Empleado empleado, Integer estado);
	void guardar(Asignacion asignacion);
	void eliminar(Asignacion asignacion);
}
