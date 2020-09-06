package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Cliente;
import com.contable.repository.ClientesRepository;
import com.contable.service.IClientesService;

@Service
public class ClientesServiceJpa implements IClientesService{
	
	@Autowired
	private ClientesRepository repo;

	@Override
	public Cliente buscarPorId(Integer id) {
		Optional<Cliente> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Cliente> buscarTodos() {
		return repo.findAll();
	}

	@Override
	public List<Cliente> buscarPorEstado(Integer estado) {
		return repo.findByEstado(estado);
	}

	@Override
	public void guardar(Cliente cliente) {
		repo.save(cliente);
	}

	@Override
	public void eliminar(Integer idCliente) {
		Optional<Cliente> optional = repo.findById(idCliente);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public void eliminar(Cliente cliente) {
		repo.delete(cliente);
	}

}
