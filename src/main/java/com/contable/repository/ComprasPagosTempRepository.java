package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Compra;
import com.contable.model.CompraPagoTemp;
import com.contable.model.Empresa;
import com.contable.model.Usuario;

public interface ComprasPagosTempRepository extends JpaRepository<CompraPagoTemp, Integer> {
	List<CompraPagoTemp> findByEmpresaAndUsuarioAndCompra(Empresa empresa, Usuario usuario, Compra compra);
	List<CompraPagoTemp> findByEmpresaAndCompra(Empresa empresa, Compra compra);
}