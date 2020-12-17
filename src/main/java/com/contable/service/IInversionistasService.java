package com.contable.service;

import java.util.List;

import com.contable.model.Inversionista;

public interface IInversionistasService {
	Inversionista buscarPorId(Integer id);
	List<Inversionista> buscarTodos();
	void guardar(Inversionista inversionista);
	void eliminar(Inversionista inversionista);
}
