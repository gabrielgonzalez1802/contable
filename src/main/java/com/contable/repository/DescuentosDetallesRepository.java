package com.contable.repository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.DescuentoDetalle;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;

public interface DescuentosDetallesRepository extends JpaRepository<DescuentoDetalle, Integer> {
	List<DescuentoDetalle> findByEmpresaAndCarpeta(Empresa empresa, Carpeta carpeta);
	List<DescuentoDetalle> findByEmpresaAndCarpetaAndCliente(Empresa empresa, Carpeta carpeta, Cliente cliente);
	List<DescuentoDetalle> findByEmpresaAndCarpetaAndPrestamoIn(Empresa empresa, Carpeta carpeta,
			LinkedList<Prestamo> prestamos);
	List<DescuentoDetalle> findByEmpresaAndCarpetaAndFechaBetween(Empresa empresa, Carpeta carpeta, Date desde, Date hasta);
}
