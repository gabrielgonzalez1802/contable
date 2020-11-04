package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.PagoTemp;
import com.contable.model.Prestamo;
import com.contable.repository.PagosTempRepository;
import com.contable.service.IPagosTempService;

@Service
public class PagosTempServiceJpa implements IPagosTempService{
	
	@Autowired
	private PagosTempRepository repo;

	@Override
	public PagoTemp buscarPorId(Integer id) {
		Optional<PagoTemp> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<PagoTemp> buscarPorPrestamo(Prestamo prestamo) {
		return repo.findByPrestamo(prestamo);
	}

	@Override
	public void guardar(PagoTemp pagoTemp) {
		repo.save(pagoTemp);
	}

	@Override
	public void eliminar(PagoTemp pagoTemp) {
		repo.delete(pagoTemp);
	}

	@Override
	public void eliminar(List<PagoTemp> pagosTemp) {
		repo.deleteAll(pagosTemp);
	}
	
}
