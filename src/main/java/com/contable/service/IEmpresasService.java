package com.contable.service;

import java.util.List;

import com.contable.model.Empresa;

public interface IEmpresasService {
	Empresa buscarPorId(Integer id);
	List<Empresa> buscarTodas();
	void guardar(Empresa empresa);
	void eliminar(Empresa empresa);
}
