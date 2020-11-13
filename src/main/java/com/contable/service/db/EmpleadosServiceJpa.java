package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Empleado;
import com.contable.model.Empresa;
import com.contable.repository.EmpleadosRepository;
import com.contable.service.IEmpleadosService;

@Service
public class EmpleadosServiceJpa implements IEmpleadosService {

	@Autowired
	private EmpleadosRepository repo;
	
	@Override
	public Empleado buscarPorId(Integer id) {
		Optional<Empleado> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Empleado> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public void guardar(Empleado empleado) {
		repo.save(empleado);
	}

	@Override
	public void eliminar(Empleado empleado) {
		repo.delete(empleado);
	}

	@Override
	public List<Empleado> buscarPorCedula(String cedula) {
		return repo.findByCedula(cedula);
	}

	@Override
	public List<Empleado> buscarTodosEstado(Integer estado) {
		return repo.findByEstado(estado);
	}

	@Override
	public List<Empleado> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}

}
