package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Carpeta;
import com.contable.model.Empresa;

public interface CarpetasRepository extends JpaRepository<Carpeta, Integer> {
	Carpeta findByNombre(String nombre);
	List<Carpeta> findByPrincipal(Integer principal);
	Carpeta findByNombreAndEmpresa(String nombre, Empresa empresa);
	List<Carpeta> findByPrincipalAndEmpresa(Integer principal, Empresa empresa);
}
