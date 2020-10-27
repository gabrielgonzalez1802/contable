package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Abono;
import com.contable.model.AbonoDetalle;
import com.contable.repository.AbonosDetallesRepository;
import com.contable.service.IAbonosDetallesService;

@Service
public class AbonosDetallesServiceJpa implements IAbonosDetallesService {

	@Autowired
	private AbonosDetallesRepository repo;
	
	@Override
	public AbonoDetalle buscarPorId(Integer id) {
		Optional<AbonoDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<AbonoDetalle> buscarPorAbono(Abono abono) {
		return repo.findByAbono(abono);
	}

	@Override
	public void guardar(AbonoDetalle abonoDetalle) {
		repo.save(abonoDetalle);
	}

	@Override
	public void eliminar(AbonoDetalle abonoDetalle) {
		repo.delete(abonoDetalle);
	}

	@Override
	public List<AbonoDetalle> buscarPorAbonoConcepto(Abono abono, String concepto) {
		return repo.findByAbonoAndConcepto(abono, concepto);
	}

}
