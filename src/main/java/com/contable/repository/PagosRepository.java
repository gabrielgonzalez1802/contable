package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Compra;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.model.Pago;
import com.contable.model.Usuario;

public interface PagosRepository extends JpaRepository<Pago, Integer> {
	List<Pago> findByEmpresa(Empresa empresa);
	List<Pago> findByEmpresaAndUsuario(Empresa empresa, Usuario usuario);
	List<Pago> findByEmpresaAndEntradaDiario(Empresa empresa, EntradaDiario entradaDiario);
	List<Pago> findByEmpresaAndEntradaDiarioAndUsuario(Empresa empresa, EntradaDiario entradaDiario, Usuario usuario);
	List<Pago> findByEmpresaAndCompra(Empresa empresa, Compra compra);
}
