package com.contable.service.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;
import com.contable.repository.PrestamosRepository;
import com.contable.service.IPrestamosService;

@Service
public class PrestamosServiceJpa implements IPrestamosService{
	
	@Autowired
	private PrestamosRepository repo;

	@Override
	public Prestamo buscarPorId(Integer id) {
		Optional<Prestamo> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Prestamo> buscarPorEstado(Integer estado) {
		return repo.findByEstado(estado);
	}

	@Override
	public List<Prestamo> buscarPorCliente(Cliente cliente) {
		return repo.findByCliente(cliente);
	}

	@Override
	public List<Prestamo> buscarPorFecha(Date fecha) {
		return repo.findByFecha(fecha);
	}

	@Override
	public void guardar(Prestamo prestamo) {
		repo.save(prestamo);
	}

	@Override
	public void eliminar(Integer id) {
		Optional<Prestamo> optional = repo.findById(id);
		if(optional.isPresent()) {
			repo.delete(optional.get());
		}
	}

	@Override
	public List<Prestamo> buscarPorCarpeta(Carpeta carpeta) {
		return repo.findByCarpeta(carpeta);
	}

	@Override
	public List<Prestamo> buscarPorClienteCarpetaPorFechaDesc(Cliente cliente, Carpeta carpeta) {
		return repo.findByClienteAndCarpetaOrderByFechaDesc(cliente, carpeta);
	}

	@Override
	public List<Prestamo> buscarPorTipo(String tipo) {
		return repo.findByTipo(tipo);
	}

	@Override
	public List<Prestamo> buscarPorCarpetaFechaDesc(Carpeta carpeta) {
		return repo.findByCarpetaOrderByFechaDesc(carpeta);
	}

	@Override
	public List<Prestamo> buscarPorCarpetaFecha(Carpeta carpeta, Date date) {
		return repo.findByCarpetaAndFecha(carpeta, date);
	}

	@Override
	public List<Prestamo> buscarPorClienteCarpetaEmpresaPorFechaDesc(Cliente cliente, Carpeta carpeta,
			Empresa empresa) {
		return repo.findByClienteAndCarpetaAndEmpresaOrderByFechaDesc(cliente, carpeta, empresa);
	}

	@Override
	public List<Prestamo> buscarPorClienteCarpetaEmpresaFechasPorFechaDesc(Cliente cliente, Carpeta carpeta,
			Empresa empresa, Date desde, Date hasta) {
		return repo.findByClienteAndCarpetaAndEmpresaAndFechaBetweenOrderByFechaDesc(cliente, carpeta, empresa, desde, hasta);
	}

	@Override
	public List<Prestamo> buscarPorCarpetaEmpresaFechasPorFechaDesc(Carpeta carpeta, Empresa empresa, Date desde,
			Date hasta) {
		return repo.findByCarpetaAndEmpresaAndFechaBetweenOrderByFechaDesc(carpeta, empresa, desde, hasta);
	}

	@Override
	public List<Prestamo> buscarPorCarpetaEmpresa(Carpeta carpeta, Empresa empresa) {
		return repo.findByCarpetaAndEmpresa(carpeta, empresa);
	}

}
