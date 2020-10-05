package com.contable.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.ComprobanteFiscal;
import com.contable.repository.ComprobantesFiscalesRepository;

@Service
public class ComprobantesFiscalesServiceJpa implements IComprobantesFiscalesService{
	
	@Autowired
	private ComprobantesFiscalesRepository repo;

	@Override
	public ComprobanteFiscal buscarPorId(Integer id) {
		Optional<ComprobanteFiscal> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public void guardar(ComprobanteFiscal comprobanteFiscal) {
		repo.save(comprobanteFiscal);
	}

	@Override
	public void eliminar(ComprobanteFiscal comprobanteFiscal) {
		repo.delete(comprobanteFiscal);
	}

	@Override
	public List<ComprobanteFiscal> buscarTodos() {
		return repo.findAll();
	}

}
