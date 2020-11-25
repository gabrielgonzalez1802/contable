package com.contable.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.Carpeta;
import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.service.ICarpetasService;
import com.contable.service.ICuentasContablesService;
import com.contable.service.IEntradasDiariosService;

@Controller
@RequestMapping("/cuentasContables")
public class CuentasContablesController {
	
	@Autowired
	private ICuentasContablesService serviceCuentasContables;
	
	@Autowired
	private IEntradasDiariosService serviceEntradasDiario;
	
	@Autowired
	private ICarpetasService serviceCarpetas;

	@PostMapping("/crear")
	public ResponseEntity<String> crear(Model model, HttpSession session, 
			String tipoCuenta, String grupoCuenta, String codigoCuenta, String nombreCuenta,
			String cuentaControl, Integer idCuentaControl){
		String response = "0";
		
		if(codigoCuenta.length()>1) {		
			
			if(idCuentaControl == 0) {
				return new ResponseEntity<String>("-3", HttpStatus.ACCEPTED);
			}
			
			String temporalCode = codigoCuenta.substring(0,1);
			//Verificamos que exista el codigo princial
			List<CuentaContable> temporalCuentaContable = serviceCuentasContables.buscarPorEmpresaCodigo((Empresa) session.getAttribute("empresa"), temporalCode);
			if(temporalCuentaContable.isEmpty()) {
				response = "-1";
				return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
			}
		}
		
		CuentaContable cuentaContable = new CuentaContable();
		cuentaContable.setCodigo(codigoCuenta);
		cuentaContable.setTipo(tipoCuenta);
		cuentaContable.setEmpresa((Empresa) session.getAttribute("empresa"));
		cuentaContable.setGrupoCuenta(grupoCuenta);
		cuentaContable.setNombreCuenta(nombreCuenta);
		
		if(cuentaControl.length()>0 && idCuentaControl!=0) {
			CuentaContable cuentaTemp = serviceCuentasContables.buscarPorId(idCuentaControl);
			if(cuentaTemp!=null) {
				cuentaContable.setCuentaControl(cuentaTemp.getCodigo());
				cuentaContable.setIdCuentaControl(idCuentaControl);
				if(cuentaTemp.getTipo().equalsIgnoreCase("A")) {
					//Si la cuenta control es auxiliar no puede guardar
					return new ResponseEntity<String>("-2", HttpStatus.ACCEPTED);
				}
			}
		}
		
		serviceCuentasContables.guardar(cuentaContable);
		
		if(cuentaContable.getId()!=null) {
			response = "1";
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/iniciarContabilidad")
	public ResponseEntity<String> iniciarContabilidad(Model model, HttpSession session,
			Integer idCuentaContable, Integer tipo, Double monto){
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(idCuentaContable);
		EntradaDiario entradaDiario = new EntradaDiario();
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		
		Carpeta carpeta = null;
		
		if(session.getAttribute("carpeta") != null) {
			 carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
		
		String response = "0";
		if(tipo == 1) {
			entradaDiario.setDebito(new BigDecimal(monto));
		}else if(tipo == 2){
			entradaDiario.setCredito(new BigDecimal(monto));
		}
		entradaDiario.setCuentaContable(cuentaContable);
		entradaDiario.setDetalle("inicio contable");
		entradaDiario.setEmpresa(empresa);
		entradaDiario.setCarpeta(carpeta);
		serviceEntradasDiario.guardar(entradaDiario);
		
		if(entradaDiario.getId()!=null) {
			response = "1";
			cuentaContable.setEstado(1);
			serviceCuentasContables.guardar(cuentaContable);
		}
		
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/cuentasContablesAuxiliar")
	public String cuentasContablesAuxiliar(Model model, HttpSession session){
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.buscarPorEmpresaTipoEstado((Empresa) session.getAttribute("empresa"), "A", 0);
		model.addAttribute("cuentasContablesAuxiliares", cuentasContablesAuxiliares);
		return "contabilidad/contabilidad :: #cuentaContableAuxiliar";
	}
	
	@PostMapping("/modificar")
	public ResponseEntity<String> modificar(Model model, HttpSession session, 
			String tipoCuenta, String grupoCuenta, String codigoCuenta, String nombreCuenta,
			String cuentaControl, Integer idCuentaControl, Integer id){
		
		String response = "0";
		
		if(idCuentaControl == 0) {
			return new ResponseEntity<String>("-3", HttpStatus.ACCEPTED);
		}
		
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(id);
				
		if(codigoCuenta.length()>1) {		
			String temporalCode = codigoCuenta.substring(0,1);
			//Verificamos que exista el codigo princial
			List<CuentaContable> temporalCuentaContable = serviceCuentasContables.buscarPorEmpresaCodigo((Empresa) session.getAttribute("empresa"), temporalCode);
			if(temporalCuentaContable.isEmpty()) {
				response = "-1";
				return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
			}
		}
		
		cuentaContable.setCodigo(codigoCuenta);
		cuentaContable.setTipo(tipoCuenta);
		cuentaContable.setEmpresa((Empresa) session.getAttribute("empresa"));
		cuentaContable.setGrupoCuenta(grupoCuenta);
		cuentaContable.setNombreCuenta(nombreCuenta);
		
		if(cuentaControl.length()>0 && idCuentaControl!=0) {
			CuentaContable cuentaTemp = serviceCuentasContables.buscarPorId(idCuentaControl);
			if(cuentaTemp!=null) {
				cuentaContable.setCuentaControl(cuentaTemp.getCodigo());
				cuentaContable.setIdCuentaControl(idCuentaControl);
				if(cuentaTemp.getTipo().equalsIgnoreCase("A")) {
					//Si la cuenta control es auxiliar no puede guardar
					return new ResponseEntity<String>("-2", HttpStatus.ACCEPTED);
				}
			}
		}
		
		serviceCuentasContables.guardar(cuentaContable);
		
		if(cuentaContable.getId()!=null) {
			response = "1";
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/modificarCuentaContable/{id}")
	public String modificarCuentaContable(Model model, @PathVariable("id") Integer id) {
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(id);
		model.addAttribute("cuentaContable", cuentaContable);
		return "contabilidad/contabilidad :: #editCuentaContable";
	}
	
	@GetMapping("/mostrarCuentasContables")
	public String mostrarCuentasContables(Model model, HttpSession session) {
		List<CuentaContable> cuentasContables = serviceCuentasContables.buscarPorEmpresaOrderByCodigoAsc((Empresa) session.getAttribute("empresa"));
		for (CuentaContable cuentaContable : cuentasContables) {
			if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("C")) {
				cuentaContable.setClase("ccpadre");
			}else if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("C")) {
				
			}
		}
		model.addAttribute("cuentasContables", cuentasContables);
		return "contabilidad/contabilidad :: #tablaCuentasContables";
	}
	
	@GetMapping("/imprimirCuentasContables")
	public String imprimirCuentasContables(Model model, HttpSession session) {
		List<CuentaContable> cuentasContables = serviceCuentasContables.buscarPorEmpresaOrderByCodigoAsc((Empresa) session.getAttribute("empresa"));
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		model.addAttribute("cuentasContables", cuentasContables);
		model.addAttribute("empresa", empresa);
		return "impresiones/contabilidad/contabilidad :: #testCarga";
	}
	
	@PostMapping("/eliminar")
	public ResponseEntity<String> eliminar(Integer idCuentaContable){
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(idCuentaContable);
		serviceCuentasContables.eliminar(cuentaContable);
		return new ResponseEntity<String>("1", HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/buscarCuentaControl/{codigoCuenta}")
	public ResponseEntity<String[]> buscarCuentaControl(Model model, HttpSession session,
			@PathVariable("codigoCuenta") String codigoCuenta) {
		String response[] = {"0","0"};
		String tempCode = "";
		if(codigoCuenta.contains("-")) {
			//Obtenemos la posicion del ultimo -
			 int position = codigoCuenta.lastIndexOf("-");
			 tempCode = codigoCuenta.substring(0, position);
		}else {
			int longitud = codigoCuenta.length()-1;
			tempCode = codigoCuenta.substring(0, longitud);
		}
		
		List<CuentaContable> cuentaContables = serviceCuentasContables.buscarPorEmpresaCodigo((Empresa) session.getAttribute("empresa"), tempCode);
		if(!cuentaContables.isEmpty()) {
			response[0] = cuentaContables.get(0).getNombreCuenta();
			response[1] = cuentaContables.get(0).getId().toString();
		}
	
		return new ResponseEntity<String[]>(response, HttpStatus.ACCEPTED);
	}

}
