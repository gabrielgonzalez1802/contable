package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Compra;
import com.contable.model.CompraItbis;
import com.contable.repository.CompraItbisRepository;
import com.contable.service.IComprasItbisService;

@Service
public class CompraItbisServiceJpa implements IComprasItbisService{
	
	@Autowired
	private CompraItbisRepository repo;
	
	@Override
	public CompraItbis buscarPorId(Integer id) {
		Optional<CompraItbis> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<CompraItbis> buscarPorCompra(Compra compra) {
		return repo.findByCompra(compra);
	}

	@Override
	public void guardar(CompraItbis compraItbis) {
		repo.save(compraItbis);
	}

	@Override
	public void eliminar(CompraItbis compraItbis) {
		repo.delete(compraItbis);
	}

}
