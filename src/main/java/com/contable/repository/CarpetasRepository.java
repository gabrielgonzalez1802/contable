package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Carpeta;

public interface CarpetasRepository extends JpaRepository<Carpeta, Integer> {
	Carpeta findByNombre(String nombre);
	List<Carpeta> findByPrincipal(Integer principal);
}
