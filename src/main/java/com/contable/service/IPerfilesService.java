package com.contable.service;

import java.util.List;

import com.contable.model.Perfil;

public interface IPerfilesService {
	List<Perfil> buscarTodos();
	Perfil buscarPorId(Integer id);
	Perfil buscarPorPerfil(String perfil);
	void guardar(Perfil perfil);
	void elminar(Integer id);
}
