package com.contable.service;

import java.util.List;

import com.contable.model.Token;

public interface ITokensService {
	Token buscarPorId(Integer id);
	List<Token> buscarTodos();
	List<Token> buscarToken(String codigo);
	List<Token> buscarPorEstado(Integer estado);
	void guardar(Token token);
	void eliminar(Token token);
}
