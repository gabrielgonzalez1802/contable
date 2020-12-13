package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Compra;
import com.contable.model.CompraPago;
import com.contable.model.Empresa;
import com.contable.model.Usuario;

public interface ComprasPagosRepository extends JpaRepository<CompraPago, Integer> {
	List<CompraPago> findByEmpresa(Empresa empresa);
	List<CompraPago> findByEmpresaAndUsuarioAndCompra(Empresa empresa, Usuario usuario, Compra compra);
	List<CompraPago> findByEmpresaAndCompra(Empresa empresa, Compra compra);
}