package com.contable.controller;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.CompraProductoTemp;
import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.Producto;
import com.contable.model.Usuario;
import com.contable.service.IComprasProductosTempService;
import com.contable.service.ICuentasContablesService;
import com.contable.service.IProductosService;
import com.contable.util.Utileria;

@Controller
@RequestMapping("/productos")
public class ProductosController {
	
	@Value("${contable.ruta.imagenes}")
	private String ruta;
	
	@Autowired
	private IProductosService serviceProductos;
	
	@Autowired
	private ICuentasContablesService serviceCuentasContables;
	
	@Autowired
	private IComprasProductosTempService serviceComprasProductosTemp;

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
		
		producto.setCantidad(originalProducto.getCantidad());
		
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
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Producto producto = serviceProductos.buscarPorId(id);
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.buscarPorEmpresaTipo(empresa, "A");			
		model.addAttribute("cuentasContablesAuxiliares", cuentasContablesAuxiliares);
		model.addAttribute("producto", producto);
		return "inventario/productos :: #formUpdateProduct";
	}

	@GetMapping("/listaProductos")
	public String listaProductos(Model model, HttpSession session, Pageable pageable) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		double totalCostos = 0;
		Page<Producto> productos = serviceProductos.buscarPorEmpresa(empresa, pageable);
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.buscarPorEmpresaTipo(empresa, "A");	
		for (Producto producto : productos) {
			totalCostos+= producto.getCosto()*producto.getCantidad();
		}
		model.addAttribute("totalCostos",  formato2d(totalCostos));
		model.addAttribute("productos", productos);
		model.addAttribute("producto", new Producto());
		model.addAttribute("cuentasContablesAuxiliares", cuentasContablesAuxiliares);
		return "inventario/productos :: listaProductos";
	}
	
	@GetMapping("/listaProductosFragment")
	public String listaProductosFragment(Model model, HttpSession session, Pageable pageable) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		double totalCostos = 0;
		Page<Producto> productos = serviceProductos.buscarPorEmpresa(empresa, pageable);
		model.addAttribute("productos", productos);
		for (Producto producto : productos) {
			totalCostos+= producto.getCosto()*producto.getCantidad();
		}
		model.addAttribute("totalCostos",  formato2d(totalCostos));
		return "inventario/productos :: #tablaProductos";
	}
	
	@PostMapping("/listaProductosFragment")
	public String listaProductosFragment(Model model, HttpSession session, Pageable pageable, String nombre, String tipo) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		double totalCostos = 0;
		Page<Producto> productos = null;
		if(tipo.equals("")) {
			productos = serviceProductos.buscarPorEmpresaContainingOrderByNombre(empresa, nombre, pageable);
		}else if(tipo.equals("1")){
			productos = serviceProductos.buscarPorEmpresaActivoFijoContainingNombre(empresa, 1, nombre, pageable);
		}else if(tipo.equals("2")) {
			productos = serviceProductos.buscarPorEmpresaActivoFijoContainingNombre(empresa, 2, nombre, pageable);
		}
		for (Producto producto : productos) {
			totalCostos+= producto.getCosto()*producto.getCantidad();
		}
		model.addAttribute("totalCostos",  formato2d(totalCostos));
		model.addAttribute("productos", productos);
		return "inventario/productos :: #tablaProductos";
	}
	
	@PostMapping("/listaProductosFragmentFindByAndPagination")
	public String listaProductosFragmentFindByAndPagination(Model model, HttpSession session, Pageable pageable,
			String nombre, String tipo) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Page<Producto> productos = null;
		double totalCostos= 0;
		if(tipo.equals("")) {
			productos = serviceProductos.buscarPorEmpresaContainingOrderByNombre(empresa, nombre, pageable);
		}else if(tipo.equals("1")){
			productos = serviceProductos.buscarPorEmpresaActivoFijoContainingNombre(empresa, 1, nombre, pageable);
		}else if(tipo.equals("2")) {
			productos = serviceProductos.buscarPorEmpresaActivoFijoContainingNombre(empresa, 2, nombre, pageable);
		}
		for (Producto producto : productos) {
			totalCostos+= producto.getCosto()*producto.getCantidad();
		}
		model.addAttribute("totalCostos",  formato2d(totalCostos));
		model.addAttribute("productos", productos);
		return "inventario/productos :: #tablaProductos";
	}
	
	@GetMapping("/getImagen/{id}")
	public String getImagen(@PathVariable("id") Integer id, Model model) {
		Producto producto = serviceProductos.buscarPorId(id);
		model.addAttribute("imagenProducto", producto.getImagen());
		return "inventario/productos :: #imagenProducto";
	}
	
	@PostMapping("/modificarCantidad")
	public ResponseEntity<String> modificarCantidad(Integer id, String tipo, Integer cantidad){
		Producto producto = serviceProductos.buscarPorId(id);
		if(tipo.equalsIgnoreCase("entrada")) {
			producto.setCantidad(producto.getCantidad()+cantidad);
		}else {
			producto.setCantidad(producto.getCantidad()-cantidad);
		}
		serviceProductos.guardar(producto);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	@GetMapping("/deleteProductAndListFragment/{id}")
	public String deleteProductAndListFragment(Model model, HttpSession session, @PathVariable("id") Integer id, Pageable pageable) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Producto producto = serviceProductos.buscarPorId(id);
		double totalCostos = 0;
		serviceProductos.eliminar(producto);
		Page<Producto> productos = serviceProductos.buscarPorEmpresa(empresa, pageable);
		model.addAttribute("productos", productos);
		for (Producto productoTemp : productos) {
			totalCostos+= productoTemp.getCosto()*productoTemp.getCantidad();
		}
		model.addAttribute("totalCostos",  formato2d(totalCostos));
		return "inventario/productos :: #tablaProductos";
	}
	
	@GetMapping("/miniInfoProduct/{id}")
	public String miniInfoProduct(Model model, HttpSession session, @PathVariable("id") Integer id) {
		Producto producto = serviceProductos.buscarPorId(id);
		model.addAttribute("producto", producto);
		return "inventario/productos :: #miniInfoProducto";
	}
	
	@PostMapping("/addProductosTempForCompras")
	public String addProductosTempForCompras(Model model, HttpSession session,
			Integer idProducto, Integer cantidad, Double costo, String txt) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Producto producto = serviceProductos.buscarPorId(idProducto);
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		CompraProductoTemp compraProductoTemp = new CompraProductoTemp();
		compraProductoTemp.setCantidad(cantidad);
		compraProductoTemp.setCosto(costo);
		compraProductoTemp.setEmpresa(empresa);
		compraProductoTemp.setProducto(producto);
		compraProductoTemp.setUsuario(usuario);
		compraProductoTemp.setInfo(producto.getNombre()+" - "+txt);
		compraProductoTemp.setCuentaContable(producto.getCuentaContable());
		compraProductoTemp.setTotal(formato2d(cantidad*costo));
		serviceComprasProductosTemp.guardar(compraProductoTemp);
		double totalProductoTemp = 0;
		List<CompraProductoTemp> productosTemp = serviceComprasProductosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		for (CompraProductoTemp compraProducto : productosTemp) {
			totalProductoTemp+=formato2d(compraProducto.getCantidad()*compraProducto.getCosto());
		}
		model.addAttribute("totalProductoTemp" , formato2d(totalProductoTemp));
		model.addAttribute("productosTemps", productosTemp);
		return "contabilidad/compras :: #tablaCompras";
	}
	
	@GetMapping("/reloadProductsCompra/{tipos}")
	public String reloadProductsCompra(Model model, HttpSession session, @PathVariable("tipos") Integer tipos) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<Integer> activosFijos = new LinkedList<>();
		
		if(tipos.intValue()==1) {
			activosFijos.add(1);
		}else if(tipos.intValue()==2) {
			activosFijos.add(2);
		}else if(tipos.intValue()==3) {
			activosFijos.add(3);
		}else if(tipos.intValue()==12) {
			activosFijos.add(1);
			activosFijos.add(2);
		}else if(tipos.intValue()==13) {
			activosFijos.add(1);
			activosFijos.add(3);
		}else if(tipos.intValue()==23) {
			activosFijos.add(2);
			activosFijos.add(3);
		}
		
		List<Producto> productos = serviceProductos.buscarPorEmpresaActivosFijos(empresa, activosFijos);
		model.addAttribute("productos", productos);
		return "contabilidad/compras :: #productosSelectForCompra";
	}
	
	@GetMapping("/deleteProductTemp/{id}")
	public String deleteProductTemp(Model model, HttpSession session, @PathVariable("id") Integer id) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		CompraProductoTemp productoTemp = serviceComprasProductosTemp.buscarPorId(id);
		serviceComprasProductosTemp.eliminar(productoTemp);
		List<CompraProductoTemp> productosTemp = serviceComprasProductosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		model.addAttribute("productosTemps", productosTemp);
		return "contabilidad/compras :: #tablaCompras";
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}

}
