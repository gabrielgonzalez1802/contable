package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Asignacion;
import com.contable.model.Empleado;
import com.contable.repository.AsignacionesRepository;
import com.contable.service.IAsignacionesServices;

@Service
public class AsignacionesServiceJpa implements IAsignacionesServices{

	@Autowired
	private AsignacionesRepository repo;
	
	@Override
	public Asignacion buscarPorId(Integer id) {
		Optional<Asignacion> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	@Override
	public List<Asignacion> buscarPorEstado(Integer estado) {
		return repo.findByEstado(estado);
	}

	@Override
	public List<Asignacion> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public void guardar(Asignacion asignacion) {
		repo.save(asignacion);
	}

	@Override
	public void eliminar(Asignacion asignacion) {
		repo.delete(asignacion);
	}

	@Override
	public List<Asignacion> buscarPorEmpleado(Empleado empleado) {
		return repo.findByEmpleado(empleado);
	}

	@Override
	public List<Asignacion> buscarPorEmpleadoEstado(Empleado empleado, Integer estado) {
		return repo.findByEmpleadoAndEstado(empleado, estado);
	}

}
