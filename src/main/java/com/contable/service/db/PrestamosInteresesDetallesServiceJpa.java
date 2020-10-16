package com.contable.service.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Prestamo;
import com.contable.model.PrestamoInteresDetalle;
import com.contable.repository.PrestamosInteresesDetallesRepository;
import com.contable.service.IPrestamosInteresesDetallesService;

@Service
public class PrestamosInteresesDetallesServiceJpa implements IPrestamosInteresesDetallesService{

	@Autowired
	private PrestamosInteresesDetallesRepository repo;
	
	@Override
	public PrestamoInteresDetalle buscarPorId(Integer id) {
		Optional<PrestamoInteresDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<PrestamoInteresDetalle> buscarPorPrestamo(Prestamo prestamo) {
		return repo.findByPrestamo(prestamo);
	}

	@Override
	public void guardar(PrestamoInteresDetalle prestamoInteresDetalle) {
		repo.save(prestamoInteresDetalle);
	}

	@Override
	public void eliminar(PrestamoInteresDetalle prestamoInteresDetalle) {
		repo.delete(prestamoInteresDetalle);
	}

	@Override
	public List<PrestamoInteresDetalle> buscarPorPrestamoFecha(Prestamo prestamo, Date fecha) {
		return repo.findByPrestamoAndFecha(prestamo, fecha);
	}

}
