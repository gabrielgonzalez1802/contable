package com.contable.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.Producto;

public interface ProductosRepository extends JpaRepository<Producto, Integer>, PagingAndSortingRepository<Producto, Integer>{
	List<Producto> findByEmpresa(Empresa empresa);
	List<Producto> findByEmpresaAndCuentaContable(Empresa empresa, CuentaContable cuentaContable);
	List<Producto> findByEmpresaOrderByNombre(Empresa empresa);
	Page<Producto> findByEmpresaAndNombreContaining(Empresa empresa, String nombre, Pageable pageable);
	Page<Producto> findByEmpresaOrderByNombre(Empresa empresa, Pageable pageable);
	List<Producto> findByEmpresaAndActivoFijo(Empresa empresa, Integer activoFijo);
	List<Producto> findByEmpresaAndActivoFijoIn(Empresa empresa, List<Integer> activosFijos);
	Page<Producto> findByEmpresaAndActivoFijoOrderByNombre(Empresa empresa, Integer activoFijo, Pageable pageable);
	Page<Producto> findByEmpresaAndActivoFijoAndNombreContainingOrderByNombre(Empresa empresa, Integer activoFijo, String nombre, Pageable pageable);
}
