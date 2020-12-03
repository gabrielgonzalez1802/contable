package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Empresa;
import com.contable.model.Producto;

public interface ProductosRepository extends JpaRepository<Producto, Integer>{
	List<Producto> findByEmpresa(Empresa empresa);
	List<Producto> findByEmpresaAndActivoFijo(Empresa empresa, Integer activoFijo);
}
