package com.contable.service;

import java.util.Date;
import java.util.List;

import com.contable.model.Diario;

public interface IDiariosService {
	Diario buscarPorId(Integer id);
	List<Diario> buscarTodos();
	List<Diario> buscarPorFechaBetween(Date desde, Date hasta);
	void guardar(Diario diario);
	void eliminar(Diario diario);
}
