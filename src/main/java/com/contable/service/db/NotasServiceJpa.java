package com.contable.service.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Nota;
import com.contable.model.Prestamo;
import com.contable.model.PrestamoDetalle;
import com.contable.repository.NotasRepository;
import com.contable.service.INotasService;

@Service
public class NotasServiceJpa implements INotasService {
	
	@Autowired
	private NotasRepository repo;

	@Override
	public Nota buscarPorId(Integer id) {
		Optional<Nota> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Nota> buscarPorPrestamo(Prestamo prestamo) {
		return repo.findByPrestamo(prestamo);
	}

	@Override
	public List<Nota> buscarPorPrestamoDetalle(PrestamoDetalle prestamoDetalle) {
		return repo.findByPrestamoDetalle(prestamoDetalle);
	}

	@Override
	public List<Nota> buscarPorTipo(Integer tipo) {
		return repo.findByTipo(tipo);
	}

	@Override
	public List<Nota> buscarPorPrestamoFecha(Prestamo prestamo, Date fecha) {
		return repo.findByPrestamoAndFecha(prestamo, fecha);
	}

	@Override
	public void guardar(Nota nota) {
		repo.save(nota);
	}

	@Override
	public void eliminar(Nota nota) {
		repo.delete(nota);
	}

}
