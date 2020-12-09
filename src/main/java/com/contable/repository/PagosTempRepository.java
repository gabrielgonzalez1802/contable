package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Compra;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.model.PagoTemp;
import com.contable.model.Prestamo;
import com.contable.model.Usuario;

public interface PagosTempRepository extends JpaRepository<PagoTemp, Integer> {
	List<PagoTemp> findByPrestamo(Prestamo prestamo);
	List<PagoTemp> findByEmpresa(Empresa empresa);
	List<PagoTemp> findByEmpresaAndUsuario(Empresa empresa, Usuario usuario);
	List<PagoTemp> findByEmpresaAndEntradaDiario(Empresa empresa, EntradaDiario entradaDiario);
	List<PagoTemp> findByEmpresaAndEntradaDiarioAndUsuario(Empresa empresa, EntradaDiario entradaDiario, Usuario usuario);
	List<PagoTemp> findByEmpresaAndCompra(Empresa empresa, Compra compra);
}
