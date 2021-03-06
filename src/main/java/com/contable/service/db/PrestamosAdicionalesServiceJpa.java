package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Prestamo;
import com.contable.model.PrestamoAdicional;
import com.contable.model.PrestamoDetalle;
import com.contable.repository.PrestamosAdicionalesRepository;
import com.contable.service.IPrestamosAdicionalesService;

@Service
public class PrestamosAdicionalesServiceJpa implements IPrestamosAdicionalesService{
	
	@Autowired
	private PrestamosAdicionalesRepository repo;

	@Override
	public List<PrestamoAdicional> buscarPorPrestamo(Prestamo prestamo) {
		return repo.findByPrestamo(prestamo);
	}

	@Override
	public List<PrestamoAdicional> buscarPorPrestamoDetalle(PrestamoDetalle prestamoDetalle) {
		return repo.findByPrestamoDetalle(prestamoDetalle);
	}

	@Override
	public void guardar(PrestamoAdicional prestamoAdicional) {
		repo.save(prestamoAdicional);
	}

	@Override
	public void eliminar(PrestamoAdicional prestamoAdicional) {
		repo.delete(prestamoAdicional);
	}

	@Override
	public List<PrestamoAdicional> buscarPorPrestamoEstado(Prestamo prestamo, Integer estado) {
		return repo.findByPrestamoAndEstado(prestamo, estado);
	}
	@Override
	public List<PrestamoAdicional> buscarPorPrestamoNumeroCuota(Prestamo prestamo, Integer numeroCuota) {
		return repo.findByPrestamoAndNumeroCuota(prestamo, numeroCuota);
	}

	@Override
	public PrestamoAdicional buscarPorId(Integer id) {
		Optional<PrestamoAdicional> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

}
