package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Token;
import com.contable.repository.TokensRepository;
import com.contable.service.ITokensService;

@Service
public class TokensServiceJpa implements ITokensService{

	@Autowired
	private TokensRepository repo;
	
	@Override
	public Token buscarPorId(Integer id) {
		Optional<Token> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Token> buscarPorEstado(Integer estado) {
		return repo.findByEstado(estado);
	}
	
	@Override
	public List<Token> buscarTodos() {
		return repo.findAll();
	}
	
	@Override
	public List<Token> buscarToken(String token) {
		return repo.findByToken(token);
	}

	@Override
	public void guardar(Token token) {
		repo.save(token);
	}

	@Override
	public void eliminar(Token token) {
		repo.delete(token);
	}
}
