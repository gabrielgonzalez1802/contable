package com.contable.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.ComprobanteFiscal;
import com.contable.model.Usuario;
import com.contable.service.ICarpetasService;
import com.contable.service.IClientesService;
import com.contable.service.IComprobantesFiscalesService;
import com.contable.util.Utileria;

@Controller
@RequestMapping("/clientes")
public class ClientesController {

	@Value("${contable.ruta.imagenes}")
	private String ruta;

	@Autowired
	private IClientesService serviceClientes;

	@Autowired
	private IComprobantesFiscalesService serviceComprobantesFiscales;
	
	@Autowired
	private ICarpetasService serviceCarpetas;

	@GetMapping("/")
	public String getListaClientes(Model model) {
		List<Cliente> listaClientes = serviceClientes.buscarTodos().stream().filter(e -> e.getEstado() == 1)
				.collect(Collectors.toList());
		model.addAttribute("listaClientes", listaClientes);
		return "clientes/listaClientes :: listaCliente";
	}

	@GetMapping("/buscarCliente")
	public String formBuscarCliente(Model model, HttpSession session) {
		//Dejamos el cliente vacio
		session.setAttribute("cliente", 0);
		//Verificamos si tenemos la carpeta en sesion para seleccionarla
		if(session.getAttribute("carpeta")==null) {
			session.setAttribute("carpeta", 1);
		}
		Integer idCarpeta = (Integer) session.getAttribute("carpeta");
		if(idCarpeta!=null) {
			model.addAttribute("carpeta", serviceCarpetas.buscarPorId(idCarpeta));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpeta(1);
			model.addAttribute("carpeta", carpetas.get(0));
			session.setAttribute("carpeta", carpetas.get(0).getId());
		}
		model.addAttribute("msg", "0");
		return "clientes/buscarCliente :: buscarCliente";
	}
	
	@GetMapping("/buscarClienteCarpetaPrincipal")
	public String formBuscarClienteCarpetaPrincipal(Model model, HttpSession session) {
		List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpeta(1);
		model.addAttribute("carpeta", carpetas.get(0));
		session.setAttribute("carpeta", carpetas.get(0).getId());
		model.addAttribute("msg", "0");
		return "clientes/buscarCliente :: buscarCliente";
	}

	@PostMapping("/getInfoCliente")
	public String getInfoCliente(Model model, Integer carpeta, String tipoDocumento, String item, HttpSession session) {
		Cliente cliente = new Cliente();
//		Integer idCarpeta = (Integer) session.getAttribute("carpeta");
		Carpeta carpetaTemp = serviceCarpetas.buscarPorId(carpeta);
		session.setAttribute("carpeta", carpetaTemp.getId());
		model.addAttribute("carpeta", carpetaTemp);
		if (tipoDocumento.equals("cedula")) {
			cliente = serviceClientes.buscarPorCedula(item);
			if(cliente != null) {
				session.setAttribute("cliente", cliente.getId());
			}
		}else if(tipoDocumento.equals("otro")){
			cliente = serviceClientes.buscarPorOtro(item);
			if(cliente != null) {
				session.setAttribute("cliente", cliente.getId());
			}
		}else {
			//busqueda por nombre
			List<Cliente> clientes = serviceClientes.buscarPorNombre(item).stream().
					filter(c -> c.getEstado() == 1).collect(Collectors.toList());
			if(clientes.isEmpty()) {
				cliente = null;
			}else {
				if(clientes.size()>1) {
					model.addAttribute("clientes", clientes);
					return "clientes/infoCliente :: infoClienteLista";
				}else {
					cliente = clientes.get(0);
					if(cliente != null) {
						session.setAttribute("cliente", cliente.getId());
					}
				}
			}
		}
		
		if(cliente == null) {
			session.setAttribute("cliente", 0);
			List<Cliente> clientes = serviceClientes.buscarTodos().stream().filter(c -> c.getEstado() == 1)
					.collect(Collectors.toList());
			for (Cliente clienteTemp : clientes) {
				clienteTemp.setNombre(clienteTemp.getNombre() + " - " + clienteTemp.getCedula());
			}
			
			model.addAttribute("msg", "No se encontro el cliente");
			model.addAttribute("carpeta",carpetaTemp);
			
			return "clientes/buscarCliente :: buscarCliente"; 
		}

		model.addAttribute("cliente", cliente);
		return "clientes/infoCliente :: infoCliente";
	}
	
