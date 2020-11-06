package com.contable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Empresa;

public interface EmpresasRepository extends JpaRepository<Empresa, Integer>{
	
}
