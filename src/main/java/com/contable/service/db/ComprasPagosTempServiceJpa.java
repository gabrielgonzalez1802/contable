package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Compra;
import com.contable.model.CompraPagoTemp;
import com.contable.model.Empresa;
import com.contable.model.Usuario;
import com.contable.repository.ComprasPagosTempRepository;
import com.contable.service.IComprasPagosTempService;

@Service
public class ComprasPagosTempServiceJpa implements IComprasPagosTempService{

	@Autowired
	private ComprasPagosTempRepository repo;
	
	@Override
	public CompraPagoTemp buscarPorId(Integer id) {
		Optional<CompraPagoTemp> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<CompraPagoTemp> buscarPorEmpresaUsuarioCompra(Empresa empresa, Usuario usuario, Compra compra) {
		return repo.findByEmpresaAndUsuarioAndCompra(empresa, usuario, compra);
	}

	@Override
	public List<CompraPagoTemp> buscarPorEmpresaCompra(Empresa empresa, Compra compra) {
		return repo.findByEmpresaAndCompra(empresa, compra);
	}

	@Override
	public void guardar(CompraPagoTemp compraPagoTemp) {
		repo.save(compraPagoTemp);
	}

	@Override
	public void eliminar(CompraPagoTemp compraPagoTemp) {
		repo.delete(compraPagoTemp);
	}

	@Override
	public void eliminar(List<CompraPagoTemp> comprasPagosTemp) {
		repo.deleteAll(comprasPagosTemp);
	}
}
