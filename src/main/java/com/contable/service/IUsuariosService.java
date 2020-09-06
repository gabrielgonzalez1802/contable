package com.contable.service;

import java.util.List;

import com.contable.model.Perfil;
import com.contable.model.Usuario;

public interface IUsuariosService {
	Usuario buscarPorId(Integer id);
	List<Usuario> buscarPorPerfil(Perfil perfil);
	List<Usuario> buscarPorEstado(Integer estado);
	List<Usuario> buscarPorPerfilEstado(Perfil perfil, Integer estado);
	void guardar(Usuario usurio);
	void eliminar(Integer id);
}
