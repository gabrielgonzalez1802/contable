package com.contable.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contable.model.Perfil;
import com.contable.model.Usuario;
import com.contable.service.IPerfilesService;
import com.contable.service.IUsuariosService;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {
	
	@Autowired
	private IPerfilesService servicePerfiles;
	
	@Autowired
	private IUsuariosService serviceUsuarios;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/agregarUsuarios")
	public String agregarUsuarios(Model model, HttpSession session) {
		List<Perfil> perfiles = servicePerfiles.buscarTodos();
		Usuario usuario = new Usuario();
		model.addAttribute("usuario", usuario);
		model.addAttribute("perfiles", perfiles);
		return "usuarios/agregarUsuario :: agregarUsuario";
	}
	
	@GetMapping("/listaUsuarios")
	public String listaUsuarios(Model model, HttpSession session) {
		List<Perfil> perfiles = servicePerfiles.buscarTodos();
		List<Usuario> usuarios = serviceUsuarios.buscarPorEstado(1);
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("perfiles", perfiles);
		return "usuarios/listaUsuarios :: listaUsuarios";
	}
	
	@GetMapping("/modificarUsuario/{id}")
	public String formModificarUsuario(@PathVariable(name = "id") Integer id ,Model model, HttpSession session) {
		Usuario usuario = serviceUsuarios.buscarPorId(id);
		List<Perfil> perfiles = servicePerfiles.buscarTodos();
		model.addAttribute("usuario", usuario);
		model.addAttribute("perfiles", perfiles);
		return "usuarios/modificarUsuario :: modificarUsuario";
	}
	
	@GetMapping("/cambiarPassword/{id}")
	public String cambiarPassword(@PathVariable(name = "id") Integer id ,Model model, HttpSession session) {
		Usuario usuario = serviceUsuarios.buscarPorId(id);
		if(usuario!=null) {
			model.addAttribute("usuario", usuario);
			return "usuarios/cambiarPassword :: cambiarPassword";
		}
		List<Perfil> perfiles = servicePerfiles.buscarTodos();
		usuario = new Usuario();
		model.addAttribute("usuario", usuario);
		model.addAttribute("perfiles", perfiles);
		return "usuarios/agregarUsuario :: agregarUsuario";
	}
	
	@PostMapping("/crear")
	public ResponseEntity<String> crear(@ModelAttribute("usuario") Usuario usuario){
		String response = "0";
		//Verificamos que el usuario no exista
		Usuario usuarioTemp = serviceUsuarios.buscarPorUsername(usuario.getUsername());
		if(usuarioTemp != null) {
			response = "2";
		}else {
			usuario.setEstatus(1);
			usuario.setFechaRegistro(new Date());
			String pdwPlano = usuario.getPassword();
			String pwdEncriptado = passwordEncoder.encode(pdwPlano);
			usuario.setPassword(pwdEncriptado);
			serviceUsuarios.guardar(usuario);
			if(usuario.getId()>0) {
				response = "1";
			}
		}
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@PostMapping("/modificar")
	public ResponseEntity<String> modificar(@ModelAttribute("usuario") Usuario usuario){
		String response = "0";
		Usuario originalUser = serviceUsuarios.buscarPorId(usuario.getId());
		//Verificamos que el usuario no exista
		Usuario usuarioTemp = serviceUsuarios.buscarPorUsername(usuario.getUsername());
		if(usuarioTemp != null) {
			response = "2";
		}else {
			if(originalUser != null) {
				originalUser.setUsername(usuario.getUsername());
				originalUser.setNombre(usuario.getNombre());
//				usuarioTemp.setEstatus(usuario.getEstatus());	
				serviceUsuarios.guardar(originalUser);
				response = "1";
			}else {
				response = "0";
			}
		}
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@PostMapping("/modificarPassword")
	public ResponseEntity<String> modificarPassword(@ModelAttribute("usuario") Usuario usuario){
		String response = "0";
		Usuario originalUser = serviceUsuarios.buscarPorId(usuario.getId());
		//Verificamos que el usuario no exista
		Usuario usuarioTemp = serviceUsuarios.buscarPorUsername(usuario.getUsername());
		if(usuarioTemp != null) {
			response = "2";
		}else {
			if(originalUser != null) {
				String pdwPlano = usuario.getPassword();
				String pwdEncriptado = passwordEncoder.encode(pdwPlano);
				originalUser.setPassword(pwdEncriptado);
				serviceUsuarios.guardar(originalUser);
				response = "1";
			}else {
				response = "0";
			}
		}
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@GetMapping("/bcrypt/{texto}")
	@ResponseBody
	public String encriptar(@PathVariable("texto") String texto) {
		return texto + " Encriptado en Bcrypt: "+passwordEncoder.encode(texto);
	}
}