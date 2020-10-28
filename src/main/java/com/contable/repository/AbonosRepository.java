package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Abono;
import com.contable.model.Cliente;
import com.contable.model.Prestamo;

public interface AbonosRepository extends JpaRepository<Abono, Integer> {
	List<Abono> findByPrestamo(Prestamo prestamo);
	List<Abono> findByCliente(Cliente cliente);
	List<Abono> findByPrestamoAndEstado(Prestamo prestamo, Integer estado);
	List<Abono> findByPrestamoIn(List<Prestamo> prestamos);
}
