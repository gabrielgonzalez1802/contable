package com.contable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Perfil;

public interface PerfilesRepository extends JpaRepository<Perfil, Integer> {
	Perfil findByPerfil(String perfil);
}
