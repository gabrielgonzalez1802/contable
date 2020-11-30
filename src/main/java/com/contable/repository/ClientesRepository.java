package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Cliente;
import com.contable.model.Empresa;

public interface ClientesRepository extends JpaRepository<Cliente, Integer> {
	List<Cliente> findByEstado(Integer estado);
	List<Cliente> findByNombreContaining(String nombre);
	List<Cliente> findByEmpresaAndNombreContaining(Empresa empresa, String nombre);
	Cliente findByTipoDocumentoAndCedula(String tipoDocumento, String cedula);
	Cliente findByTipoDocumentoAndCedulaAndEmpresa(String string, String item, Empresa empresa);
	List<Cliente> findByEmpresa(Empresa empresa);
}
