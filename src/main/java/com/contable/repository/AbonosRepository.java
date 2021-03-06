package com.contable.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Abono;
import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;
import com.contable.model.Usuario;

public interface AbonosRepository extends JpaRepository<Abono, Integer> {
	List<Abono> findByPrestamo(Prestamo prestamo);
	List<Abono> findByCliente(Cliente cliente);
	List<Abono> findByPrestamoAndEstado(Prestamo prestamo, Integer estado);
	List<Abono> findByPrestamoIn(List<Prestamo> prestamos);
	List<Abono> findByPrestamoInAndFecha(List<Prestamo> prestamos, Date fecha);
	List<Abono> findByFecha(Date fecha);
	List<Abono> findByPrestamoInAndUsuario(List<Prestamo> prestamos, Usuario usuario);
	List<Abono> findByClienteAndEmpresa(Cliente cliente, Empresa empresa);
	List<Abono> findByClienteAndEmpresaAndCarpeta(Cliente cliente, Empresa empresa, Carpeta carpeta);
	List<Abono> findByClienteAndEmpresaAndFechaBetween(Cliente cliente, Empresa empresa, Date desde, Date hasta);
	List<Abono> findByClienteAndEmpresaAndCarpetaAndFechaBetween(Cliente cliente, Empresa empresa, Carpeta carpeta, Date desde, Date hasta);
	List<Abono> findByEmpresa(Empresa empresa);
	List<Abono> findByEmpresaAndCarpeta(Empresa empresa, Carpeta carpeta);
	List<Abono> findByEmpresaAndFechaBetween(Empresa empresa, Date desde, Date hasta);
	List<Abono> findByEmpresaAndCarpetaAndFechaBetween(Empresa empresa, Carpeta carpeta, Date desde, Date hasta);
}
