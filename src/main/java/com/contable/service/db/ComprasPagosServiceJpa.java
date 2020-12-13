package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Compra;
import com.contable.model.CompraPago;
import com.contable.model.Empresa;
import com.contable.model.Usuario;
import com.contable.repository.ComprasPagosRepository;
import com.contable.service.IComprasPagosService;

@Service
public class ComprasPagosServiceJpa implements IComprasPagosService{

	@Autowired
	private ComprasPagosRepository repo;
	
	@Override
	public CompraPago buscarPorId(Integer id) {
		Optional<CompraPago> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<CompraPago> buscarPorEmpresaUsuarioCompra(Empresa empresa, Usuario usuario, Compra compra) {
		return repo.findByEmpresaAndUsuarioAndCompra(empresa, usuario, compra);
	}

	@Override
	public List<CompraPago> buscarPorEmpresaCompra(Empresa empresa, Compra compra) {
		return repo.findByEmpresaAndCompra(empresa, compra);
	}

	@Override
	public void guardar(CompraPago CompraPago) {
		repo.save(CompraPago);
	}

	@Override
	public void eliminar(CompraPago CompraPago) {
		repo.delete(CompraPago);
	}

	@Override
	public void eliminar(List<CompraPago> comprasPagos) {
		repo.deleteAll(comprasPagos);
	}

	@Override
	public List<CompraPago> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}
}
