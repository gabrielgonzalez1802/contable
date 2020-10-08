package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Prestamo;
import com.contable.model.PrestamoDetalle;
import com.contable.repository.PrestamosDetallesRepository;
import com.contable.service.IPrestamosDetallesService;

@Service
public class PrestamosDetallesService implements IPrestamosDetallesService{
	
	@Autowired
	private PrestamosDetallesRepository repo;
	
	@Override
	public PrestamoDetalle buscarPorId(Integer id) {
		Optional<PrestamoDetalle> optional =  repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<PrestamoDetalle> buscarPorPrestamo(Prestamo prestamo){
		return repo.findByPrestamo(prestamo);
	}

	@Override
	public void guardar(PrestamoDetalle prestamoDetalle) {
		repo.save(prestamoDetalle);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<PrestamoDetalle> optional =  repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public void eliminar(List<PrestamoDetalle> prestamoDetalles) {
		repo.deleteAll(prestamoDetalles);
	}

}
