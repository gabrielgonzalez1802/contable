package com.contable.service;

import java.util.List;

import com.contable.model.Empleado;
import com.contable.model.Empresa;

public interface IEmpleadosService {
	Empleado buscarPorId(Integer id);
	List<Empleado> buscarPorCedula(String cedula);
	List<Empleado> buscarTodos();
	List<Empleado> buscarPorEmpresa(Empresa empresa);
	List<Empleado> buscarTodosEstado(Integer estado);
	void guardar(Empleado empleado);
	void eliminar(Empleado empleado);
}
