package com.contable.controller;

import java.util.Date;
import java.util.LinkedList;
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

import com.contable.model.Cliente;
import com.contable.model.ComprobanteFiscal;
import com.contable.model.Usuario;
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

	@GetMapping("/")
	public String getListaClientes(Model model) {
		List<Cliente> listaClientes = serviceClientes.buscarTodos().stream().filter(e -> e.getEstado() == 1)
				.collect(Collectors.toList());
		model.addAttribute("listaClientes", listaClientes);
		return "clientes/listaClientes :: listaCliente";
	}

	@GetMapping("/buscarCliente")
	public String formBuscarCliente(Model model) {
		List<Cliente> clietes = serviceClientes.buscarTodos().stream().filter(c -> c.getEstado() == 1)
				.collect(Collectors.toList());

		for (Cliente cliente : clietes) {
			cliente.setNombre(cliente.getNombre() + " - " + cliente.getCedula());
		}

		model.addAttribute("clientes", clietes);
		return "clientes/buscarCliente :: buscarCliente";
	}

	@PostMapping("/getInfoCliente")
	public String getInfoCliente(Model model, Integer idCliente) {
		Cliente cliente = new Cliente();
		if (idCliente > 0) {
			cliente = serviceClientes.buscarPorId(idCliente);
		}

		model.addAttribute("cliente", cliente);
		return "clientes/infoCliente :: infoCliente";
	}

	@GetMapping("/agregar")
	public String formularioCliente(Model model) {
		Cliente cliente = new Cliente();
		List<ComprobanteFiscal> comprobantesFiscales = serviceComprobantesFiscales.buscarTodos();
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
		} else {
			cliente.setUsuario(usuario);
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