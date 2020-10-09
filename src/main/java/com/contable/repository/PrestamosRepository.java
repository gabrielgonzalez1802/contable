package com.contable.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Prestamo;

public interface PrestamosRepository extends JpaRepository<Prestamo, Integer>{
	List<Prestamo> findByEstado(Integer estado);
	List<Prestamo> findByCliente(Cliente cliente);
	List<Prestamo> findByFecha(Date fecha);
	List<Prestamo> findByCarpeta(Carpeta carpeta);
	List<Prestamo> findByClienteAndCarpetaOrderByFechaDesc(Cliente cliente, Carpeta carpeta);
}
