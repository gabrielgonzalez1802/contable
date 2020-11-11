package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Deduccion;
import com.contable.model.Empleado;
import com.contable.repository.DeduccionesRepository;
import com.contable.service.IDeduccionesServices;

@Service
public class DeduduccionesServiceJpa implements IDeduccionesServices{
	
	@Autowired
	private DeduccionesRepository repo;

	@Override
	public Deduccion buscarPorId(Integer id) {
		Optional<Deduccion> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Deduccion> buscarPorEstado(Integer estado) {
		return repo.findByEstado(estado);
	}

	@Override
	public List<Deduccion> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public void guardar(Deduccion deduccion) {
		repo.save(deduccion);
	}

	@Override
	public void eliminar(Deduccion deduccion) {
		repo.delete(deduccion);
	}

	@Override
	public List<Deduccion> buscarPorEmpleado(Empleado empleado) {
		return repo.findByEmpleado(empleado);
	}

	@Override
	public List<Deduccion> buscarPorEmpleadoEstado(Empleado empleado, Integer estado) {
		return repo.findByEmpleadoAndEstado(empleado, estado);
	}

}
