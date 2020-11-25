package com.contable.service;

import java.util.List;

import com.contable.model.PrestamoTipo;

public interface IPrestamosTiposService {
	PrestamoTipo buscarPorId(Integer id);
	PrestamoTipo buscarPorTipo(String tipo);
	List<PrestamoTipo> buscarTodos();
	void guardar(PrestamoTipo prestamoTipo);
	void eliminar(PrestamoTipo prestamoTipo);
}
