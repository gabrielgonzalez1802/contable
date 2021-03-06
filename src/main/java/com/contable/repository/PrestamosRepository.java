package com.contable.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;
import com.contable.model.Usuario;

public interface PrestamosRepository extends JpaRepository<Prestamo, Integer>{
	List<Prestamo> findByUsuario(Usuario usuario);
	List<Prestamo> findByEmpresaAndEstado(Empresa empresa, Integer estado);
	List<Prestamo> findByEstadoNotIn(List<Integer> estados);
	List<Prestamo> findByEmpresaAndEstadoNotIn(Empresa empresa, List<Integer> estados);
	List<Prestamo> findByEmpresaAndMonedaAndEstadoNotIn(Empresa empresa, String moneda, List<Integer> estados);
	List<Prestamo> findByClienteAndCarpetaAndEmpresaAndMonedaAndEstadoNotIn(Cliente cliente, Carpeta carpeta,
			Empresa empresa, String moneda, List<Integer> estados);
	List<Prestamo> findByCarpetaAndEmpresaAndMonedaAndEstadoNotIn(Carpeta carpeta, Empresa empresa, String moneda,
			List<Integer> estados);
	List<Prestamo> findByCarpetaAndEmpresaAndEstado(Carpeta carpeta, Empresa empresa, Integer estado);
	List<Prestamo> findByClienteAndCarpetaAndEmpresaAndEstado(Cliente cliente, Carpeta carpeta, Empresa empresa,
			Integer estado);
	List<Prestamo> findByEstado(Integer estado);
	List<Prestamo> findByCliente(Cliente cliente);
	List<Prestamo> findByFecha(Date fecha);
	List<Prestamo> findByCarpetaAndFecha(Carpeta carpeta, Date fecha);
	List<Prestamo> findByCarpeta(Carpeta carpeta);
	List<Prestamo> findByCarpetaAndEmpresa(Carpeta carpeta, Empresa empresa);
	List<Prestamo> findByTipo(String tipo);
	List<Prestamo> findByClienteAndCarpetaOrderByFechaDesc(Cliente cliente, Carpeta carpeta);
	List<Prestamo> findByClienteAndCarpetaAndEmpresaOrderByFechaDesc(Cliente cliente, Carpeta carpeta, Empresa empresa);
	List<Prestamo> findByClienteAndCarpetaAndEmpresaAndFechaBetweenOrderByFechaDesc(Cliente cliente, Carpeta carpeta, Empresa empresa, Date desde, Date hasta);
	List<Prestamo> findByCarpetaAndEmpresaAndFechaBetweenOrderByFechaDesc(Carpeta carpeta, Empresa empresa, Date desde, Date hasta);
	List<Prestamo> findByCarpetaOrderByFechaDesc(Carpeta carpeta);
}
