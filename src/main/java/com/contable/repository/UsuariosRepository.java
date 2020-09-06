package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Perfil;
import com.contable.model.Usuario;

public interface UsuariosRepository extends JpaRepository<Usuario, Integer> {
	Usuario findByUsername(String username);
	List<Usuario> findByPerfil(Perfil perfil);
	List<Usuario> findByEstatus(Integer estatus);
	List<Usuario> findByPerfilAndEstatus(Perfil perfil, Integer estatus);
	
}