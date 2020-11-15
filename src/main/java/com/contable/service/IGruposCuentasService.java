package com.contable.service;

import com.contable.model.GrupoCuenta;

public interface IGruposCuentasService {
	GrupoCuenta buscarPorId(Integer id);
	GrupoCuenta buscarPorTipo(String tipo);
	GrupoCuenta buscarPorCodigo(Integer id);
	void guardar(GrupoCuenta grupoCuenta);
	void eliminar(GrupoCuenta grupoCuenta);
}
