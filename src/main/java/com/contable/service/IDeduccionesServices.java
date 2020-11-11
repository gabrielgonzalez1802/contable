package com.contable.service;

import java.util.List;

import com.contable.model.Deduccion;
import com.contable.model.Empleado;

public interface IDeduccionesServices {
	Deduccion buscarPorId(Integer id);
	List<Deduccion> buscarPorEmpleado(Empleado empleado);
	List<Deduccion> buscarPorEmpleadoEstado(Empleado empleado, Integer estado);
	List<Deduccion> buscarPorEstado(Integer estado);
	List<Deduccion> buscarTodos();
	void guardar(Deduccion deduccion);
	void eliminar(Deduccion deduccion);
}
