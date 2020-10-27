package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Abono;
import com.contable.model.Prestamo;
import com.contable.repository.AbonosRepository;
import com.contable.service.IAbonosService;

@Service
public class AbonosServiceJpa implements IAbonosService{
	
	@Autowired
	private AbonosRepository repo;

	@Override
	public Abono buscarPorId(Integer id) {
		Optional<Abono> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Abono> buscarPorPrestamo(Prestamo prestamo) {
		return repo.findByPrestamo(prestamo);
	}

	@Override
	public List<Abono> buscarPorPrestamoEstado(Prestamo prestamo, Integer estado) {
		return repo.findByPrestamoAndEstado(prestamo, estado);
	}

	@Override
	public void guardar(Abono abono) {
		repo.save(abono);
	}

	@Override
	public void eliminar(Abono abono) {
		repo.delete(abono);
	}

}
