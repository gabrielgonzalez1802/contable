package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Empresa;
import com.contable.model.Suplidor;
import com.contable.model.Usuario;

public interface SuplidoresRepository extends JpaRepository<Suplidor, Integer> {
	List<Suplidor> findByEmpresa(Empresa empresa);
	List<Suplidor> findByEmpresaAndUsuario(Empresa empresa, Usuario usuario);
}
