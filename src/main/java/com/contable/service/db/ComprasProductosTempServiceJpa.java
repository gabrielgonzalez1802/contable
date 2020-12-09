package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.CompraProductoTemp;
import com.contable.model.Empresa;
import com.contable.model.Producto;
import com.contable.model.Usuario;
import com.contable.repository.ComprasProductosTempRepository;
import com.contable.service.IComprasProductosTempService;

@Service
public class ComprasProductosTempServiceJpa implements IComprasProductosTempService{
	
	@Autowired
	private ComprasProductosTempRepository repo;

	@Override
	public CompraProductoTemp buscarPorId(Integer id) {
		Optional<CompraProductoTemp> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<CompraProductoTemp> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario) {
		return repo.findByEmpresaAndUsuario(empresa, usuario);
	}

	@Override
	public List<CompraProductoTemp> buscarPorEmpresaUsuarioProducto(Empresa empresa, Usuario usuario,
			Producto producto) {
		return repo.findByEmpresaAndUsuarioAndProducto(empresa, usuario, producto);
	}

	@Override
	public List<CompraProductoTemp> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}

	@Override
	public void guardar(CompraProductoTemp compraProductoTemp) {
		repo.save(compraProductoTemp);
	}

	@Override
	public void eliminar(CompraProductoTemp compraProductoTemp) {
		repo.delete(compraProductoTemp);
	}

	@Override
	public void eliminar(List<CompraProductoTemp> comprasProductosTemp) {
		repo.deleteAll(comprasProductosTemp);
	}

}
