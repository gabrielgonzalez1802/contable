package com.contable.service.db;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.DescuentoDetalle;
import com.contable.model.Empresa;
import com.contable.model.Prestamo;
import com.contable.repository.DescuentosDetallesRepository;
import com.contable.service.IDescuentosDetallesService;

@Service
public class DescuentosDetallesServiceJpa implements IDescuentosDetallesService {

	@Autowired
	private DescuentosDetallesRepository repo;
	
	@Override
	public DescuentoDetalle buscarPorId(Integer id) {
		Optional<DescuentoDetalle> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<DescuentoDetalle> buscarPorEmpresaCarpeta(Empresa empresa, Carpeta carpeta) {
		return repo.findByEmpresaAndCarpeta(empresa, carpeta);
	}

	@Override
	public List<DescuentoDetalle> buscarPorEmpresaCarpetaCliente(Empresa empresa, Carpeta carpeta, Cliente cliente) {
		return repo.findByEmpresaAndCarpetaAndCliente(empresa, carpeta, cliente);
	}

	@Override
	public List<DescuentoDetalle> buscarPorEmpresaCarpetaPrestamos(Empresa empresa, Carpeta carpeta,
			LinkedList<Prestamo> prestamos) {
		return repo.findByEmpresaAndCarpetaAndPrestamoIn(empresa, carpeta, prestamos);
	}

	@Override
	public void guardar(DescuentoDetalle descuentoDetalle) {
		repo.save(descuentoDetalle);
	}

	@Override
	public void eliminar(DescuentoDetalle descuentoDetalle) {
		repo.delete(descuentoDetalle);
	}

	@Override
	public List<DescuentoDetalle> buscarPorEmpresaCarpetaFechas(Empresa empresa, Carpeta carpeta, Date desde,
			Date hasta) {
		return repo.findByEmpresaAndCarpetaAndFechaBetween(empresa, carpeta, desde, hasta);
	}

}
