package com.contable.service;

import java.util.Date;
import java.util.List;

import com.contable.model.Abono;
import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;
import com.contable.model.Usuario;

public interface IAbonosService {
	Abono buscarPorId(Integer id);
	List<Abono> buscarPorPrestamo(Prestamo prestamo);
	List<Abono> buscarPorPrestamos(List<Prestamo> prestamos);
	List<Abono> buscarPorPrestamoEstado(Prestamo prestamo, Integer estado);
	List<Abono> buscarPorPrestamosOrderByAbono(List<Prestamo> prestamos);
	List<Abono> buscarPorPrestamosFecha(List<Prestamo> prestamos, Date fecha);
	List<Abono> buscarPorClienteEmpresa(Cliente cliente, Empresa empresa);
	List<Abono> buscarPorClienteEmpresaCarpeta(Cliente cliente, Empresa empresa, Carpeta carpeta);
	List<Abono> buscarPorClienteEmpresaFechas(Cliente cliente, Empresa empresa, Date desde, Date hasta);
	List<Abono> buscarPorClienteEmpresaCarpetaFechas(Cliente cliente, Empresa empresa, Carpeta carpeta, Date desde, Date hasta);
	List<Abono> buscarPorEmpresa(Empresa empresa);
	List<Abono> buscarPorEmpresaCarpeta(Empresa empresa, Carpeta carpeta);
	List<Abono> buscarPorEmpresaFechas(Empresa empresa, Date desde, Date hasta);
	List<Abono> buscarPorEmpresaCarpetaFechas(Empresa empresa, Carpeta carpeta, Date desde, Date hasta);
	List<Abono> buscarPorPrestamosUsuario(List<Prestamo> prestamos, Usuario usuarioTemp);
	List<Abono> buscarPorFecha(Date fecha);
	void guardar(Abono abono);
	void eliminar(Abono abono);
}
