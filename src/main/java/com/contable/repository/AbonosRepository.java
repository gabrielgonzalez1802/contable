package com.contable.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Abono;
import com.contable.model.Cliente;
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
	
}
