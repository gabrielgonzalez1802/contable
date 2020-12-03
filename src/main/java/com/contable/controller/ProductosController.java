package com.contable.controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Empresa;
import com.contable.model.Producto;
import com.contable.service.IProductosService;
import com.contable.util.Utileria;

@Controller
@RequestMapping("/productos")
public class ProductosController {
	
	@Value("${contable.ruta.imagenes}")
	private String ruta;
	
	@Autowired
	private IProductosService serviceProductos;

	@PostMapping("/crear")
	public ResponseEntity<String> guardarProducto(Model model, Producto producto, HttpSession session){
		String response = "0";
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		producto.setEmpresa(empresa);
		if(!producto.getImagenTemp().isEmpty()) {
			String nombreImagen = Utileria.guardarArchivo(producto.getImagenTemp(), ruta);
			if (nombreImagen != null) {
				producto.setImagen(nombreImagen);
			}
		}
		serviceProductos.guardar(producto);
		if(producto.getId()!=null) {
			response = "1";
		}else {
			File imageFile = new File(ruta+ producto.getImagen());
			imageFile.delete();
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/modificar")
	public ResponseEntity<String> modificarProducto(Model model, Producto producto, HttpSession session){
		String response = "0";
		Producto originalProducto = serviceProductos.buscarPorId(producto.getId());
		producto.setEmpresa(originalProducto.getEmpresa());

		if(!producto.getImagenTemp().isEmpty()) {
			//verificamos si el registro poseia foto
			if(originalProducto.getImagen()!=null) {
				File imageFile = new File(ruta+ originalProducto.getImagen());
				imageFile.delete();
			}
			
			String nombreImagen = Utileria.guardarArchivo(producto.getImagenTemp(), ruta);
			if (nombreImagen != null) {
				producto.setImagen(nombreImagen);
			}
		}else {
			producto.setImagen(originalProducto.getImagen());
		}
		
		serviceProductos.guardar(producto);
		if(producto.getId()!=null) {
			response = "1";
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/eliminarProducto")
	public ResponseEntity<String> eliminarProducto(Model model, Integer idProducto, HttpSession session){
		String response = "0";
		Producto producto = serviceProductos.buscarPorId(idProducto);

		//verificamos si el registro poseia foto
		if(producto.getImagen()!=null) {
			File imageFile = new File(ruta+ producto.getImagen());
			imageFile.delete();
		}
		
		serviceProductos.eliminar(producto);
		
		Producto productoTemp = serviceProductos.buscarPorId(idProducto);
		
		if(productoTemp==null) {
			response = "1";
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/modificarProducto/{id}")
	public String modificarProducto(Model model, HttpSession session, @PathVariable("id") Integer id) {
		Producto producto = serviceProductos.buscarPorId(id);
		model.addAttribute("producto", producto);
		return "contabilidad/contabilidad :: #formUpdateProduct";
	}

	
	@GetMapping("/listaProductos")
	public String listaProductos(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<Producto> productos = serviceProductos.buscarPorEmpresa(empresa);
		model.addAttribute("productos", productos);
		return "contabilidad/contabilidad :: #tablaProductos";
	}
	
}
