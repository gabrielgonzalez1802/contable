package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Inversionista;
import com.contable.repository.InversionistasRepository;
import com.contable.service.IInversionistasService;

@Service
public class InversionistasServiceJpa implements IInversionistasService {

	@Autowired
	private InversionistasRepository repo;
	
	@Override
	public Inversionista buscarPorId(Integer id) {
		Optional<Inversionista> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Inversionista> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public void guardar(Inversionista inversionista) {
		repo.save(inversionista);
	}

	@Override
	public void eliminar(Inversionista inversionista) {
		repo.delete(inversionista);
	}

}
