package com.contable.service.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Abono;
import com.contable.model.Cliente;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;
import com.contable.model.Usuario;
import com.contable.repository.AbonosRepository;
import com.contable.service.IAbonosService;

@Service
public class AbonosServiceJpa implements IAbonosService{
	
	@Autowired
	private AbonosRepository repo;

	@Override
	public Abono buscarPorId(Integer id) {
		Optional<Abono> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Abono> buscarPorPrestamo(Prestamo prestamo) {
		return repo.findByPrestamo(prestamo);
	}

	@Override
	public List<Abono> buscarPorPrestamoEstado(Prestamo prestamo, Integer estado) {
		return repo.findByPrestamoAndEstado(prestamo, estado);
	}

	@Override
	public void guardar(Abono abono) {
		repo.save(abono);
	}

	@Override
	public void eliminar(Abono abono) {
		repo.delete(abono);
	}

	@Override
	public List<Abono> buscarPorPrestamosOrderByAbono(List<Prestamo> prestamos) {
		return repo.findByPrestamoIn(prestamos);
	}

	@Override
	public List<Abono> buscarPorPrestamosFecha(List<Prestamo> prestamos, Date fecha) {
		return repo.findByPrestamoInAndFecha(prestamos, fecha);
	}

	@Override
	public List<Abono> buscarPorFecha(Date fecha) {
		return repo.findByFecha(fecha);
	}

	@Override
	public List<Abono> buscarPorPrestamos(List<Prestamo> prestamos) {
		return repo.findByPrestamoIn(prestamos);
	}

	@Override
	public List<Abono> buscarPorPrestamosUsuario(List<Prestamo> prestamos, Usuario usuarioTemp) {
		return repo.findByPrestamoInAndUsuario(prestamos, usuarioTemp);
	}

	@Override
	public List<Abono> buscarPorClienteEmpresa(Cliente cliente, Empresa empresa) {
		return repo.findByClienteAndEmpresa(cliente, empresa);
	}

	@Override
	public List<Abono> buscarPorClienteEmpresaFechas(Cliente cliente, Empresa empresa, Date desde, Date hasta) {
		return repo.findByClienteAndEmpresaAndFechaBetween(cliente, empresa, desde, hasta);
	}

	@Override
	public List<Abono> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}

	@Override
	public List<Abono> buscarPorEmpresaFechas(Empresa empresa, Date desde, Date hasta) {
		return repo.findByEmpresaAndFechaBetween(empresa, desde, hasta);
	}

}