	@GetMapping("/getInfoCliente/{id}")
	public String getInfoCliente(Model model, @PathVariable("id") Integer id, HttpSession session) {
		Cliente cliente = serviceClientes.buscarPorId(id);
		Integer idCarpeta = (Integer) session.getAttribute("carpeta");
		Carpeta carpeta = serviceCarpetas.buscarPorId(idCarpeta);
		model.addAttribute("carpeta", carpeta);
		if(cliente != null) {
			session.setAttribute("cliente", cliente.getId());
			model.addAttribute("cliente", cliente);
			return "clientes/infoCliente :: infoCliente";
		}else {
			session.setAttribute("cliente", 0);
		}
		model.addAttribute("msg", "No se encontro el cliente");
		return "clientes/buscarCliente :: buscarCliente"; 
	}

	@GetMapping("/agregar")
	public String formularioCliente(Model model) {
		Cliente cliente = new Cliente();
		List<ComprobanteFiscal> comprobantesFiscales = serviceComprobantesFiscales.buscarTodos();
		cliente.setDoctypeTemp("cedula");
		model.addAttribute("cliente", cliente);
		model.addAttribute("comprobantesFiscales", comprobantesFiscales);
		return "clientes/formularioCliente :: formularioCliente";
	}

	@GetMapping("/modificar/{id}")
	public String formularioModificarCliente(Model model, @PathVariable(name = "id") Integer id) {
		Cliente cliente = serviceClientes.buscarPorId(id);
		if (cliente != null) {
			model.addAttribute("cliente", cliente);
		} else {
			model.addAttribute("cliente", new Cliente());
		}
		return "clientes/form :: form";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminarCliente(Model model, @PathVariable(name = "id") Integer id, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Cliente cliente = serviceClientes.buscarPorId(id);
		cliente.setEliminado(new Date());
		cliente.setEstado(0);
		cliente.setUsuarioEliminado(usuario);
		serviceClientes.guardar(cliente);
		List<Cliente> listaClientes = serviceClientes.buscarTodos().stream().filter(e -> e.getEstado() == 1)
				.collect(Collectors.toList());
		model.addAttribute("listaClientes", listaClientes);
		return "clientes/listaClientes :: listaCliente";
	}

	@PostMapping("/guardar")
	@ResponseBody
	public ResponseEntity<String> guardar(Model model, @ModelAttribute("cliente") Cliente cliente,
			HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		String response = "INSERT";
		
		if (cliente.getId() != null) {
			response = "UPDATE";
			Cliente originalCliente = serviceClientes.buscarPorId(cliente.getId());
			cliente.setModificado(new Date());
			cliente.setUsuario(originalCliente.getUsuario());
			cliente.setUsuario_modificado(usuario);
			cliente.setCreado(originalCliente.getCreado());
			cliente.setFotoFrontal(originalCliente.getFotoFrontal());
			cliente.setFotoTrasera(originalCliente.getFotoTrasera());
			if(cliente.getComprobanteFiscal().getId() == 0) {
				cliente.setComprobanteFiscal(null);
			}
		} else {
			cliente.setUsuario(usuario);
			if(cliente.getComprobanteFiscal().getId() == 0) {
				cliente.setComprobanteFiscal(null);
			}
		}

		// verificamos la imagen frontal
		if (!cliente.getFrontal().isEmpty()) {
			String nombreImagenFrente = Utileria.guardarArchivo(cliente.getFrontal(), ruta);
			if (nombreImagenFrente != null) { // La imagen si se subio
				// Procesamos la variable nombreImagen
				cliente.setFotoFrontal(nombreImagenFrente);
			}
		}

		// verificamos la imagen trasera
		if (!cliente.getTrasera().isEmpty()) {
			String nombreImagenTrasera = Utileria.guardarArchivo(cliente.getTrasera(), ruta);
			if (nombreImagenTrasera != null) { // La imagen si se subio
				// Procesamos la variable nombreImagen
				cliente.setFotoTrasera(nombreImagenTrasera);
			}
		}

		cliente.setTipoDocumento(cliente.getDoctypeTemp());

		serviceClientes.guardar(cliente);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}