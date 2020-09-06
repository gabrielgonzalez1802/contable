package com.contable.service;

import com.contable.model.Perfil;

public interface IPerfilesService {
	Perfil buscarPorId(Integer id);
	Perfil buscarPorPerfil(String perfil);
	void guardar(Perfil perfil);
	void elminar(Integer id);
}
