package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.CompraProductoTemp;
import com.contable.model.Empresa;
import com.contable.model.Producto;
import com.contable.model.Usuario;

public interface ComprasProductosTempRepository extends JpaRepository<CompraProductoTemp, Integer> {
	List<CompraProductoTemp> findByEmpresa(Empresa empresa);
	List<CompraProductoTemp> findByEmpresaAndUsuario(Empresa empresa, Usuario usuario);
	List<CompraProductoTemp> findByEmpresaAndUsuarioAndProducto(Empresa empresa, Usuario usuario, Producto producto);
}
